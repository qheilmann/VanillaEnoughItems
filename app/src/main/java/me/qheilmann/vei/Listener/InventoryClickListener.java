package me.qheilmann.vei.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;

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
            menu.onMenuClick(event);
            return;
        }

        return;
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
