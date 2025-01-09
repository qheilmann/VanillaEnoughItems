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

    public void openRecipeMenu(Player player, Recipe recipe) {
        RecipeMenu recipeMenu = new RecipeMenu(plugin, this);
        recipeMenu.setRecipe(recipe);
        player.openInventory(recipeMenu.getInventory());
    }

    public void handleMenuClickEvent(InventoryClickEvent event) {

        Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;

        if(inventory.getHolder(false) instanceof IOwnedByMenu ownedByMenu) {
            IMenu menu = ownedByMenu.getOwnedMenu();
            menu.onMenuClick(event);
        }
    }
}
