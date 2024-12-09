package me.qheilmann.vei.API;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import me.qheilmann.vei.Inventory.RecipeInventory;

public class RecipeInterface {

    private RecipeInventory recipeInventory;

    public RecipeInterface(JavaPlugin plugin) {
        recipeInventory = new RecipeInventory(plugin);
    }

    public void openInterface(Player player, ShapedRecipe shapedRecipe) {
        RecipeInventory recipeInterface = recipeInventory.setRecipe(shapedRecipe);
        player.openInventory(recipeInterface.getInventory());
    }
}
