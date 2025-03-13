package me.qheilmann.vei.Menu;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Recipe.ItemRecipeMap;
import me.qheilmann.vei.Core.Style.Styles.Style;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MenuManager {

    private final JavaPlugin plugin;
    private final Style style;

    public MenuManager(JavaPlugin plugin, Style style) {
        this.plugin = plugin;
        this.style = style;
    }

    /**
     * Open the recipe menu for the given player <p>
     * For using this methode safely during an InventoryInteractEvent, 
     * schedule a task using BukkitScheduler.runTask(Plugin, Runnable), 
     * which will run the task on the next tick.
     */
    public void openRecipeMenu(@NotNull Player player, @NotNull ItemStack item, @Nullable Process<?> process, int variant) {
        ItemRecipeMap itemRecipeMap = VanillaEnoughItems.allRecipesMap.getItemRecipeMap(item);
        if (itemRecipeMap == null) {
            player.sendMessage(Component.text("No recipe found for this item").color(TextColor.color(0xFB5454)));
            return;
        }

        if (process != null) {
            if (!itemRecipeMap.containsProcess(process)) {
                player.sendMessage(Component.text("No recipe found for this process").color(TextColor.color(0xFB5454)));
                return;
            }
        } else {
            process = itemRecipeMap.getAllProcess().iterator().next();
        }

        // TODO check if the variant is valid

        RecipeMenu recipeMenu = new RecipeMenu(style, itemRecipeMap, process, variant);
        recipeMenu.open(player);
    }

    /**
     * Old way to open the recipe menu for the given player <p>
     */
    public void openRecipeMenuOld(Player player, Recipe recipe) {
        RecipeMenuOld recipeMenu = new RecipeMenuOld(plugin, this);
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
