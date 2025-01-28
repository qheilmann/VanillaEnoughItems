package me.qheilmann.vei.Core.Menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import net.kyori.adventure.text.Component;

public class RecipeMenu extends BaseGui<RecipeMenu> {
    public RecipeMenu(Recipe recipe) {
        super(6, Component.text("Recipe Menu"), InteractionModifier.VALUES);
    
        this.setDefaultClickAction((event, context) -> event.setCancelled(true)); // Cancel the event for the entire GUI

        var item = new GuiItem<RecipeMenu>(new ItemStack(Material.GOLD_BLOCK), (event, context) -> {
            event.getWhoClicked().sendMessage("You clicked the gold block!");
        });

        addItem(List.of(item)); // TODO check with two items
    }
}
