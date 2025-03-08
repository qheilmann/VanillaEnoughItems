package me.qheilmann.vei.Core.GUI;

import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GuiAction<E extends InventoryEvent, G extends BaseGui<G, ?>> {
    
    /*
     * Executes the action.
     * 
     * @param event the event that triggered the action
     * @param gui the gui that the action is executed on
     */
    void execute(@NotNull final E event, final G gui);
}
