package dev.qheilmann.vanillaenoughitems.commands.arguments;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import net.kyori.adventure.text.Component;

/**
 * A custom argument for parsing and validating search modes.
 * Converts input strings to search modes, throwing exceptions if invalid.
 * Provides methods for generating suggestions based on available search modes.
 * 
 * @see SearchMode
 * @see CustomArgument
 */
@NullMarked
public class SearchModeArgument extends CustomArgument<SearchModeArgument.SearchMode, String> {

    /**
     * Constructs a new SearchModeArgument with the specified node name.
     * converts the input string to a specific SearchMode.
     * and throws a Minecraft-like exception if the search mode is not found.
     * 
     * @param nodeName the name of the argument node
     * @param context the recipe context containing the RecipeIndexReader
     */
    public SearchModeArgument(String nodeName, RecipeGuiContext context) {
        super(new StringArgument(nodeName), (input) -> {
            String argument = input.currentInput();
            SearchMode searchMode = SearchMode.fromNameIgnoreCase(argument);

            if (searchMode == null) {
                throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("No search mode found for: " + arg), input);
            }

            return searchMode;
        });
        
        // Default suggestions: all search mode names
        replaceSuggestions(argumentSuggestions(context, null));
    }

    /**
     * Create argument suggestions for search mode names based on the provided item.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for {@link SearchModeArgument.SearchMode#ALL}
     * @return ArgumentSuggestions providing available search mode names for the item, or {@link SearchModeArgument.SearchMode#ALL} if item is null
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeGuiContext context, @Nullable ItemStack item) {
        return ArgumentSuggestions.stringCollection((info) -> suggestions(context, item));
    }

    /**
     * Get available search mode names based on the provided item.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for {@link SearchModeArgument.SearchMode#ALL}
     * @return a collection of available search mode names for the item, or {@link SearchModeArgument.SearchMode#ALL} if item is null
     */
    public static Collection<String> suggestions(RecipeGuiContext context, @Nullable ItemStack item) {
        EnumSet<SearchMode> availableModes = getSearchModes(context, item);
        @SuppressWarnings("null")
        Set<String> availableModeNames = availableModes.stream()
            .map(SearchMode::getName)
            .collect(Collectors.toSet());
        
        return availableModeNames;
    }

    /**
     * Get available search modes based on the provided item.
     *
     * @param context the recipe context containing the RecipeIndexReader
     * @param item the ItemStack to search for, or null for {@link SearchModeArgument.SearchMode#ALL}
     * @return an EnumSet of available SearchModes for the item, or {@link SearchModeArgument.SearchMode#ALL} if item is null
     */
    private static EnumSet<SearchMode> getSearchModes(RecipeGuiContext context, @Nullable ItemStack item) {
        if (item == null) {
            return SearchMode.ALL;
        }

        @SuppressWarnings("null")
        EnumSet<SearchMode> availableModes = EnumSet.noneOf(SearchMode.class);

        // Recipe
        MultiProcessRecipeReader reader = context.getRecipeIndexReader().readerByResult(item);
        if (reader != null) {
            availableModes.add(SearchMode.RECIPE);
        }

        // Usage
        reader = context.getRecipeIndexReader().readerByIngredient(item);
        if (reader != null) {
            availableModes.add(SearchMode.USAGE);
        }

        return availableModes;
    }

    /**
     * Enum representing different search modes for recipe lookup.
     */
    public enum SearchMode {
        RECIPE("recipe"),
        USAGE("usage");

        /**
         * The default search mode
         */
        public static final SearchMode DEFAULT = RECIPE;
        @SuppressWarnings("null")
        public static final EnumSet<SearchMode> ALL = EnumSet.allOf(SearchMode.class);

        private final String name;

        SearchMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public static SearchMode fromNameIgnoreCase(String mode) {
            for (SearchMode searchMode : SearchMode.values()) {
                if (searchMode.name.equalsIgnoreCase(mode)) {
                    return searchMode;
                }
            }
            return null;
        }
    }
}
