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
import me.qheilmann.vei.Core.Recipe.Index.MixedProcessRecipeMap;
import me.qheilmann.vei.Core.Recipe.Index.ProcessRecipeSet;
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
        MixedProcessRecipeMap mixedProcessRecipeMap = VanillaEnoughItems.allRecipesMap.getMixedProcessRecipeMap(item);
        ProcessRecipeSet<?> processRecipeSet;

        if (mixedProcessRecipeMap == null) {
            player.sendMessage(Component.text("No recipe found for this item %s".formatted(item.getType().name())).color(TextColor.color(0xFB5454)));
            return;
        }

        if (process != null) {
            if (!mixedProcessRecipeMap.containsProcess(process)) {
                player.sendMessage(Component.text("The process %s does not exist for this item".formatted(process.getProcessName())).color(TextColor.color(0xFB5454)));
                return;
            }
        } else {
            process = mixedProcessRecipeMap.getAllProcess().iterator().next();
        }

        processRecipeSet = mixedProcessRecipeMap.getProcessRecipeSet(process);
        if (processRecipeSet == null) {
            throw new RuntimeException("The processRecipeSet is null, it should not be null");
        }
        int nbVariant = processRecipeSet.size();
        if (variant < 0 || variant >= nbVariant) {
            player.sendMessage(Component.text("Recipe variant %d for this item and process does not exist".formatted(variant + 1)).color(TextColor.color(0xFB5454))); // 1-based index for user
            return;
        }

        RecipeMenu recipeMenu = new RecipeMenu(style, mixedProcessRecipeMap, process, variant);
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
