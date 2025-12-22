package dev.qheilmann.vanillaenoughitems.commands.arguments;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.SearchModeArgument.SearchMode;
import dev.qheilmann.vanillaenoughitems.recipe.RecipeContext;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

/**
 * A custom argument for parsing and validating recipe IDs (check if the recipe exists).
 * Converts input strings to recipe IDs, throwing exceptions if invalid.
 * Provides methods for generating suggestions based on a recipe index, item, search mode, and process.
 * <p> Note: Not all recipes are Keyed, non-keyed recipes cannot be used with this argument.</p>
 * @see Recipe
 * @see CustomArgument
 */
@NullMarked
public class RecipeIdArgument extends CustomArgument<NamespacedKey, NamespacedKey> {
    public RecipeIdArgument(String nodeName, RecipeContext context) {
        super(new NamespacedKeyArgument(nodeName), (input) -> {
            NamespacedKey key = input.currentInput();

            // Check if the key is a valid recipe ID
            Recipe recipe = context.getRecipeIndex().getSingleRecipeByKey(key);
            if (recipe == null) {
                throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("No recipe found for ID: " + arg), input);
            }

            return key;
        });

        // Default suggestions: all recipe IDs
        replaceSuggestions(argumentSuggestions(context, null, null, null));
    }

    /**
     * Create argument suggestions for recipe IDs based on the provided item, search mode, and process.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for global index
     * @param searchMode the SearchMode to use, or null for {@link SearchModeArgument.SearchMode#DEFAULT}. Ignored if item is null
     * @param process the Process to use, or null for all processes
     * @return ArgumentSuggestions providing available recipe IDs for the item, search mode, and process
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeContext context, @Nullable ItemStack item, @Nullable SearchMode searchMode, @Nullable Process process) {
        return ArgumentSuggestions.stringCollection((info) -> suggestions(context, item, searchMode, process));
    }

    /**
     * Get available recipe IDs based on the provided item, search mode, and process.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for global index
     * @param searchMode the SearchMode to use, or null for {@link SearchModeArgument.SearchMode#DEFAULT}. Ignored if item is null
     * @param process the Process to use, or null for all processes
     * @return a collection of available recipe ID strings for the item, search mode, and process
     */
    @SuppressWarnings("null")
    public static Collection<String> suggestions(RecipeContext context, @Nullable ItemStack item, @Nullable SearchMode searchMode, @Nullable Process process) {
        Collection<Key> recipeKeys = getRecipeIds(context, item, searchMode, process);

        return recipeKeys.stream()
            .map(key -> key.asString())
            .collect(Collectors.toSet());
    }

    /**
     * Get recipe keys based on the provided item, search mode, and process.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for global index
     * @param searchMode the SearchMode to use, or null for {@link SearchModeArgument.SearchMode#DEFAULT}. Ignored if item is null
     * @param process the Process to use, or null for all processes
     * @return a collection of recipe keys for the item, search mode, and process
     */
    private static Collection<Key> getRecipeIds(RecipeContext context, @Nullable ItemStack item, @Nullable SearchMode searchMode, @Nullable Process process) {
        RecipeIndex recipeIndex = context.getRecipeIndex();
        MultiProcessRecipeReader reader;
        Collection<Key> recipeKeys;

        // Global index
        if (item == null) {
            // All processes
            if (process == null) {
                reader = recipeIndex.readerWithAllRecipes();
            } 
            // Specific process
            else {
                reader = recipeIndex.readerByProcess(process);
            }
        }
        // Item-specific index
        else {
            // Determine search mode
            if (searchMode == null) {
                searchMode = SearchModeArgument.SearchMode.DEFAULT;
            }
    
            reader = switch (searchMode) {
                case RECIPE -> recipeIndex.readerByResult(item);
                case USAGE -> recipeIndex.readerByIngredient(item);
                default -> throw new UnsupportedOperationException("Search mode " + searchMode + " is not implemented");
            };
        }

        // No results found
        if (reader == null) {
            return List.of();
        }

        // Collect results
        if (process != null) {
            if (!reader.containsProcess(process)) {
                return List.of();
            }

            reader.setCurrentProcess(process);
            recipeKeys = collectSingleProcessRecipeKeys(reader, recipeIndex.getAssociatedRecipeExtractor());
        }
        else {
            recipeKeys = collectAllProcessRecipeKeys(reader, recipeIndex.getAssociatedRecipeExtractor());
        }

        return recipeKeys;
    }

    private static Collection<Key> collectAllProcessRecipeKeys(MultiProcessRecipeReader reader, RecipeExtractor extractor) {
        NavigableSet<Process> processes = reader.getAllProcesses();
        Set<Key> recipeKeys = new HashSet<>();

        for (Process process : processes) {
            reader.setCurrentProcess(process);
            recipeKeys.addAll(collectSingleProcessRecipeKeys(reader, extractor));
        }

        return recipeKeys;
    }

    @SuppressWarnings("null")
    private static Collection<Key> collectSingleProcessRecipeKeys(MultiProcessRecipeReader reader, RecipeExtractor extractor) {
        return reader.getCurrentProcessRecipeReader().getAllRecipes().stream()
            .filter(recipe -> extractor.canHandle(recipe))
            .map(recipe -> extractor.extractKey(recipe))
            .collect(Collectors.toSet());
    }
}
