package me.qheilmann.vei.Command.CustomArguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Command.CustomArguments.SearchModeArgument.SearchMode;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import net.kyori.adventure.text.Component;

public class RecipeIdArgument extends CustomArgument<NamespacedKey, NamespacedKey> {
    public RecipeIdArgument(String nodeName, RecipeIndexService recipeIndex) {
        super(new NamespacedKeyArgument(nodeName), (input) -> {
            NamespacedKey key = input.currentInput();

            // Check if the key is a valid recipe ID
            Recipe recipe = recipeIndex.getSingleRecipeById(key);
            if (recipe == null) {
                throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("Unknown recipe ID '" + arg + "'"), input);
            }

            return key;
        });

        // Default suggestions
        this.replaceSuggestions(RecipeIdArgument.argumentSuggestionsFrom(recipeIndex, null, null, null));
    }

    /**
     * Generates a collection of suggestions for the recipe ID argument based on the provided item, search mode, and process.
     * 
     * @param recipeIndex The RecipeIndexService to use
     * @param item The item to use for generating suggestions. If null, global index is used.
     * @param searchMode The search mode to use for generating suggestions. If null, defaults to AS_RESULT. If item is null, this parameter is ignored.
     * @param process The process to use for generating suggestions. If null, all processes are used.
     * @return A CompletableFuture containing the collection of suggestions.
     */
    public static CompletableFuture<Collection<String>> suggestionsFrom(RecipeIndexService recipeIndex, ItemStack item, SearchMode searchMode, Process<?> process) {
        Objects.requireNonNull(recipeIndex, "recipeIndex cannot be null");
        
        return CompletableFuture.supplyAsync(() -> {

            // Global index case
            if (item == null) {
                if (process == null) {
                    return recipeIndex.getAllRecipeIds()
                        .stream()
                        .map(NamespacedKey::toString)
                        .toList();
                } else {
                    ProcessRecipeReader<?> processRecipeReader = recipeIndex.getByProcess(process);
                    if (processRecipeReader == null) {
                        return new ArrayList<String>();
                    }

                    Collection<String> suggestions = new ArrayList<>();
                    for (Recipe recipe : processRecipeReader.getAllRecipes()) {
                        if (recipe instanceof org.bukkit.Keyed keyedRecipe) {
                            suggestions.add(keyedRecipe.getKey().toString());
                        }
                    }
                    return suggestions;
                }
            }
            
            // SearchMode path
            SearchMode mode = searchMode;
            if (mode == null) {
                mode = SearchMode.AS_RESULT;
            }
            
            MixedProcessRecipeReader recipeReader;
            if (mode == SearchMode.AS_RESULT) {
                recipeReader = recipeIndex.getByResult(item);
            }
            else if (mode == SearchMode.AS_INGREDIENT) {
                recipeReader = recipeIndex.getByIngredient(item);
            } else {
                throw new IllegalArgumentException("Unknown search mode: " + mode);
            }

            if (recipeReader == null) {
                return new ArrayList<String>();
            }

            // Process path
            NavigableSet<Process<?>> processes;
            if (process == null) {
                processes = recipeReader.getAllProcess();
            } else {
                recipeReader.setProcess(process); // throws exception if process is not valid
                processes = new java.util.TreeSet<>();
                processes.add(recipeReader.currentProcess());
            }

            // Collecting
            Collection<String> suggestions = new ArrayList<>();
            for (Process<?> processItem : processes) {
                recipeReader.setProcess(processItem);
                @SuppressWarnings("unchecked")
                NavigableSet<Recipe> recipes = (NavigableSet<Recipe>) recipeReader.currentProcessRecipeReader().getAllRecipes();

                for (Recipe recipe : recipes) {
                    if (recipe instanceof org.bukkit.Keyed keyedRecipe) {
                        suggestions.add(keyedRecipe.getKey().toString());
                    }
                }
            }

            return suggestions;
        })
        .exceptionally((e) -> {
            VanillaEnoughItems.LOGGER.error("Error while generating suggestions for recipe ID argument", e);
            return new ArrayList<String>();
        });
    }

    /**
     * Generates argument suggestions for the recipe ID argument based on the provided item, search mode, and process.
     * 
     * @param recipeIndex The RecipeIndexService to use
     * @param item The item to use for generating suggestions. If null, global index is used.
     * @param searchMode The search mode to use for generating suggestions. If null, defaults to AS_RESULT. If item is null, this parameter is ignored.
     * @param process The process to use for generating suggestions. If null, all processes are used.
     * @return An ArgumentSuggestions object containing the suggestions.
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestionsFrom(RecipeIndexService recipeIndex, ItemStack item, SearchMode searchMode, Process<?> process) {
        return ArgumentSuggestions.stringCollectionAsync((info) -> suggestionsFrom(recipeIndex, item, searchMode, process));
    }
}
