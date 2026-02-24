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
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
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
     * @param recipeIndex the recipe index
     */
    public SearchModeArgument(String nodeName, RecipeIndex recipeIndex) {
        super(new StringArgument(nodeName), (input) -> {
            String argument = input.currentInput();
            SearchMode searchMode = SearchMode.fromNameIgnoreCase(argument);

            if (searchMode == null) {
                throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("No search mode found for: " + arg), input);
            }

            return searchMode;
        });
        
        // Default suggestions: all search mode names
        replaceSuggestions(argumentSuggestions(recipeIndex, null));
    }

    /**
     * Create argument suggestions for search mode names based on the provided item.
     *
     * @param recipeIndex the recipe index
     * @param item the ItemStack to search for, or null for {@link SearchModeArgument.SearchMode#ALL}
     * @return ArgumentSuggestions providing available search mode names for the item, or {@link SearchModeArgument.SearchMode#ALL} if item is null
     */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeIndex recipeIndex, @Nullable ItemStack item) {
        return ArgumentSuggestions.stringCollection((info) -> suggestions(recipeIndex, item));
    }

    /**
     * Get available search mode names based on the provided item.
     *
     * @param recipeIndex the recipe index
     * @param item the ItemStack to search for, or null for {@link SearchModeArgument.SearchMode#ALL}
     * @return a collection of available search mode names for the item, or {@link SearchModeArgument.SearchMode#ALL} if item is null
     */
    @SuppressWarnings("null")
    public static Collection<String> suggestions(RecipeIndex recipeIndex, @Nullable ItemStack item) {
        Set<SearchMode> availableModes = getSearchModes(recipeIndex, item);
        return availableModes.stream()
            .map(SearchMode::getName)
            .collect(Collectors.toSet());
    }

    /**
     * Get available search modes based on the provided item.
     *
     * @param recipeIndex the recipe index
     * @param item the ItemStack to search for, or null for {@link SearchModeArgument.SearchMode#ALL}
     * @return an Set of available SearchModes for the item, or {@link SearchModeArgument.SearchMode#ALL} if item is null
     */
    private static Set<SearchMode> getSearchModes(RecipeIndex recipeIndex, @Nullable ItemStack item) {
        if (item == null) {
            return SearchMode.ALL;
        }

        EnumSet<SearchMode> availableModes = EnumSet.noneOf(SearchMode.class);

        // Recipe
        MultiProcessRecipeReader reader = recipeIndex.readerByResult(item);
        if (reader != null) {
            availableModes.add(SearchMode.RECIPE);
        }

        // Usage
        reader = recipeIndex.readerByIngredient(item);
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
        public static final Set<SearchMode> ALL = Set.copyOf(EnumSet.allOf(SearchMode.class));

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
