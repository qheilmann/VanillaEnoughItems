package me.qheilmann.vei.Command.CustomArguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
import dev.jorel.commandapi.arguments.StringArgument;
import me.qheilmann.vei.Core.Process.Process;

public class VEICommandArguments {

    /**
     * Creates a custom argument for a recipe process
     * @param nodeName
     * @return
     */
    public static Argument<Process<?>> processArgument(String nodeName) {
        return new CustomArgument<Process<?>, String>(new StringArgument(nodeName), (input) -> {
            String processName = input.input().toLowerCase();
            Process<?> process = Process.ProcessRegistry.getProcessByName(processName);

            if (process == null) {
                throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Unknown process: ").appendArgInput());
            }

            return process;
        });
    }
}
