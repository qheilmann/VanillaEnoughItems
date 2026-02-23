package dev.qheilmann.vanillaenoughitems.commands;

import org.jspecify.annotations.NullMarked;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@NullMarked
public class ReloadCommand {
    private static final String NAME = "vei";
    private static final String[] ALIASES = {};
    private static final CommandPermission PERMISSION = CommandPermission.OP;
    private static final String SHORT_HELP = "Reload VEI config";
    private static final String LONG_HELP = SHORT_HELP;
    private static final String USAGE = """

                                    /vei reload
                                    """;

    private ReloadCommand() {} // Prevent instantiation

    public static void register() {

        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_HELP, LONG_HELP)
            .withUsage(USAGE)
            .withSubcommand(
                new CommandAPICommand("reload")
                    .executes((sender, args) -> {
                        VanillaEnoughItems.reloadVeiConfigAsync()
                            .thenRun(() -> {
                                sender.sendMessage(Component.text("[VEI] Config reloaded.", NamedTextColor.GREEN));
                                VanillaEnoughItems.LOGGER.info("Config reloaded by " + sender.getName());
                            })
                            .exceptionally(e -> {
                                sender.sendMessage(Component.text("[VEI] Failed to reload config: " + e.getMessage(), NamedTextColor.RED));
                                VanillaEnoughItems.LOGGER.error("Failed to reload config", (Throwable) e);
                                return null;
                            });
                    })
            )
            .register();
    }
}
