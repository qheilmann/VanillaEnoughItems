package me.qheilmann.vei.Command.CustomArguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import net.kyori.adventure.text.Component;

/**
 * A custom argument for parsing and validating search modes.
 * Converts input strings to search modes, throwing exceptions if invalid.
 * Provides methods for generating suggestions based on available search modes.
 * 
 * @see SearchMode
 * @see CustomArgument
 */
public class SearchModeArgument extends CustomArgument<SearchModeArgument.SearchMode, String>{

    /**
     * Constructs a new SearchModeArgument with the specified node name.
     * Converts the input string to a specific search mode from the SearchMode enum.
     * Throws a Minecraft-like exception if the search mode is not found.
     */    
    public SearchModeArgument(String nodeName) {
        super(new StringArgument(nodeName), (input) -> {
            String argument = input.currentInput().toLowerCase();
            SearchMode searchMode = SearchMode.fromString(argument);

            if (searchMode == null) {
                throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("Unknown search mode '" + arg + "'"), input);
            }

            return searchMode;
        });

        // Default suggestions
        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync((info) -> suggestionsAll()));
    }

    public Argument<SearchMode> replaceSuggestions(ArgumentSuggestions<CommandSender> suggestions) {
        return super.replaceSuggestions(suggestions);
    }

    /**
    * Produces a collection of suggestions for the search mode argument.
    * 
    * @param recipeIndex The RecipeIndexService to utilize.
    * @param item The item to check as an ingredient or result. If null, all search modes are suggested.
    * @return A CompletableFuture containing the collection of search mode suggestions.
    * @see #argumentSuggestionsFrom()
    */
    public static CompletableFuture<Collection<String>> suggestionsFrom(RecipeIndexService recipeIndex, ItemStack item) {
        if (recipeIndex == null) {
            throw new IllegalArgumentException("RecipeIndexService cannot be null.");
        }
        if (item == null) {
            return suggestionsAll();
        }
        
        return CompletableFuture.supplyAsync(() -> {
            Collection<String> suggestions = new ArrayList<>();
            if(recipeIndex.getByIngredient(item) != null) {
                suggestions.add(SearchMode.AS_INGREDIENT.getName());
            }
            
            if (recipeIndex.getByResult(item) != null) {
                suggestions.add(SearchMode.AS_RESULT.getName());
            }
            
            if(SearchMode.values().length != 2) {
                assert false : "SearchMode values length is not 2. Please check the SearchMode enum.";
            }

            return suggestions;
        })
        .exceptionally((e) -> {
            VanillaEnoughItems.LOGGER.error("Error while generating search mode suggestions", e);
            return new ArrayList<String>();
        });
    }

    /**
    * Produces a collection of all search mode suggestions.
    */
    public static CompletableFuture<Collection<String>> suggestionsAll() {
        return CompletableFuture.supplyAsync(() -> {
            Collection<String> suggestions = new ArrayList<>();
            for (SearchMode searchMode : SearchMode.values()) {
                suggestions.add(searchMode.getName());
            }
            return suggestions;
        });
    }

    /**
    * Produces a argument suggestions based on the search mode argument with the given item.
    * 
    * @param recipeIndex The RecipeIndexService to utilize.
    * @param item The item to check as an ingredient or result. If null, all search modes are suggested.
    * @return An ArgumentSuggestions object containing the suggestions.
    * @see #suggestionsFrom(RecipeIndexService, ItemStack)
    */
    public static ArgumentSuggestions<CommandSender> argumentSuggestionsFrom(RecipeIndexService recipeIndex, ItemStack item) {
        return ArgumentSuggestions.stringCollectionAsync((info) -> suggestionsFrom(recipeIndex, item));
    }

    public enum SearchMode {
        AS_RESULT("as-result"),
        AS_INGREDIENT("as-ingredient");

        private final String name;

        SearchMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public static SearchMode fromString(String mode) {
            for (SearchMode searchMode : SearchMode.values()) {
                if (searchMode.name.equalsIgnoreCase(mode)) {
                    return searchMode;
                }
            }
            return null;
        }
    }
}
