package me.qheilmann.vei.Menu;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.foundation.gui.ActionType;
import me.qheilmann.vei.foundation.gui.GuiItemService;

public class RecipeMenu implements IMenu{
    
    private RecipeInventory recipeInventory;
    private JavaPlugin plugin;
    private MenuManager menuManager;

    public RecipeMenu(JavaPlugin plugin, MenuManager menuManager) {
        this.plugin = plugin;
        this.menuManager = menuManager;
        this.recipeInventory = new RecipeInventory(this, plugin, new GuiItemService());
    }

    public void setRecipe(Recipe recipe) {
        this.recipeInventory.setRecipe(recipe);
    }

    public Inventory getInventory() {
        return recipeInventory.getInventory();
    }

    @Override
    public void onMenuClick(InventoryClickEvent event)
    {
        event.setCancelled(true); // can be enhanced to cancel only if the click occurs inside the RecipeMenu AND does not modify the contents of the RecipeMenu (shift+click, ...)
        
        ItemStack item = event.getCurrentItem();
        if(item == null || item.isEmpty()) {
            return;
        }
        
        // Only click on something inside Menu (not empty)
        NamespacedKey key = new NamespacedKey(VanillaEnoughItems.NAMESPACE, ActionType.REFERENCE_KEY);
        boolean isActionItem = item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING);
        
        if(isActionItem) {
            // TEMP
            String ref = item.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            event.getWhoClicked().sendMessage("Action item clicked %s".formatted(ref));

            if(ref == "info"){
                ShapedRecipe craft = (ShapedRecipe)plugin.getServer().getRecipe(new NamespacedKey(VanillaEnoughItems.NAMESPACE, "warriorsword"));
                // new RecipeInterface(plugin).openInterface((Player)event.getWhoClicked(), craft);
                // event.getWhoClicked().sendMessage("yolo %d".formatted(plugin.getServer().getCurrentTick()));
                BukkitScheduler scheduler = plugin.getServer().getScheduler();
                scheduler.runTask(plugin, () -> menuManager.openRecipeMenu((Player)event.getWhoClicked(), craft)); // inside something like MenuManager ?
                scheduler.runTask(plugin, () -> event.getWhoClicked().sendMessage("yolo %d".formatted(plugin.getServer().getCurrentTick())));
            }
        }
    }

    
    @Override
    public void onMenuOpen(InventoryOpenEvent event) {
        return;
    }

    @Override
    public void onMenuClose(InventoryCloseEvent event) {
        return;
    }
}
