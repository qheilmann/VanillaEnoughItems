package me.qheilmann.vei.Core.Menu;

import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import net.kyori.adventure.text.Component;

import java.util.List;

import org.bukkit.Material;

public class TestMenu extends BaseGui<TestMenu> {

    private GuiItem<TestMenu> goldBlock;
    private GuiItem<TestMenu> ironBlock;
    private GuiItem<TestMenu> lapisBlock;

    public TestMenu() {
        super(6, Component.text("Test"), InteractionModifier.VALUES); // Call the BaseGui constructor

        // First we set the default click action for the entire GUI (cancels the event)
        this.setDefaultClickAction((event, context) -> event.setCancelled(true));

        goldBlock = new GuiItem<>(Material.COAL_BLOCK); // not final just for testing
        goldBlock.setAction((event, context) -> {
            context.addItem(context.goldBlock);
            event.getWhoClicked().sendMessage("You clicked a coal block, false item !");
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
            event.getWhoClicked().sendMessage("You clicked a gold block add 1 iron, true item !");
        });

        // Add the button to the GUI at a specific slot
        addItem(ironBlock);
        // setItem(0, ironBlock);

        // add same GuiItem over itself
        lapisBlock = new GuiItem<>(Material.LAPIS_BLOCK);
        lapisBlock.lore(List.of(Component.text("Click me to add a lapis block here")));
        lapisBlock.setAction((event, context) -> {
            
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
        });

        setItem(0, 5, lapisBlock);
    }
}
