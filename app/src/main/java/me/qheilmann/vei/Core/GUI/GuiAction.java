package me.qheilmann.vei.Core.GUI;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiAction<E extends Event, G extends BaseGui<G>> {
    
    /*
     * Executes the action.
     * 
     * @param event the event that triggered the action
     * @param gui the gui that the action is executed on
     */
    void execute(final E event, final G gui);
}
