package me.qheilmann.vei.Listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.qheilmann.vei.Menu.RecipeMenu;

public class InventoryClickListener implements Listener
{
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        recipeMenuAction(event);
    }

    private void recipeMenuAction(InventoryClickEvent event)
    {
        // If it's the a RecipeMenu click it can't be other as container or quick bar
        if(!isContainerOrQuickBar(event)) {
            return;
        }

        if(event.getInventory().getHolder() instanceof RecipeMenu menu) {
            onRecipeMenuClick(event); // TEMP menu.onInventoryClick(event); // TODO implement onInventoryClick RecipeMenu
            return;
        }

        return;
    }

    private void onRecipeMenuClick(InventoryClickEvent event)
    {
        event.setCancelled(true); // can be enhanced to cancel only if the click occurs inside the RecipeMenu AND does not modify the contents of the RecipeMenu (shift+click, ...)
        
        ItemStack item = event.getCurrentItem();
        if(item == null || item.isEmpty()) {
            return;
        }
        
        // Only click on something inside Menu (not empty)
        NamespacedKey key = new NamespacedKey("vei", "recipe_action"); // TODO replace with the namespace key from a static (vei and recipe_action)
        boolean isActionItem = item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING);

        if(isActionItem) {
            event.getWhoClicked().sendMessage("Action item clicked %s".formatted(item.getPersistentDataContainer().get(key, PersistentDataType.STRING)));
        }
    }

    /*
     * Check if the event is from a container or quick bar
     */
    private boolean isContainerOrQuickBar(InventoryClickEvent event)
    {
        SlotType slotType = event.getSlotType();

        if (slotType == SlotType.CONTAINER || event.getSlotType() == SlotType.QUICKBAR) {
            return true;
        }

        return false;
    }
}
