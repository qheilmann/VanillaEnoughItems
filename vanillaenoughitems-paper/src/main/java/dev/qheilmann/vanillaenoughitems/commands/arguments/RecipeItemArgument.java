package dev.qheilmann.vanillaenoughitems.commands.arguments;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.qheilmann.itemregistry.ItemRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.Arrays;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

/**
 * A custom argument for parsing and validating recipe item keys.
 * Converts input NamespacedKeys to ItemStacks, throwing exceptions if invalid 
 * It's invalid if they don't represent an item or are not found inside the recipe index.
 * Provides methods for generating suggestions based on all indexed items.
 * 
 * @see ItemStack
 * @see CustomArgument
 */
@SuppressWarnings("java:S110") // Inheritance depth from CommandAPI's CustomArgument
@NullMarked
public class RecipeItemArgument extends CustomArgument<ItemStack, NamespacedKey> {

    // Cache suggestions by index+registry identity + 2sec TTL to avoid recomputing on each key press. 
    // TTL is needed because the index and registry can change on live.
    private static final Map<SuggestionCacheKey, CachedSuggestions> suggestionsCache = new ConcurrentHashMap<>();
    private static final long SUGGESTIONS_CACHE_TTL_MS = 2000;

    // Cache for unknown items, to allow the recipe item argument to parse the key to itemstack
    private static final Map<Key, ItemStack> unknownItemCache = new ConcurrentHashMap<>();

    public RecipeItemArgument(String nodeName, RecipeIndex recipeIndex, @Nullable ItemRegistry itemRegistry) {
        super(new NamespacedKeyArgument(nodeName), info -> {
            NamespacedKey key = info.currentInput();

            // First check the optional item registry for a matching item
            ItemStack registryItem = getItemByRegistry(key, itemRegistry);
            if (registryItem != null) {
                return registryItem;
            }

            // Fallback
             
            // Fallback for vanilla items
            if (key.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
                try {
                    return Bukkit.getItemFactory().createItemStack(key.asString());
                } catch (IllegalArgumentException e) {
                    // Ignore and continue to custom items
                }
            }
            
            // Fallback for custom items
            ItemStack unknownItemStack = unknownItemCache.get(key);
            if (unknownItemStack != null) {
                return unknownItemStack;
            }
            
            throw CustomArgumentHelper.minecraftLikeException(arg -> Component.text("No item found for key: " + arg), info);
        });

        // Default suggestions: all registered item keys
        replaceSuggestions(argumentSuggestions(recipeIndex, itemRegistry));
    }

    /**
     * Create argument suggestions for item keys based on all indexed items.
     *
     * @param recipeIndex the recipe index
     * @return ArgumentSuggestions providing available item key strings
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeIndex recipeIndex, @Nullable ItemRegistry itemRegistry) {
        return (info, builder) -> {
            SuggestionCacheKey cacheKey = new SuggestionCacheKey(recipeIndex, itemRegistry);
            long currentTime = System.currentTimeMillis();
            
            // Get or compute suggestions, checking TTL expiration
            Collection<String> suggestions = suggestionsCache
                .compute(cacheKey, (key, cached) -> {
                    // If no cached entry or TTL expired, recompute
                    if (cached == null || (currentTime - cached.cachedAtMs) >= SUGGESTIONS_CACHE_TTL_MS) {
                        return new CachedSuggestions(RecipeItemArgument.suggestions(recipeIndex, itemRegistry), currentTime);
                    }
                    return cached;
                })
                .suggestions();

            String currentInputLowerCase = builder.getRemainingLowerCase();
            for (String suggestion : suggestions) {
                if (shouldSuggest(suggestion, currentInputLowerCase)) {
                    builder.suggest(suggestion);
                }
            }
            return CompletableFuture.completedFuture(builder.build());
        };
    }

    /**
     * Generates a collection of suggestions for the item argument based on all indexed items.
     * 
     * @param recipeIndex The recipe index
     * @return A CompletableFuture containing the collection of suggestions.
     */
    @SuppressWarnings("null")
    public static Collection<String> suggestions(RecipeIndex recipeIndex, @Nullable ItemRegistry itemRegistry) {
        return getAllItemKeys(recipeIndex, itemRegistry).stream()
            .map(Key::asString)
            .collect(Collectors.toSet());
    }

