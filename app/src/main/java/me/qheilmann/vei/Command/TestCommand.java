package me.qheilmann.vei.Command;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.GUI.Gui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import net.kyori.adventure.text.Component;

public class TestCommand implements ICommand{
    public static final String NAME = "teste";
    public static final String[] ALIASES = {"te"};
    public static final String SHORT_DESCRIPTION = "Test temporary command";
    public static final String LONG_DESCRIPTION = "";
    public static final CommandPermission PERMISSION = CommandPermission.NONE;
    public static final String USAGE = "";

    public TestCommand() {}

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

    private void TestAction(Player player) {
        Set<InteractionModifier> interactionModifiers = Set.of(InteractionModifier.PREVENT_ITEM_PLACE);
        Gui gui = new Gui(6, Component.text("Test"), interactionModifiers);
        
        GuiItem<Gui> diams = new GuiItem<>(Material.DIAMOND);
        diams.lore(List.of(Component.text("Diamonds (no action)")));
        
        GuiItem<Gui> redstoneDust = new GuiItem<>(Material.REDSTONE);
        redstoneDust.lore(List.of(Component.text("Click me (send message)")));
        redstoneDust.setAction((event, contextGui) -> {
            event.setCancelled(true);
            player.sendMessage("You clicked the item");
        });

        GuiItem<Gui> lapiz = new GuiItem<>(Material.LAPIS_LAZULI);
        lapiz.lore(List.of(Component.text("Click me (random gold)")));
        lapiz.setAction((event, contextGui) -> {
            event.setCancelled(true);
            int randomStack = (int) (Math.random() * 64);
            ItemStack item = new ItemStack(Material.GOLD_INGOT, randomStack);
            GuiItem<Gui> gold = new GuiItem<>(item);
            contextGui.setItem(2, 1, gold);
        });

        GuiItem<Gui> emerald = new GuiItem<>(Material.EMERALD);
        emerald.lore(List.of(Component.text("Click me (add item to inventory)")));
        emerald.setAction((event, contextGui) -> {
            event.setCancelled(true);
            contextGui.addItem(new GuiItem<Gui>(Material.DIAMOND));
        });
        
        gui.setItem(0, 0, diams);
        gui.setItem(1, 0, redstoneDust);
        gui.setItem(2, 0, lapiz);
        gui.setItem(3, 0, emerald);

        gui.open(player);
        player.sendMessage("Test command executed");
    }
}
