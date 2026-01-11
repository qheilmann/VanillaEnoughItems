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

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
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
@NullMarked
public class RecipeItemArgument extends CustomArgument<ItemStack, NamespacedKey> {

    // Cache for suggestions per recipe index
    // Uses reference equality (identity) since RecipeIndex instances are typically singletons
    private static final Map<RecipeIndex, Collection<String>> suggestionsCache = new ConcurrentHashMap<>();

    // Cache for unknown items, to allow the recipe item argument to parse the key to itemstack
    private static final Map<Key, ItemStack> unknownItemCache = new ConcurrentHashMap<>();

    public RecipeItemArgument(String nodeName, RecipeIndex recipeIndex) {
        super(new NamespacedKeyArgument(nodeName), info -> {
            NamespacedKey key = info.currentInput();

            // First check if it's a vanilla item
            if (key.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
                try {
                    return Bukkit.getItemFactory().createItemStack(key.asString());
                } catch (IllegalArgumentException e) {
                    // Ignore and continue to custom items
                }
            }

            // Then check custom items
            // [CUSTOM ITEM REGISTRY] impl custom item registry lookup here
            // ItemStack customItem = CustomItemRegistry.getItemByKey(key);

            ItemStack unknownItemStack = unknownItemCache.get(key);
            if (unknownItemStack != null) {
                return unknownItemStack;
            }
            
            throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("No item found for key: " + arg), info);
        });

        // Default suggestions: all registered item keys
        replaceSuggestions(argumentSuggestions(recipeIndex));
    }

    /**
     * Create argument suggestions for item keys based on all indexed items.
     *
     * @param recipeIndex the recipe index
     * @return ArgumentSuggestions providing available item key strings
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeIndex recipeIndex) {
        return (info, builder) -> {
            // Get cached suggestions or compute them if not cached
            Collection<String> suggestions = suggestionsCache.computeIfAbsent(recipeIndex, ctx -> suggestions(ctx));

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
    public static Collection<String> suggestions(RecipeIndex recipeIndex) {
        return getAllItemKeys(recipeIndex).stream()
            .map(Key::asString)
            .collect(Collectors.toSet());
    }

    /**
     * Retrieves all item keys from the indexed recipes in the given recipe index.
     * 
     * @param recipeIndex The recipe index
     * @return A collection of all item keys.
     */
    @SuppressWarnings("null")
    private static Collection<Key> getAllItemKeys(RecipeIndex recipeIndex) {
        Set<ItemStack> allItems = new HashSet<>();

        allItems.addAll(recipeIndex.getAllResultItems());
        allItems.addAll(recipeIndex.getAllIngredientItems());
        allItems.addAll(recipeIndex.getAllOtherItems());

        return allItems.stream()
            .map(itemStack -> convertItemToKey(itemStack))
            .collect(Collectors.toSet());
    }

    private static Key convertItemToKey(ItemStack item) {
        
        // Vanilla items 
        // We re-create ItemStack to check if this ItemStack is a non modified form of a vanilla item
        ItemStack vanillaItem = new ItemStack(item.getType());
        if (item.isSimilar(vanillaItem)) {
            // Now it's really a vanilla item

            Registry<ItemType> itemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
            ItemType itemType = item.getType().asItemType();
            Key key = itemRegistry.getKey(itemType);
            
            if (key == null) {
                throw new NotImplementedException("Could not find key for this vanilla item: " + item.getType().name());
            }

            return key;
        }

        // Custom items
        // [CUSTOM ITEM REGISTRY] impl custom item registry lookup here

        ItemType itemType = item.getType().asItemType();
        int hashCode = item.hashCode();
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
}
