package me.qheilmann.vei.Core.GUI;

import org.bukkit.inventory.Inventory;

@FunctionalInterface
public interface GuiInventoryProvider<G extends BaseGui<G, ?>> {
    Inventory create(G gui);
}
