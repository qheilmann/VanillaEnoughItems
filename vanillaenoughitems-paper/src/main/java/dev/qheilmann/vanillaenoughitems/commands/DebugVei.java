package dev.qheilmann.vanillaenoughitems.commands;

import org.jspecify.annotations.NullMarked;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.qheilmann.vanillaenoughitems.utils.playerhead.PlayerHeadRegistry;

@NullMarked
public class DebugVei {
    public static final String NAME = "debugvei";
    public static final String[] ALIASES = {"dc"};
    public static final CommandPermission PERMISSION = CommandPermission.OP;
    public static final String SHORT_HELP = "Debug VEI";
    public static final String LONG_HELP = SHORT_HELP;
    public static final String USAGE = """

                                    /debugvei
                                    """;

    private DebugVei() {}; // Prevent instantiation

    public static void register() {

        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_HELP, LONG_HELP)
            .withUsage(USAGE)
            .executesPlayer((player, args) -> {
                // TEST CODE BEGIN

                player.give(PlayerHeadRegistry.craftingTable());
                player.give(PlayerHeadRegistry.fireworkStarCyan());
                player.give(PlayerHeadRegistry.fireworkStarGreen());
                player.give(PlayerHeadRegistry.quartzArrowDown());
                player.give(PlayerHeadRegistry.quartzArrowLeft());
                player.give(PlayerHeadRegistry.quartzArrowRight());
                player.give(PlayerHeadRegistry.quartzArrowUp());
                player.give(PlayerHeadRegistry.quartzBackwardII());
                player.give(PlayerHeadRegistry.quartzBackward());
                player.give(PlayerHeadRegistry.quartzForward());
                player.give(PlayerHeadRegistry.quartzForwardII());
                player.give(PlayerHeadRegistry.quartzPlus());
                player.give(PlayerHeadRegistry.quartzReverseExclamationMark());
                player.give(PlayerHeadRegistry.quartzSlash());
                player.give(PlayerHeadRegistry.quartzX());
                player.give(PlayerHeadRegistry.steve());

                // TEST CODE END
            })

            .register();
    }
}
