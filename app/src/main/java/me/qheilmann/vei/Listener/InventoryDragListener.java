package me.qheilmann.vei.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;


public class InventoryDragListener implements Listener
{
    JavaPlugin plugin;

    public InventoryDragListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        callInventoryClickEventAction(event);
    }

    /**
     * Call InventoryClickEvent for each slot in the drag event
     */
    private void callInventoryClickEventAction(InventoryDragEvent event)
    {
        InventoryView view = event.getView();
        DragType dragType = event.getType();
        ClickType clickType;
        InventoryAction action;
        
        if(dragType == DragType.EVEN) {
            clickType = ClickType.LEFT;
            action = InventoryAction.PLACE_SOME;
        }
        else {
            clickType = ClickType.RIGHT;
            action = InventoryAction.PLACE_ONE;
        }

        for (int slot : event.getRawSlots()) {
            InventoryClickEvent clickEvent = new InventoryClickEvent(view, SlotType.CONTAINER, slot, clickType, action);
            plugin.getServer().getPluginManager().callEvent(clickEvent);
            if (clickEvent.isCancelled()) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