    public static void invalidateSuggestionCache() {
        suggestionsCache.clear();
    }

    /**
     * Retrieves all item keys from the indexed recipes in the given recipe index.
     * 
     * @param recipeIndex The recipe index
     * @return A collection of all item keys.
     */
    @SuppressWarnings("null")
    private static Collection<Key> getAllItemKeys(RecipeIndex recipeIndex, @Nullable ItemRegistry itemRegistry) {
        Set<ItemStack> allItems = new HashSet<>();

        allItems.addAll(recipeIndex.getAllResultItems());
        allItems.addAll(recipeIndex.getAllIngredientItems());
        allItems.addAll(recipeIndex.getAllOtherItems());

        return allItems.stream()
            .map(itemStack -> convertItemToKey(itemStack, itemRegistry))
            .collect(Collectors.toSet());
    }

    private static Key convertItemToKey(ItemStack item, @Nullable ItemRegistry itemRegistry) {
        // First try to resolve the item back to a key using the optional item registry 
        Key registryKey = getKeyByRegistry(item, itemRegistry);
        if (registryKey != null) {
            return registryKey;
        }
        
        // Fallback

        // Fallback for vanilla items
        // We re-create ItemStack to check if this ItemStack is a non modified form of a vanilla item
        ItemStack vanillaItem = new ItemStack(item.getType());
        if (item.isSimilar(vanillaItem)) {
            // Now it's really a vanilla item

            Registry<ItemType> bukkitItemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
            ItemType itemType = item.getType().asItemType();
            Key key = bukkitItemRegistry.getKey(itemType);
            
            if (key == null) {
                throw new NotImplementedException("Could not find key for this vanilla item: " + item.getType().name());
            }

            return key;
        }

        // Fallback for custom items
        ItemType itemType = item.getType().asItemType();
        int hashCode = Arrays.hashCode(item.serializeAsBytes()); // use serialisation to generate a more deterministic hashcode
        String itemIdentifier = itemType.getKey().value() + "_0x" + Integer.toHexString(hashCode);
        Key unidentifiedKey = Key.key("unknown", itemIdentifier);
        unknownItemCache.put(unidentifiedKey, item);
        return unidentifiedKey;
    }

    /**
     * Determines if a suggestion should be included based on the current input.
     * 
     * @param suggestion The suggestion string to check
     * @param currentInputLowerCase The current user input in lowercase
     * @return true if the suggestion matches the current input, false otherwise
     */
    private static boolean shouldSuggest(String suggestion, String currentInputLowerCase) {
        // No need to call toLowerCase() on suggestion since NamespacedKeys are always lowercase
        return suggestion.contains(currentInputLowerCase);
    }

    private static @Nullable ItemStack getItemByRegistry(NamespacedKey key, @Nullable ItemRegistry itemRegistry) {
        if (itemRegistry == null) {
            return null;
        }

        return itemRegistry.createItem(key);
    }

    private static @Nullable Key getKeyByRegistry(ItemStack item, @Nullable ItemRegistry itemRegistry) {
        if (itemRegistry == null) {
            return null;
        }

        return itemRegistry.resolveKey(item);
    }

    private record CachedSuggestions(
        Collection<String> suggestions,
        long cachedAtMs
    ) {}

    private record SuggestionCacheKey(
        RecipeIndex recipeIndex,
        @Nullable ItemRegistry itemRegistry
    ) {
        @Override
        public int hashCode() {
            int result = System.identityHashCode(recipeIndex);
            result = 31 * result + System.identityHashCode(itemRegistry);
            return result;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SuggestionCacheKey(RecipeIndex otherIndex, @Nullable ItemRegistry otherItemRegistry))) {
                return false;
            }
            return recipeIndex == otherIndex && itemRegistry == otherItemRegistry;
        }
    }

}
