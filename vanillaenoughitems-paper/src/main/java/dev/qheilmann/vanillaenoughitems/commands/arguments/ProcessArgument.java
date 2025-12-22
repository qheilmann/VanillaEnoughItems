package dev.qheilmann.vanillaenoughitems.commands.arguments;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;
import net.kyori.adventure.text.Component;

/**
 * A custom argument for parsing and validating processes type.
 * Converts input strings to processes from {@link ProcessRegistry}, throwing exceptions if invalid.
 * Provides methods for generating suggestions based on a recipe index, item, and search mode.
 * 
 * @see Process
 * @see CustomArgument
 */
@NullMarked
public class ProcessArgument extends CustomArgument<Process, NamespacedKey> {

    /**
     * Constructs a new ProcessArgument with the specified node name.
     * converts the input string to a specific process type from the {@link ProcessRegistry}.
     * and throws a Minecraft-like exception if the process is not found.
     * 
     * @param nodeName the name of the argument node
     * @param context the recipe context containing the RecipeIndexReader
     */
    public ProcessArgument(String nodeName, RecipeGuiContext context) {
        super(new NamespacedKeyArgument(nodeName), (input) -> {
            NamespacedKey key = input.currentInput();
            ProcessRegistry processRegistry = context.getRecipeIndexReader().getAssociatedProcessRegistry();
            Process process = processRegistry.getProcess(key);

            if (process == null) {
                throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("No process found for key: " + arg), input);
            }

            return process;
        });

        // Default suggestions: all registered process keys
        replaceSuggestions(argumentSuggestions(context, null, null));
    }

    /**
     * Create argument suggestions for process keys based on the provided item and search mode.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for all indexed recipes in context 
     * @param searchMode the SearchMode to use with the item, or {@link SearchModeArgument.SearchMode#DEFAULT} if null
     * @return a NavigableSet of process key strings matching the criteria, the set can be empty if no processes match
     * @throws IllegalArgumentException if searchMode is provided without an item
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeGuiContext context, @Nullable ItemStack item, SearchModeArgument.@Nullable SearchMode searchMode) {
        return ArgumentSuggestions.stringCollection((info) -> suggestions(context, item, searchMode));
    }

    /**
     * Provide suggestions for process keys based on the provided item and search mode.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for all indexed recipes in context 
     * @param searchMode the SearchMode to use with the item, or {@link SearchModeArgument.SearchMode#DEFAULT} if null
     * @return a NavigableSet of process key strings matching the criteria, the set can be empty if no processes match
     * @throws IllegalArgumentException if searchMode is provided without an item
     */
    public static NavigableSet<String> suggestions(RecipeGuiContext context, @Nullable ItemStack item, SearchModeArgument.@Nullable SearchMode searchMode) {
        NavigableSet<String> suggestions = getProcesses(context, item, searchMode).stream()
                .map(process -> process.key().asString())
                .collect(Collectors.toCollection(TreeSet::new));
        return suggestions; 
    }

    /**
     * Get processes from the RecipeIndexReader based on the provided item and search mode.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for all indexed recipes in context 
     * @param searchMode the SearchMode to use with the item, or {@link SearchModeArgument.SearchMode#DEFAULT} if null
     * @return a NavigableSet of process key strings matching the criteria, the set can be empty if no processes match
     * @throws IllegalArgumentException if searchMode is provided without an item
     */
    private static NavigableSet<Process> getProcesses(RecipeGuiContext context, @Nullable ItemStack item, SearchModeArgument.@Nullable SearchMode searchMode) {

        if (item == null && searchMode != null) {
            throw new IllegalArgumentException("Search mode cannot be specified without an item");
        }

        MultiProcessRecipeReader reader;

        // Global index
        if (item == null) {
            reader = context.getRecipeIndexReader().readerWithAllRecipes();
        }

        // Item specific
        else {
            // Determine search mode
            if (searchMode == null) {
                searchMode = SearchModeArgument.SearchMode.DEFAULT;
            }
    
            reader = switch (searchMode) {
                case RECIPE -> context.getRecipeIndexReader().readerByResult(item);
                case USAGE -> context.getRecipeIndexReader().readerByIngredient(item);
                default -> throw new UnsupportedOperationException("Search mode " + searchMode + " is not implemented");
            };
        }

        if (reader == null) {
            return new TreeSet<>();
        }

        return reader.getAllProcesses();
    }
}
