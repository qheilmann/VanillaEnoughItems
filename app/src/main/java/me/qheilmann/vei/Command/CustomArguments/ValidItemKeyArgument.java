package me.qheilmann.vei.Command.CustomArguments;

import com.mojang.brigadier.arguments.ArgumentType;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.suggestion.Suggestions;

public class ValidItemKeyArgument implements ArgumentType<String> {
    private final List<String> validKeys;

    public ValidItemKeyArgument(Collection<String> validKeys) {
        this.validKeys = validKeys.stream()
                                  .map(String::toLowerCase)
                                  .toList();
    }

    public static ValidItemKeyArgument of(Collection<String> validKeys) {
        return new ValidItemKeyArgument(validKeys);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String key = reader.readUnquotedString();
        if (!validKeys.contains(key.toLowerCase())) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                .createWithContext(reader);
                //  createWithContext(reader, key);
        }
        return key;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        validKeys.stream()
                 .filter(k -> k.contains(remaining))
                 .forEach(builder::suggest);

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        // A few examples for ambiguity checks
        return validKeys.stream().limit(3).collect(Collectors.toList());
    }
}
