package me.qheilmann.vei.Menu;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Menu.Button.ButtonItem;
import me.qheilmann.vei.foundation.gui.GuiItemService;

public class RecipeMenu implements IMenu {

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

    public MenuManager getMenuManager() {
        return menuManager;
    }

    @Override
    public void onMenuClick(InventoryClickEvent event) {
        // can be enhanced to cancel only if the click occurs inside the RecipeMenu AND
        // does not modify the contents of the RecipeMenu (shift+click, ...)
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.isEmpty()) {
            return;
        }

        // Because of the way PaperMc/Minecraft create the inventory, all subclass of ItemStack are lost and re-create as ItemStack
        // So we can't use instanceof to detect the type of the item and check if it is a specifique subclass (ButtonItem, ...)
        // So we have to use the PersistentDataContainer to store the type of the item and retrieve the subclass

        // TODO refact the archi of the ButtonItem, subclass and ButtonType

        NamespacedKey key = new NamespacedKey(VanillaEnoughItems.NAMESPACE, ButtonItem.REFERENCE_KEY);
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        boolean isButtonItem = pdc.has(key, PersistentDataType.STRING);

        if (!isButtonItem) {
            return;
        }

        String reference = pdc.get(key, PersistentDataType.STRING);
        ButtonItem button = ButtonItem.restoreButton(reference, item, this, menuManager);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        Player player = (Player) event.getWhoClicked(); // TODO replace each Button / Manager with HumanEntity

        scheduler.runTask(plugin, () -> button.trigger(player));


        // ButtonType buttonType = ButtonType.fromReference(reference);
        // if (buttonType != null) {
        //     ButtonItem button = ButtonFactory.createButton(buttonType, item);
        //     if (button instanceof GenericButton genericButton) {
        //         scheduler.runTask(plugin, () -> genericButton.trigger(menuManager, player));
        //     }
        //     else if (button instanceof RecipeMenuButton recipeMenuButton) {
        //         scheduler.runTask(plugin, () -> recipeMenuButton.trigger(this, player));
        //     }
        //     else {
        //         throw new IllegalArgumentException("Unknown ButtonItem type");
        //     }
        // }
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
