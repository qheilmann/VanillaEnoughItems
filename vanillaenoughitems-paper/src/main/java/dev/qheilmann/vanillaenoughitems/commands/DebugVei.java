package dev.qheilmann.vanillaenoughitems.commands;

import org.jspecify.annotations.NullMarked;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.config.style.Style;

@NullMarked
public class DebugVei {
    private static final String NAME = "debugvei";
    private static final String[] ALIASES = {"dc"};
    private static final CommandPermission PERMISSION = CommandPermission.OP;
    private static final String SHORT_HELP = "Debug VEI";
    private static final String LONG_HELP = SHORT_HELP;
    private static final String USAGE = """

                                    /debugvei
                                    """;

    private DebugVei() {} // Prevent instantiation

    public static void register() {

        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_HELP, LONG_HELP)
            .withUsage(USAGE)
            .executesPlayer((player, args) -> {
                // TEST CODE BEGIN

                Style style = VanillaEnoughItems.veiConfig().style();

                if (style == null) {
                    player.sendMessage("VEI Style is null!");
                    return;
                }

                boolean hasRessourcePack = style.hasResourcePack();
                style.setHasResourcePack(!hasRessourcePack);
                VanillaEnoughItems.veiConfig().setStyle(style);
                player.sendMessage("VEI Resource Pack enabled: " + !hasRessourcePack);

                // TEST CODE END
            })

            .register();
    }
}
