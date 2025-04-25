package me.qheilmann.vei.Command.CustomArguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import net.kyori.adventure.text.Component;

/**
 * A custom argument for parsing and validating processes type.
 * Converts input strings to processes from {@code ProcessRegistry}, throwing exceptions if invalid.
 * Provides methods for generating suggestions based on a recipe index, item, and search mode.
 * 
 * @see Process
 * @see CustomArgument
 */
public class ProcessArgument extends CustomArgument<Process<?>, String>{

    /**
     * Constructs a new ProcessArgument with the specified node name.
     * converts the input string to a specific process type from the ProcessRegistry.
     * and throws a Minecraft-like exception if the process is not found.
     */    
    public ProcessArgument(String nodeName) {
        super(new StringArgument(nodeName), (input) -> {
            String processName = input.currentInput().toLowerCase();
            Process<?> process = Process.ProcessRegistry.getProcessByName(processName);

            if (process == null) {
                throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("Unknow process '" + arg + "'"), input);
            }

            return process;
        });
    }

    /**
    * Generates a collection of suggestions for the process argument based on the provided item and search mode.
    * 
    * @param recipeIndex The RecipeIndexService to use
    * @param item The item to use for generating suggestions. If null, global index is used.
    * @param searchMode The search mode to use for generating suggestions. If null, defaults to AS_RESULT.
    * @return A CompletableFuture containing the collection of suggestions.
    * @see #argumentSuggestions(RecipeIndexService, ItemStack, SearchModeArgument.SearchMode)
    */
    public static CompletableFuture<Collection<String>> suggestions(RecipeIndexService recipeIndex, ItemStack item, SearchModeArgument.SearchMode searchMode) {
        if (recipeIndex == null) {
            throw new IllegalArgumentException("RecipeIndexService cannot be null");
        }
        if (item == null && searchMode != null) {
            throw new IllegalArgumentException("Search mode cannot be used without an item");
        }

        return CompletableFuture.supplyAsync(() -> {
            SearchModeArgument.SearchMode mode = searchMode;
            MixedProcessRecipeReader recipeReader;
            Collection<String> suggestions = new ArrayList<>();

            // Global index
            if (item == null && searchMode == null) {
                recipeReader = recipeIndex.getGlobalIndex();
            }
            // Item index (item != null)
            else {
                if (mode == null) {
                    mode = SearchModeArgument.SearchMode.AS_RESULT;
                }

                if (mode == SearchModeArgument.SearchMode.AS_RESULT) {
                    recipeReader = recipeIndex.getByResult(item);
                } else if (mode == SearchModeArgument.SearchMode.AS_INGREDIENT) {
                    recipeReader = recipeIndex.getByIngredient(item);
                } else {
                    throw new NotImplementedException("Search mode " + mode + " is not implemented");
                }
            }

            // Map to process names
            if (recipeReader != null) {
                suggestions.addAll(recipeReader.getAllProcess().stream()
                    .map(Process::getProcessName)
                    .toList());
            }

            return suggestions;
        });
    }

    /**
    * Generates argument suggestions for the process argument based on the provided item and search mode
    * 
    * @param recipeIndex The RecipeIndexService to use
    * @param item The item to use for generating suggestions. If null, global index is used.
    * @param searchMode The search mode to use for generating suggestions. If null, defaults to AS_RESULT.
    * @return An ArgumentSuggestions object containing the suggestions.
    * @see #suggestions(RecipeIndexService, ItemStack, SearchModeArgument.SearchMode)
    */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeIndexService recipeIndex, ItemStack item, SearchModeArgument.SearchMode searchMode) {
        return ArgumentSuggestions.stringCollectionAsync((info) -> suggestions(recipeIndex, item, searchMode));
    }
}
