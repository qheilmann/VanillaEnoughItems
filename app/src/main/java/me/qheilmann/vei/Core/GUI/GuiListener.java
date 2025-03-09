package me.qheilmann.vei.Core.GUI;

import java.util.UUID;
import javax.annotation.Nullable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.persistence.PersistentDataContainerView;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Item.PersistentDataType.UuidPdt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The listener for all GUI events.
 * 
 * @author qhelmann but most original part come from Triumph GUI <a href="https://github.com/TriumphTeam/triumph-gui">TriumphTeam</a>
 */
public class GuiListener<G extends BaseGui<G, ?>> implements Listener {

    /**
     * Handles what happens when a player clicks on the GUI
     *
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        G gui = getGuiFromEvent(event);
        if (gui == null) return;

        Inventory clickedInventory = event.getClickedInventory();
        
        // Outside click action, then return
        if (clickedInventory == null) {
            final GuiAction<InventoryClickEvent, G> outsideClickAction = gui.getOutsideClickAction();
            if (outsideClickAction != null) {
                executeAction(outsideClickAction, event, gui, "outside click");
            }
            return;
        }
        
        InventoryType clickedInventoryType = clickedInventory.getType();

        // Default click action
        final GuiAction<InventoryClickEvent, G> defaultClick = gui.getDefaultClickAction();
        if (defaultClick != null) {
            executeAction(defaultClick, event, gui, "default click");
        }

        // Default top click action
        final GuiAction<InventoryClickEvent, G> defaultTopClick = gui.getDefaultTopClickAction();
        if (defaultTopClick != null && clickedInventoryType != InventoryType.PLAYER) {
            executeAction(defaultTopClick, event, gui, "default top click");
        }

        // Default player inventory click action
        final GuiAction<InventoryClickEvent, G> playerInventoryClick = gui.getPlayerInventoryAction();
        if (playerInventoryClick != null && clickedInventoryType == InventoryType.PLAYER) {
            executeAction(playerInventoryClick, event, gui, "player inventory click");
        }

        // Default specific slot click action
        final GuiAction<InventoryClickEvent, G> slotAction = gui.getSlotAction(event.getSlot());
        if (slotAction != null && clickedInventoryType != InventoryType.PLAYER) {
            executeAction(slotAction, event, gui, "slot click");
        }

        GuiItem<G> guiItem = gui.getGuiItem(event.getSlot());
        if(guiItem == null) return;

        // Checks if the current item clicked is the same as the item in the slot
        if (!isIdenticalItem(event.getCurrentItem(), guiItem)){
            VanillaEnoughItems.LOGGER.warn("The item clicked is not the same as the item in the same slot inside the GUI, (no-op)");
            return;
        }

        // GuiItem click action
        final GuiAction<InventoryClickEvent, G> itemAction = guiItem.getAction();
        if (itemAction != null) {
            executeAction(itemAction, event, gui, "item click");
        }
    }

    /**
     * Handles what happens when a player drag on the GUI
     *
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {
        G gui = getGuiFromEvent(event);
        if (gui == null) return;

        // Drag action
        final GuiAction<InventoryDragEvent, G> dragAction = gui.getDragAction();
        if (dragAction != null){
            executeAction(dragAction, event, gui, "drag");  
        }
    }

    /**
     * Handles what happens when the GUI is opened
     *
     * @param event The InventoryOpenEvent
     */
    @EventHandler
    public void onGuiOpen(final InventoryOpenEvent event) {
        G gui = getGuiFromEvent(event);
        if (gui == null) return;

        // Open action
        final GuiAction<InventoryOpenEvent, G> openAction = gui.getOpenGuiAction();
        if (openAction != null && !gui.isUpdating()){
            executeAction(openAction, event, gui, "open");
        }
    }

    /**
     * Handles what happens when the GUI is closed
     *
     * @param event The InventoryCloseEvent
     */
    @EventHandler
    public void onGuiClose(final InventoryCloseEvent event) {
        G gui = getGuiFromEvent(event);
        if (gui == null) return;
        
        // Close action
        final GuiAction<InventoryCloseEvent, G> closeAction = gui.getCloseGuiAction();
        if (closeAction != null && !gui.isUpdating() && gui.isCloseActionEnabled()){
            executeAction(closeAction, event, gui, "close");
        }
    }

    /**
     * Checks if the current item UUID is the same as the item returned by the
     * inventory.
     *
     * @param currentItem The current item clicked.
     * @param guiItem     The GUI item returned by the inventory.
     * @return Whether it is a GUI item or not.
     */
    private boolean isIdenticalItem(@Nullable final ItemStack currentItem, @Nullable final GuiItem<G> guiItem) {
        if (currentItem == null || guiItem == null) return false;

        PersistentDataContainerView pdc = currentItem.getPersistentDataContainer();
        if (pdc == null) return false;

        UUID uuid = pdc.get(GuiItem.UUID_KEY, UuidPdt.TYPE);
        return uuid != null && uuid.equals(guiItem.getUuid());
    }

    /**
     * Executes the action and catches any exceptions that may occur.
     *
     * @param action     The action to execute.
     * @param event      The event sent to the action.
     * @param gui        The GUI sent to the action.
     * @param actionType The type of action being executed.
     */
    private <E extends InventoryEvent> void executeAction(GuiAction<E, G> action, E event, G gui, String actionType) {
        try {
            action.execute(event, gui);
        } catch (Exception e) {
            // TODO Make a utility method for this (input: LOGGER + commonMessage + player message + log message + exception > output: server message + log message + logID + red color)
            String logID = UUID.randomUUID().toString().substring(0, 8);
            event.getView().getPlayer().sendMessage(Component.text(
                String.format(
                    "An error occurred while executing the %s action (#%s). Please contact the server administrator.",
                    actionType, logID
                ),
                NamedTextColor.RED
            ));
            VanillaEnoughItems.LOGGER.warn("An error occurred while executing the %s action (#%s) for %s".formatted(
                     actionType, logID, gui.getClass().getSimpleName()
                ),
                e
            );
        }
    }

    /**
     * Gets the GUI from the event.
     * If null is returned, the event can be ignored.
     *
     * @param event The event to get the GUI from.
     * @return The GUI from the event.
     */
    @Nullable
    private G getGuiFromEvent(InventoryEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseGui<?, ?> baseGui)) 
            return null;

        // All derived BaseGui types will correctly cast to G and function as a
        // BaseGui. Thanks to polymorphism and CRTP, only one listener is
        // needed for all GUIs.
        @SuppressWarnings("unchecked")
        G gui = (G) baseGui;
        return gui;
    }
}
