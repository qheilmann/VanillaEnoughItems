package me.qheilmann.vei.Menu;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;

// TODO: This class is a temporary implementation of the ShapedRecipe for testing purposes
public class ShapedRecipeMenu implements InventoryHolder {
    
    public Inventory inventory;
    public ShapedRecipe currentShapedRecipe;

    private JavaPlugin plugin;
    private boolean hasRecipeChanged = false;
    
    public ShapedRecipeMenu(JavaPlugin plugin) {
        this.plugin = plugin;
        this.inventory = this.plugin.getServer().createInventory(this, InventoryType.DISPENSER, Component.text("Recipe"));
    }

    public ShapedRecipeMenu setRecipe(ShapedRecipe shapedRecipe) {
        if(shapedRecipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }

        if(currentShapedRecipe != null && currentShapedRecipe.equals(shapedRecipe)) {
            return this;
        }

        currentShapedRecipe = shapedRecipe;
        hasRecipeChanged = true;
        return this;
    }

    public void populateInventory() {
        if(!hasRecipeChanged) {
            return;
        }
        
        inventory.clear();
        int recipeWidth = currentShapedRecipe.getShape()[0].length();
        int recipeHeight = currentShapedRecipe.getShape().length;
        RecipeChoice[] itemArray = currentShapedRecipe.getChoiceMap().values().toArray(new RecipeChoice[0]);

        int recipeIndex = 0;
        int craftingIndex = 0;

        // If the recipe is 1 large, center horizontally the recipe in the crafting grid (eg: sword)
        if(recipeWidth == 1)
            craftingIndex++;

        // If the recipe is 1 tall, center vertically the recipe in the crafting grid (eg: slab)
        if(recipeHeight == 1)
            craftingIndex += 3;

        for(int i = 0; i < recipeHeight; i++) {
            for(int j = 0; j < recipeWidth; j++) {
                RecipeChoice recipeChoice = itemArray[recipeIndex];
                if(recipeChoice == null) {
                    craftingIndex++;
                    recipeIndex++;
                    continue;
                }
                @SuppressWarnings("deprecation")
                ItemStack item = recipeChoice.getItemStack();
                inventory.setItem(craftingIndex, item);
                craftingIndex++;
                recipeIndex++;
            }
            craftingIndex += 3 - recipeWidth; // got to the next crafting row
        }
        hasRecipeChanged = false;
    }

    /**
     * Get the object's inventory.
     * Use the currentShapedRecipe to populate the inventory. 
     */
    @Override
    public Inventory getInventory() {
        populateInventory();


        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        OfflinePlayer a = plugin.getServer().getOfflinePlayer("MHF_ArrowLeft");
        meta.setOwningPlayer(a);
        playerHead.setItemMeta(meta);

        inventory.setItem(0, playerHead);
        return inventory;
    }
}
