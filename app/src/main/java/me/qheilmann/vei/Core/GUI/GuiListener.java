package me.qheilmann.vei.Core.GUI;

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
import dev.triumphteam.gui.components.util.ItemNbt;

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

        GuiItem<G> guiItem;

        // TODO there is no PaginatedGui in the code for the moment
        // Checks whether it's a paginated gui or not
        // if (gui instanceof PaginatedGui) {
        //     final PaginatedGui paginatedGui = (PaginatedGui) gui;

        //     // Gets the gui item from the added items or the page items
        //     guiItem = paginatedGui.getGuiItem(event.getSlot());
        //     if (guiItem == null) guiItem = paginatedGui.getPageItem(event.getSlot());

        // } else {
            // The clicked GUI Item
            guiItem = gui.getGuiItem(event.getSlot());
        // }

        // Checks if the item is null (not a GUI item)
        if(guiItem == null) return;

        // TODO check if this test is necessary
        // maybe get the getItem from the shadow inventory and check if PDC uuid is present, then try to cast to GuiItem<G> and check if it's null

        // Checks if the current item clicked is the same as the item in the slot
        ItemStack currentItem = event.getCurrentItem();
        if (!isGuiItem(currentItem, guiItem)) return;

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
     * Checks if the item is or not a GUI item
     *
     * @param currentItem The current item clicked
     * @param guiItem     The GUI item in the slot
     * @return Whether it is or not a GUI item
     */
    private boolean isGuiItem(@Nullable final ItemStack currentItem, @Nullable final GuiItem<G> guiItem) {
        if (currentItem == null || guiItem == null) return false;
        // Checks whether the Item is truly a GUI Item
        final String nbt = ItemNbt.getString(currentItem, "mf-gui");
        if (nbt == null) return false;
        return nbt.equals(guiItem.getUuid().toString());
    }
}
