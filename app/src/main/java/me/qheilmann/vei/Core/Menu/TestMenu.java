package me.qheilmann.vei.Core.Menu;

import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestMenu extends BaseGui<TestMenu> {

    private GuiItem<TestMenu> goldBlock;
    private GuiItem<TestMenu> ironBlock;
    private GuiItem<TestMenu> lapisBlock;

    public TestMenu() {
        super(6, Component.text("Test"), InteractionModifier.VALUES); // Call the BaseGui constructor

        // Example click action for the entire GUI
        this.setDefaultClickAction((event, context) -> event.setCancelled(true)); // Cancel the event for the entire GUI

        // Example Top click action
        this.setDefaultTopClickAction((event, context) -> {
            event.getWhoClicked().sendMessage("You clicked the top inventory !");
        });

        // Example Bottom click action
        this.setPlayerInventoryAction((event, context) -> {
            event.getWhoClicked().sendMessage("You clicked the bottom inventory ! (Player inventory)");
        });

        // Example Open GUI action
        this.setOpenGuiAction((event, context) -> {
            event.getPlayer().sendMessage("(1) You opened\nthe GUI ! (multiline with \\n)");
            event.getPlayer().sendMessage("(2) You opened%nthe GUI ! (multiline with \"%%n\".formatted, but [CR] char)".formatted());
        });

        // Example Close GUI action
        this.setCloseGuiAction((event, context) -> {
            event.getPlayer().sendMessage("You closed the GUI !");
        });

        // Example Drag action
        this.setDragAction((event, context) -> {
            event.getWhoClicked().sendMessage("You dragged an item !");
        });

        // Example Outside click action
        this.setOutsideClickAction((event, context) -> {
            event.getWhoClicked().sendMessage("You clicked outside the GUI !");
        });

        // Example Slot action
        this.setSlotAction(5,8, (event, context) -> {
            context.getInventory().clear(); // Clear the inventory if clicked on the last slot
        });

        // Here some guiItem action (with more complex usecase)

        goldBlock = new GuiItem<>(Material.COAL_BLOCK); // not final just for testing
        goldBlock.setAction((event, context) -> {
            context.addItem(context.goldBlock);
            event.getWhoClicked().sendMessage("You clicked a coal block, (This should not be printed not the right item) !");
        });

        ironBlock = new GuiItem<>(Material.IRON_BLOCK);
        ironBlock.lore(List.of(Component.text("Click me to add another GuiItem with a specified action")));
        ironBlock.setAction((event, contextGui) -> {
            // contextGui.setItem(1,1, contextGui.goldBlock);
            contextGui.addItem(contextGui.goldBlock);
            event.getWhoClicked().sendMessage("You clicked an iron block add 1 gold GuiItem !");
        });

        // Change the gold block after create the lamda
        goldBlock = new GuiItem<>(Material.GOLD_BLOCK);
        goldBlock.lore(List.of(Component.text("Click me to add a gold block with a specified action")));
        goldBlock.setAction((event, context) -> {
            // context.setItem(1,2, context.ironBlock);
            context.addItem(context.ironBlock);
            event.getWhoClicked().sendMessage("You clicked a gold block add 1 iron !");
        });

        // Add the button to the GUI at a specific slot
        addItem(ironBlock);
        // setItem(0, ironBlock);

        // add same GuiItem over itself
        lapisBlock = new GuiItem<>(Material.LAPIS_BLOCK);
        lapisBlock.lore(List.of(Component.text("Click me to add a lapis block here")));
        lapisBlock.setAction(this::handleLapisBlockClick);

        setItem(0, 5, lapisBlock);
    }

    @Override
    @SafeVarargs
    public final @NotNull HashMap<Integer, @NotNull ItemStack> addItem(@NotNull final GuiItem<TestMenu>... items) {
        return super.addItem(items);
    }

    private void handleLapisBlockClick(InventoryClickEvent event, TestMenu context) {
        // [Add clicked amount + 1]
        // GuiItem<TestMenu> someLapiz = context.lapisBlock.clone();
        // someLapiz.setAmount(event.getCurrentItem().getAmount());
        // someLapiz.setAmount(someLapiz.getAmount() + 1);
        // context.addItem(someLapiz);

        // [Add exactly clicked amount (cloned) always same slot]
        context.addItem(context.lapisBlock.clone());

        // [Add exactly clicked amount (ITSELF NOT AUTHORIZE)]
        // context.addItem(context.lapisBlock);

        // [Then add itself but with amount of 1]
        GuiItem<TestMenu> newLapis = context.lapisBlock.clone();
        newLapis.setAmount(1);
        context.addItem(newLapis); 
        
        event.getWhoClicked().sendMessage("You clicked a lapis block, add 1 lapis block !");
    }
}
