package me.qheilmann.vei.Menu;


import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void onMenuClick(InventoryClickEvent event){
        return;
    }

    public void onMenuOpen(InventoryOpenEvent event){
        return;
    }

    public void onMenuClose(InventoryCloseEvent event){
        return;
    }
}
