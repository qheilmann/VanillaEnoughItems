package me.qheilmann.vei.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.qheilmann.vei.Menu.MenuManager;

public class InventoryClickListener implements Listener
{
    private final MenuManager menuManager;

    public InventoryClickListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        menuManager.handleMenuClickEvent(event);
    }
}
