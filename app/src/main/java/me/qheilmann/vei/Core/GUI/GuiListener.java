package me.qheilmann.vei.Core.GUI;

import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.persistence.PersistentDataContainerView;
import me.qheilmann.vei.Core.Item.PersistentDataType.UuidPdt;

/**
 * 
 * @author Most original part come from Triumph GUI <a href="https://github.com/TriumphTeam/triumph-gui">TriumphTeam</a>
 */
public class GuiListener<G extends BaseGui<G>> implements Listener {
    
    /**
     * Handles what happens when a player clicks on the GUI
     *
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        
        final InventoryHolder holder = event.getInventory().getHolder();
        
        // Checks if the inventory holder is a BaseGui<?>
        if (! (holder instanceof BaseGui<?> baseGui)) 
            return;

        // Casts the holder to a BaseGui<G> or G (it's the same)
        @SuppressWarnings("unchecked")
        G gui = (G) baseGui;

        // START

        // Executes the outside click action
        final GuiAction<InventoryClickEvent, G> outsideClickAction = gui.getOutsideClickAction();
        if (outsideClickAction != null && event.getClickedInventory() == null) {
            outsideClickAction.execute(event, gui);
            return;
        }

        if (event.getClickedInventory() == null) return;

        // Default click action and checks weather or not there is a default action and executes it
        final GuiAction<InventoryClickEvent, G> defaultTopClick = gui.getDefaultTopClickAction();
        if (defaultTopClick != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            defaultTopClick.execute(event, gui);
        }

        // Default click action and checks weather or not there is a default action and executes it
        final GuiAction<InventoryClickEvent, G> playerInventoryClick = gui.getPlayerInventoryAction();
        if (playerInventoryClick != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            playerInventoryClick.execute(event, gui);
        }

        // Default click action and checks weather or not there is a default action and executes it
        final GuiAction<InventoryClickEvent, G> defaultClick = gui.getDefaultClickAction();
        if (defaultClick != null) defaultClick.execute(event, gui);

        // Slot action and checks weather or not there is a slot action and executes it
        final GuiAction<InventoryClickEvent, G> slotAction = gui.getSlotAction(event.getSlot());
        if (slotAction != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            slotAction.execute(event, gui);
        }

        GuiItem<G> guiItem = gui.getGuiItem(event.getSlot());
        if(guiItem == null) return;

        // Checks if the current item clicked is the same as the item in the slot
        if (!isIdenticalItem(event.getCurrentItem(), guiItem)) return;

        // Executes the action of the item
        final GuiAction<InventoryClickEvent, G> itemAction = guiItem.getAction();
        if (itemAction != null) itemAction.execute(event, gui); // TODO maybe set try catch if the user action make unexpected error
    }

    /**
     * Handles what happens when a player clicks on the GUI
     *
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {

        // TODO reduce code duplication
        final InventoryHolder holder = event.getInventory().getHolder();
        
        // Checks if the inventory holder is a BaseGui<?>
        if (! (holder instanceof BaseGui<?> baseGui)) 
            return;

        // Casts the holder to a BaseGui<G> or G (it's the same)
        @SuppressWarnings("unchecked")
        G gui = (G) baseGui;

        // Default click action and checks weather or not there is a default action and executes it
        final GuiAction<InventoryDragEvent, G> dragAction = gui.getDragAction();
        if (dragAction != null) dragAction.execute(event, gui);
    }

    /**
     * Handles what happens when the GUI is closed
     *
     * @param event The InventoryCloseEvent
     */
    @EventHandler
    public void onGuiClose(final InventoryCloseEvent event) {
        
        final InventoryHolder holder = event.getInventory().getHolder();
        
        // Checks if the inventory holder is a BaseGui<?>
        if (! (holder instanceof BaseGui<?> baseGui)) 
            return;

        // Casts the holder to a BaseGui<G> or G (it's the same)
        @SuppressWarnings("unchecked")
        G gui = (G) baseGui;

        // The GUI action for closing
        final GuiAction<InventoryCloseEvent, G> closeAction = gui.getCloseGuiAction();

        // Checks if there is or not an action set and executes it
        if (closeAction != null && !gui.isUpdating() && gui.isCloseActionEnabled())
            closeAction.execute(event, gui);
    }

    /**
     * Handles what happens when the GUI is opened
     *
     * @param event The InventoryOpenEvent
     */
    @EventHandler
    public void onGuiOpen(final InventoryOpenEvent event) {

        final InventoryHolder holder = event.getInventory().getHolder();
        
        // Checks if the inventory holder is a BaseGui<?>
        if (! (holder instanceof BaseGui<?> baseGui)) 
            return;

        // Casts the holder to a BaseGui<G> or G (it's the same)
        @SuppressWarnings("unchecked")
        G gui = (G) baseGui;

        // The GUI action for opening
        final GuiAction<InventoryOpenEvent, G> openAction = gui.getOpenGuiAction();

        // Checks if there is or not an action set and executes it
        if (openAction != null && !gui.isUpdating())
            openAction.execute(event, gui);
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
}
