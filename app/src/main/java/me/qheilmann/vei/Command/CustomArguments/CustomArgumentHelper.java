package me.qheilmann.vei.Command.CustomArguments;

import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfo;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class CustomArgumentHelper {

    // Private constructor prevents instantiation
    private CustomArgumentHelper() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
    /**
     * Creates a custom argument adventure component exception with a Minecraft-like error message.
     *
     * @param argumentType The type of the argument (e.g., "item"). This will be used in the error message,
     *                     like "Unknown item 'diirt'".
     * @param input        The CustomArgumentInfo object containing the input information.
     * @param <B>          The type of the argument.
     * @return A CustomArgumentException with a Minecraft-like error message.
     */
    public static <B> CustomArgumentException minecraftLikeException(String argumentType, CustomArgumentInfo<B> input) {
        String lastArg = input.input();
        B lastParsedArg = input.currentInput();
        String fullInput = input.previousArgs().fullInput();
        int lastArgIndex = fullInput.lastIndexOf(lastArg);
        String lastPartBeforeArg;
        
        if (lastArgIndex == -1 || lastArgIndex < 10) {
            lastPartBeforeArg = fullInput.substring(0, lastArgIndex);
        } else {
            lastPartBeforeArg = "..." + fullInput.substring(lastArgIndex - 10, lastArgIndex);
        }
        
        return CustomArgumentException.fromAdventureComponent(Component.text().content("Unknown " + argumentType + " '" + lastParsedArg.toString() + "'").color(NamedTextColor.RED).appendNewline()
                .append(Component.text(lastPartBeforeArg).color(NamedTextColor.GRAY))
                .append(Component.text(lastArg, NamedTextColor.RED).decorate(TextDecoration.UNDERLINED))
                .append(Component.text(new MessageBuilder().appendHere().toString(), NamedTextColor.RED))
                .build());
    }
}
