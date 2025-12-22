package dev.qheilmann.vanillaenoughitems.commands.arguments;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
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

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.RecipeIndexReader;
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
public class RecipeItemArgument extends CustomArgument<ItemStack, NamespacedKey> {

    // Cache for suggestions per context
    // Uses reference equality (identity) since RecipeGuiContext instances are typically singletons
    private static final Map<RecipeGuiContext, Collection<String>> suggestionsCache = new ConcurrentHashMap<>();

    public RecipeItemArgument(String nodeName, RecipeGuiContext context) {
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
            // ItemStack customItem = CustomItemRegistry.getItemByKey(key);
            // TODO impl custom item registry

            throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("No item found for key: " + arg), info);
        });

        // Default suggestions: all registered item keys
        replaceSuggestions(argumentSuggestions(context));
    }

    /**
     * Create argument suggestions for item keys based on all indexed items.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @return ArgumentSuggestions providing available item key strings
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeGuiContext context) {
        return (info, builder) -> {
            // Get cached suggestions or compute them if not cached
            Collection<String> suggestions = suggestionsCache.computeIfAbsent(context, ctx -> suggestions(ctx));

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
     * @param context The RecipeGuiContext containing the RecipeIndexReader
     * @return A CompletableFuture containing the collection of suggestions.
     */
    public static Collection<String> suggestions(RecipeGuiContext context) {
        return getAllItemKeys(context).stream()
            .map(Key::asString)
            .collect(Collectors.toSet());
    }

    /**
     * Retrieves all item keys from the indexed recipes in the given context.
     * 
     * @param context The RecipeGuiContext containing the RecipeIndexReader
     * @return A collection of all item keys.
     */
    private static Collection<Key> getAllItemKeys(RecipeGuiContext context) {
        RecipeIndexReader recipeIndex = context.getRecipeIndexReader();
        Set<ItemStack> allItems = new HashSet<>();

        allItems.addAll(recipeIndex.getAllResultItems());
        allItems.addAll(recipeIndex.getAllUsedItems());

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
        throw new NotImplementedException("Custom item registry not implemented yet");
    }

    /**
     * Determines if a suggestion should be included based on the current input in the SuggestionsBuilder.
     * 
     * @param suggestion The suggestion string to check
     * @param builder The SuggestionsBuilder containing the current input
     * @return true if the suggestion matches the current input, false otherwise
     */
    private static boolean shouldSuggest(String suggestion, String currentInputLowerCase) {
        return suggestion.toLowerCase(Locale.ROOT).contains(currentInputLowerCase);

        // TODO add a better matching algorithm
        // like contains match instead of startsWith
        // or Levenshtein distance or similar

        // also consider cachier previous input to avoid recomputing suggestions so if the input is just previous + one char recalc only the new char filtering
    }
}
