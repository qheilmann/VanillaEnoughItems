package me.qheilmann.vei.Command.CustomArguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;

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
     * Constructs a new ProcessArgument with the specified node name.
     * converts the input string to a specific process type from the ProcessRegistry.
     * and throws a Minecraft-like exception if the process is not found.
     */    
    public SearchModeArgument(String nodeName) {
        super(new StringArgument(nodeName), (input) -> {
            String argument = input.currentInput().toLowerCase();
            SearchMode searchMode = SearchMode.fromString(argument);

            if (searchMode == null) {
                throw CustomArgumentHelper.minecraftLikeException("searchMode", input);
            }

            return searchMode;
        });
    }

    /**
    * Generates a collection of suggestions for the search mode argument.
    * 
    * @return A CompletableFuture containing the collection of search mode suggestions.
    * @see #argumentSuggestions()
    */
    public static CompletableFuture<Collection<String>> suggestions() {
        return CompletableFuture.supplyAsync(() -> {
            Collection<String> suggestions = new ArrayList<>();
            for (SearchMode mode : SearchMode.values()) {
                suggestions.add(mode.getName());
            }
            return suggestions;
        });
    }

    /**
    * Generates argument suggestions for the search mode argument
    * 
    * @return An ArgumentSuggestions object containing the suggestions.
    * @see #suggestions()
    */
    public static ArgumentSuggestions<CommandSender> argumentSuggestions() {
        return ArgumentSuggestions.stringCollectionAsync((info) -> suggestions());
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
