package me.qheilmann.vei.Menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

public class MenuManager {

    private final JavaPlugin plugin;

    public MenuManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Open the recipe menu for the given player <p>
     * For using this methode safely during an InventoryInteractEvent, 
     * schedule a task using BukkitScheduler.runTask(Plugin, Runnable), 
     * which will run the task on the next tick.
     */
    public void openRecipeMenu(Player player, Recipe recipe) {
        RecipeMenu recipeMenu = new RecipeMenu(plugin, this);
        recipeMenu.setRecipe(recipe);
        player.openInventory(recipeMenu.getInventory());
    }

    /**
     * Open the bookmark menu for the given player <p>
     * For using this methode safely during an InventoryInteractEvent, 
     * schedule a task using BukkitScheduler.runTask(Plugin, Runnable), 
     * which will run the task on the next tick.
     */
    public void openBookmarkMenu(Player player) {
        player.openAnvil(null, true); // TEMP implement bookmark menu
    }

    /**
     * Open the server bookmark menu for the given player <p>
     * For using this methode safely during an InventoryInteractEvent, 
     * schedule a task using BukkitScheduler.runTask(Plugin, Runnable), 
     * which will run the task on the next tick.
     */
    public void openServerBookmarkMenu(Player player) {
        player.openSmithingTable(null, true); // TEMP implement server bookmark menu
    }

    /**
     * Open this settings menu for the given player <p>
     * For using the methode safely during an InventoryInteractEvent, 
     * schedule a task using BukkitScheduler.runTask(Plugin, Runnable), 
     * which will run the task on the next tick.
     */
    public void openSettingsMenu(Player player) {
        player.openGrindstone(null, true); // TEMP implement settings menu
    }

    /**
     * Close the menu for the given player <p>
     * For using this methode safely during an InventoryInteractEvent, 
     * schedule a task using BukkitScheduler.runTask(Plugin, Runnable), 
     * which will run the task on the next tick.
     */
    public void closeMenu(Player player) {
        player.closeInventory();
    }

    /**
     * Handle the click event of the given inventory <p>
     * The inventory must be a menu and implement the IOwnedByMenu interface
     */
    public void handleMenuClickEvent(InventoryClickEvent event) {

        Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;

        if(inventory.getHolder(false) instanceof IOwnedByMenu ownedByMenu) {
            IMenu menu = ownedByMenu.getOwnedMenu();
            menu.onMenuClick(event);
        }
    }
}
