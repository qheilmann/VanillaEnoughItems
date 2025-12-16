package dev.qheilmann.vanillaenoughitems.commands;

import org.jspecify.annotations.NullMarked;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGui;

@NullMarked
public class DebugCommand {
    public static final String NAME = "debugvei";
    public static final String[] ALIASES = {"dc"};
    public static final CommandPermission PERMISSION = CommandPermission.OP;
    public static final String SHORT_HELP = "Debug VEI";
    public static final String LONG_HELP = SHORT_HELP;
    public static final String USAGE = """

                                    /debugvei
                                    """;

    private DebugCommand() {}; // Prevent instantiation

    public static void register() {

        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_HELP, LONG_HELP)
            .withUsage(USAGE)

            // debugvei
            .executesPlayer((player, args) -> {
                RecipeGui gui = new RecipeGui();
                gui.render();
                gui.open(player);
            })

            .register();
    }
}
