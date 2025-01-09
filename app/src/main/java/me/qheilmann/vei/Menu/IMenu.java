package me.qheilmann.vei.Menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public interface IMenu {

    /**
     * Handle a click event on the menu
     * Called when a player clicks in the menu
     */
    void onMenuClick(InventoryClickEvent event);

    /**
     * Handle the opening of the menu
     * Called when the menu is opened
     */
    void onMenuOpen(InventoryOpenEvent event);

    /**
     * Handle the closing of the menu
     * Called when the menu is closed
     */
    void onMenuClose(InventoryCloseEvent event);
}
