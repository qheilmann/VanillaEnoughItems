package me.qheilmann.vei.Command;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import net.kyori.adventure.text.Component;

public class TestCommand implements ICommand{
    public static final String NAME = "teste";
    public static final String[] ALIASES = {"te"};
    public static final String SHORT_DESCRIPTION = "Test temporary command";
    public static final String LONG_DESCRIPTION = "";
    public static final CommandPermission PERMISSION = CommandPermission.NONE;
    public static final String USAGE = "";

    private JavaPlugin plugin;

    public TestCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_DESCRIPTION, LONG_DESCRIPTION)
            .executesPlayer((player, args) -> {
                TestAction(player);
            })
            .register();
    }

    // Command action
    // Must:
    // - Be suffixed with "Action"
    // - Perform the command backend API
    // Can:
    // - Verify and convert command arguments
    // - Send feedback messages
    // - Call other cosmetic methods (particles, sounds, etc.)

    private <G extends BaseGui<G>>void TestAction(Player player) {
        Set<InteractionModifier> interactionModifiers = Set.of(InteractionModifier.PREVENT_ITEM_PLACE);
        BaseGui<G> gui = new BaseGui<>(6, Component.text("Test"), interactionModifiers);
        GuiItem<G> item = new GuiItem<>(Material.DIAMOND);
        
        gui.setItem(0, 0, item);
        gui.open(player);
        player.sendMessage("Test command executed");
    }
}
