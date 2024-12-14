package me.qheilmann.vei.Menu;

import net.kyori.adventure.text.Component;

import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import me.qheilmann.vei.Menu.RecipeView.IRecipeView;
import me.qheilmann.vei.Menu.RecipeView.RecipeViewFactory;

public class RecipeMenu implements InventoryHolder {
    
    public Inventory inventory;
    
    private IRecipeView<Recipe> recipeView; // IRecipeView<? extends Recipe>
    private JavaPlugin plugin;
    
    public RecipeMenu(JavaPlugin plugin, Recipe recipe) {
        this.plugin = plugin;
        this.inventory = this.plugin.getServer().createInventory(this,54, Component.text("Recipe"));
        initInventory();
        setRecipe(recipe);
    }

    @Override
    public Inventory getInventory() {
        updateCycle();
        return inventory;
    }

    public void setRecipe(@NotNull Recipe recipe) {
        recipeView = RecipeViewFactory.createRecipeView(recipe);
        recipeView.setRecipe(recipe);
        updateRecipeViewPart();
    }

    private void initInventory() {
        // TODO: Implement this method

        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Special Item"));
        item.setItemMeta(meta);
        inventory.setItem(4, item);

        return;
    }

    private void updateRecipeViewPart() {
        for (var slot : recipeView.getRecipeContainer().getSlots()) {
            int index = menuCoordAsMenuIndex(viewCoordAsMenuCoord(slot.getCoord()));
            inventory.setItem(index, slot.getCurrentItemStack());
        }
    }

    private void updateCycle() {
        recipeView.getRecipeContainer().updateCycle();
        updateRecipeViewPart();
        return;
    }

    static private Vector2i viewCoordAsMenuCoord(Vector2i coord) {
        Validate.inclusiveBetween(0, 6, coord.x, "x must be between 0 and 6");
        Validate.inclusiveBetween(0, 4, coord.y, "y must be between 0 and 4");

        return new Vector2i(coord.x + 1, coord.y + 1);
    }

    static private int menuCoordAsMenuIndex(Vector2i coord) {
        return coord.x + coord.y * 9;
    }
}
