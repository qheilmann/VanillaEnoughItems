package me.qheilmann.vei.Command.CustomArguments;

import com.mojang.brigadier.arguments.ArgumentType;
import java.util.Collection;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import dev.jorel.commandapi.arguments.CommandAPIArgumentType;
import dev.jorel.commandapi.arguments.SafeOverrideableArgument;
import dev.jorel.commandapi.executors.CommandArguments;

public class APIValidItemKeyArgument extends SafeOverrideableArgument<String, String> {

    public APIValidItemKeyArgument(String nodeName, Collection<String> validKeys) {
        super(nodeName, new ValidItemKeyArgument(validKeys), String::valueOf);
    }

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public CommandAPIArgumentType getArgumentType() {
        return CommandAPIArgumentType.PRIMITIVE_STRING;
    }

    @Override
    public <Source> String parseArgument(CommandContext<Source> cmdCtx, String key, CommandArguments previousArgs)
            throws CommandSyntaxException {
        String value = cmdCtx.getArgument(key, String.class);
        if (value == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                .create();
        }
        return value;
    }
}
