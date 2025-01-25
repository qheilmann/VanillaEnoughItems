package me.qheilmann.vei.Command;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.GUI.Gui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.TestMenu;
import net.kyori.adventure.text.Component;

public class TestCommand implements ICommand{
    public static final String NAME = "teste";
    public static final String[] ALIASES = {"te"};
    public static final String SHORT_DESCRIPTION = "Test temporary command";
    public static final String LONG_DESCRIPTION = "";
    public static final CommandPermission PERMISSION = CommandPermission.NONE;
    public static final String USAGE = "";

    private static final String INNER_GUI = "innerGui";
    private static final String DERIVED_CLASS = "derivedClass";

    public TestCommand() {}

    @Override
    public void register() {
        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_DESCRIPTION, LONG_DESCRIPTION)
            .withArguments(new StringArgument("type")
                .replaceSuggestions(ArgumentSuggestions.strings(INNER_GUI, DERIVED_CLASS)))
            .executesPlayer((player, args) -> {
                String type = (String) args.get("type");
                TestAction(player, type);
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

    private void TestAction(Player player, String type) {

        if (type.equals(INNER_GUI)) {
            innerGui(player);
        } else if (type.equals(DERIVED_CLASS)) {
            derivedClass(player);
        } else {
            player.sendMessage("Invalid type");
        }
    }

    
    // Sub methods

    private void innerGui(Player player) {
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

        GuiItem<Gui> coal = new GuiItem<>(Material.COAL);
        coal.lore(List.of(Component.text("Click me (remove item from inventory)")));
        coal.setAction((event, contextGui) -> {
            event.setCancelled(true);
            contextGui.removeItem(new GuiItem<Gui>(Material.DIAMOND));
        });

        GuiItem<Gui> iron = new GuiItem<>(Material.IRON_INGOT);
        iron.lore(List.of(Component.text("Click me (add other GuiItem) final lapis")));
        iron.setAction((event, contextGui) -> {
            event.setCancelled(true);
            contextGui.addItem(lapiz); // work only because lapiz is a final ref
        });

        GuiItem<Gui> lapisBlock = new GuiItem<>(Material.LAPIS_BLOCK);
        gui.lapizGui = lapisBlock;
        GuiItem<Gui> ironBlock = new GuiItem<>(Material.IRON_BLOCK);
        ironBlock.lore(List.of(Component.text("Click me (add other GuiItem) ref lapis")));
        ironBlock.setAction((event, contextGui) -> {
            event.setCancelled(true);
            contextGui.addItem(contextGui.lapizGui); // work only because lapizGui is inside the Gui class
        });
        
        gui.setItem(0, 0, diams);
        gui.setItem(1, 0, redstoneDust);
        gui.setItem(2, 0, lapiz);
        gui.setItem(3, 0, emerald);
        gui.setItem(4, 0, coal);
        gui.setItem(5, 0, iron);
        gui.setItem(5, 1, ironBlock);

        gui.open(player);
        player.sendMessage("Test command executed");
    }

    private void derivedClass(Player player) {
        TestMenu menu = new TestMenu();

        // If the BaseGui was closed, the item will not be added because the addItem() methode will not be visible
        GuiItem<TestMenu> junkItem = new GuiItem<>(Material.DIRT);
        junkItem.lore(List.of(Component.text("If the BaseGui was closed, the item will not be added because the addItem() methode will not be visible")));
        menu.addItem(junkItem);

        menu.open(player);
    }
}
