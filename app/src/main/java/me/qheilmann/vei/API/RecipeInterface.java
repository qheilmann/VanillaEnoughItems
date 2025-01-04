package me.qheilmann.vei.API;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import me.qheilmann.vei.Menu.RecipeMenu;
import me.qheilmann.vei.foundation.gui.GuiItemService;

public class RecipeInterface {

    private RecipeMenu recipeInventory;

    public RecipeInterface(JavaPlugin plugin) {
        recipeInventory = new RecipeMenu(plugin, new GuiItemService());
    }

    public void openInterface(Player player, ShapedRecipe shapedRecipe) {
        recipeInventory.setRecipe(shapedRecipe);
        player.openInventory(recipeInventory.getInventory());
    }
}
