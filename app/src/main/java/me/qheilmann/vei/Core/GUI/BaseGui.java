package me.qheilmann.vei.Core.GUI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.components.exception.GuiException;
import dev.triumphteam.gui.components.util.VersionHelper;
import dev.triumphteam.gui.guis.GuiListener;
import dev.triumphteam.gui.guis.InteractionModifierListener;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Menu.InventoryShadow;

import net.kyori.adventure.text.Component;

/*
 * Base class that every GUI extends
 * 
 * @author Most part come from TriumphTeam <a href="https://github.com/TriumphTeam/triumph-gui">TriumphTeam</a>
 */
public abstract class BaseGui<G extends BaseGui<G>> implements InventoryHolder {

    // The plugin instance for registering the event and for the close delay.
    private static final Plugin plugin = VanillaEnoughItems.getPlugin(VanillaEnoughItems.class);


    private static Method GET_SCHEDULER_METHOD = null;
    private static Method EXECUTE_METHOD = null;

    // Registering the listener class.
    static {
        try {
            GET_SCHEDULER_METHOD = Entity.class.getMethod("getScheduler");
            final Class<?> entityScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            EXECUTE_METHOD = entityScheduler.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, long.class);
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {
        }

        Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new InteractionModifierListener(), plugin);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseGui<T>> boolean instanceOfBaseGui(@NotNull Object object) {
        if (object == null) {
            return false;
        }

        Class<BaseGui<T>> baseGui = (Class<BaseGui<T>>) (Class<?>) BaseGui.class;
        return baseGui.isAssignableFrom(object.getClass());
    }
    
    public static <T> boolean isInstanceOfBaseGui(T obj) {
        return obj instanceof BaseGui<?>;
    }

    /**
     * Copy a set into an EnumSet, required because {@link EnumSet#copyOf(EnumSet)} throws an exception if the collection passed as argument is empty.
     *
     * @param set The set to be copied.
     * @return An EnumSet with the provided elements from the original set.
     */
    @NotNull
    private static Set<InteractionModifier> safeCopyOf(@NotNull final Set<InteractionModifier> set) {
        return set.isEmpty() ? EnumSet.noneOf(InteractionModifier.class) : EnumSet.copyOf(set);
    }

    // Main inventory who also holds the GuiItems
    private InventoryShadow<Inventory> inventory;
    // Contains all items the GUI will have // to remove
    private Map<Integer, GuiItem<G>> guiItems;
    // Gui filler.
    private final GuiFiller<G> filler = new GuiFiller<G>(this);
    // Actions for specific slots.
    private final Map<Integer, GuiAction<InventoryClickEvent, G>> slotActions;
    // Interaction modifiers.
    private final Set<InteractionModifier> interactionModifiers;
    // title
    private Component title;
    // Rows of the GUI. (if chest)
    private int rows = 1;
    // Gui type
    private GuiType guiType;
    // Action to execute when clicking on any item.
    private GuiAction<InventoryClickEvent, G> defaultClickAction;
    // Action to execute when clicking on the top part of the GUI only.
    private GuiAction<InventoryClickEvent, G> defaultTopClickAction;
    // Action to execute when clicking on the player Inventory.
    private GuiAction<InventoryClickEvent, G> playerInventoryAction;
    // Action to execute when dragging the item on the GUI.
    private GuiAction<InventoryDragEvent, G> dragAction;
    // Action to execute when GUI closes.
    private GuiAction<InventoryCloseEvent, G> closeGuiAction;
    // Action to execute when GUI opens.
    private GuiAction<InventoryOpenEvent, G> openGuiAction;
    // Action to execute when clicked outside the GUI.
    private GuiAction<InventoryClickEvent, G> outsideClickAction;
    // Whether the GUI is updating.
    private boolean updating;
    // Whether should run the actions from the close and open methods.
    private boolean runCloseAction = true;
    private boolean runOpenAction = true;

    /**
     * The main constructor (Chest with x rows).
     *
     * @param rows                 The amount of rows to use.
     * @param title                The GUI title.
     * @param interactionModifiers Modifiers to select which interactions are allowed.
     * @since 3.0.0.
     */
    public BaseGui(final int rows, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        this(GuiType.CHEST, title, interactionModifiers, rows);
    }

    /**
     * Alternative constructor that takes {@link GuiType} instead of rows number.
     *
     * @param guiType              The {@link GuiType} to use.
     * @param title                The GUI title.
     * @param interactionModifiers Modifiers to select which interactions are allowed.
     * @since 3.0.0
     */
    public BaseGui(@NotNull final GuiType guiType, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        this(guiType, title, interactionModifiers, 6);
    }

    private BaseGui(@NotNull final GuiType guiType, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers, int rows) {
        int inventorySize;
        if(guiType == GuiType.CHEST) {
            this.rows = (rows >= 1 && rows <= 6) ? rows : 1;
            inventorySize = this.rows * 9;
        } else {
            rows = 0;
            inventorySize = guiType.getLimit();
        }
        this.guiType = guiType;
        this.interactionModifiers = safeCopyOf(interactionModifiers);
        this.title = title;
        this.inventory = new InventoryShadow<Inventory>(Bukkit.createInventory(this, inventorySize, title));
        this.slotActions = new LinkedHashMap<>(inventorySize);
    }

    /**
     * Gets the GUI's title.
     *
     * @return The GUI's title.
     */
    @NotNull
    public Component getTitle() {
        return title;
    }

    /**
     * Sets the {@link GuiItem} to a specific slot on the GUI.
     *
     * @param slot    The GUI slot.
     * @param guiItem The {@link GuiItem} to add to the slot.
     */
    public void setItem(final int slot, @NotNull final GuiItem<G> guiItem) {
        validateSlot(slot);
        inventory.setItem(slot, guiItem);
    }

    /**
     * Alternative {@link #setItem(int, GuiItem)} to set item that takes a {@link List} of slots instead.
     *
     * @param slots   The slots in which the item should go.
     * @param guiItem The {@link GuiItem} to add to the slots.
     */
    public void setItem(@NotNull final List<Integer> slots, @NotNull final GuiItem<G> guiItem) {
        for (final int slot : slots) {
            setItem(slot, guiItem);
        }
    }

    /**
     * Alternative {@link #setItem(int, GuiItem)} to set item that uses <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     *
     * @param row     The GUI row number.
     * @param col     The GUI column number.
     * @param guiItem The {@link GuiItem} to add to the slot.
     */
    public void setItem(final int row, final int col, @NotNull final GuiItem<G> guiItem) {
        setItem(getSlotFromRowCol(row, col), guiItem);
    }

    /**
     * Removes the given ItemStacks from the storage contents of the inventory<p>
     * It will try to remove 'as much as possible' from the types and amounts you give as arguments.
     * 
     * @return A HashMap containing the ItemStacks (not GuiItem) that were not removed, or null if all stacks were removed.
     * @see Inventory#removeItem(ItemStack)
     */
    @SafeVarargs
    public final HashMap<Integer, ItemStack> removeItem(@NotNull final GuiItem<G>... item) {
        return inventory.removeItem(item);
    }

    /**
     * Removes the {@link GuiItem} in the specific slot.
     *
     * @param slot The GUI slot.
     */
    public void removeSlot(final int slot) {
        validateSlot(slot);
        inventory.setItem(slot, null);
    }

    /**
     * Alternative {@link #removeSlot(int)} with cols and rows.
     *
     * @param row The row.
     * @param col The column.
     */
    public void removeSlot(final int row, final int col) {
        removeSlot(getSlotFromRowCol(row, col));
    }

    /**
     * Stores the given ItemStacks in the inventory. 
     * This will try to fill existing stacks and empty slots as well as it can.
     * <p>
     * The returned HashMap contains what it couldn't store, where the key is the index of the parameter,
     * and the value is the ItemStack at that index of the varargs parameter.
     * If all items are stored, it will return an empty HashMap.
     * 
     * @return A HashMap containing the ItemStacks (not GuiItem) that were not stored, or null if all stacks were stored.
     * @see Inventory#addItem(ItemStack...)
     */
    @SafeVarargs
    public final void addItem(@NotNull final GuiItem<G>... items) {
        inventory.addItem(items);
    }

    /**
     * Adds a {@link GuiAction} for when clicking on a specific slot.
     * See {@link InventoryClickEvent}.
     *
     * @param slot       The slot that will trigger the {@link GuiAction}.
     * @param slotAction {@link GuiAction} to resolve when clicking on specific slots.
     */
    public void addSlotAction(final int slot, @Nullable final GuiAction<@NotNull InventoryClickEvent, G> slotAction) {
        validateSlot(slot);
        slotActions.put(slot, slotAction);
    }

    /**
     * Alternative method for {@link #addSlotAction(int, GuiAction)} to add a {@link GuiAction} to a specific slot using <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     * See {@link InventoryClickEvent}.
     *
     * @param row        The row of the slot.
     * @param col        The column of the slot.
     * @param slotAction {@link GuiAction} to resolve when clicking on the slot.
     */
    public void addSlotAction(final int row, final int col, @Nullable final GuiAction<@NotNull InventoryClickEvent, G> slotAction) {
        addSlotAction(getSlotFromRowCol(row, col), slotAction);
    }

    /**
     * Gets a specific {@link GuiItem} on the slot.
     *
     * @param slot The slot of the item.
     * @return The {@link GuiItem} on the introduced slot or {@code null} if not a GuiItem or if doesn't exist.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public GuiItem<G> getGuiItem(final int slot) {
        ItemStack itemstack = inventory.getItem(slot);

        Class<GuiItem<G>> clazz = (Class<GuiItem<G>>) (Class<?>) GuiItem.class;
        if (itemstack != null && clazz.isInstance(itemstack)) {
            return (GuiItem<G>) itemstack;
        }

        return null;
    }

    /**
     * Checks whether or not the GUI is updating.
     *
     * @return Whether the GUI is updating or not.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isUpdating() {
        return updating;
    }

    /**
     * Sets the updating status of the GUI.
     *
     * @param updating Sets the GUI to the updating status.
     */
    public void setUpdating(final boolean updating) {
        this.updating = updating;
    }

    /**
     * Opens the GUI for a {@link HumanEntity}.
     *
     * @param player The {@link HumanEntity} to open the GUI to.
     */
    public void open(@NotNull final HumanEntity player) {
        if (player.isSleeping()) return;
        player.openInventory(inventory);
    }

    /**
     * Closes the GUI with a {@code 2 tick} delay (to prevent items from being taken from the {@link Inventory}).
     *
     * @param player The {@link HumanEntity} to close the GUI to.
     */
    public void close(@NotNull final HumanEntity player) {
        close(player, true);
    }

    /**
     * Closes the GUI with a {@code 2 tick} delay (to prevent items from being taken from the {@link Inventory}).
     *
     * @param player         The {@link HumanEntity} to close the GUI to.
     * @param runCloseAction If should or not run the close action.
     */
    public void close(@NotNull final HumanEntity player, final boolean runCloseAction) {
        Runnable task = () -> {
            this.runCloseAction = runCloseAction;
            player.closeInventory();
            this.runCloseAction = true;
        };

        if (VersionHelper.IS_FOLIA) {
            if (GET_SCHEDULER_METHOD == null || EXECUTE_METHOD == null) {
                throw new GuiException("Could not find Folia Scheduler methods.");
            }

            try {
                EXECUTE_METHOD.invoke(GET_SCHEDULER_METHOD.invoke(player), plugin, task, null, 2L);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GuiException("Could not invoke Folia task.", e);
            }
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, task, 2L);
    }

    /**
     * Updates the GUI for all the {@link Inventory} views.
     */
    public void update() {
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) ((Player) viewer).updateInventory();
    }

    /**
     * Updates the title of the GUI.
     * <i>This method may cause LAG if used on a loop</i>.
     *
     * @param title The title to set.
     * @return The GUI for easier use when declaring, works like a builder.
     */
    @Contract("_ -> this")
    @NotNull
    public BaseGui<G> updateTitle(@NotNull final Component title) {
        updating = true;

        final List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());

        inventory = new InventoryShadow<Inventory>(Bukkit.createInventory(this, inventory.getSize(), title));

        for (final HumanEntity player : viewers) {
            open(player);
        }

        updating = false;
        this.title = title;
        return this;
    }

    /**
     * Disable item placement inside the GUI.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> disableItemPlace() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    /**
     * Disable item retrieval inside the GUI.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> disableItemTake() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    /**
     * Disable item swap inside the GUI.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> disableItemSwap() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    /**
     * Disable item drop inside the GUI
     *
     * @return The BaseGui
     * @since 3.0.3.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> disableItemDrop() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    /**
     * Disable other GUI actions
     * This option pretty much disables creating a clone stack of the item
     *
     * @return The BaseGui
     * @since 3.0.4
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> disableOtherActions() {
        interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    /**
     * Disable all the modifications of the GUI, making it immutable by player interaction.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> disableAllInteractions() {
        interactionModifiers.addAll(InteractionModifier.VALUES);
        return this;
    }

    /**
     * Allows item placement inside the GUI.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> enableItemPlace() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    /**
     * Allow items to be taken from the GUI.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> enableItemTake() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    /**
     * Allows item swap inside the GUI.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> enableItemSwap() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    /**
     * Allows item drop inside the GUI
     *
     * @return The BaseGui
     * @since 3.0.3
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> enableItemDrop() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    /**
     * Enable other GUI actions
     * This option pretty much enables creating a clone stack of the item
     *
     * @return The BaseGui
     * @since 3.0.4
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> enableOtherActions() {
        interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    /**
     * Enable all modifications of the GUI, making it completely mutable by player interaction.
     *
     * @return The BaseGui.
     * @author SecretX.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> this")
    public BaseGui<G> enableAllInteractions() {
        interactionModifiers.clear();
        return this;
    }

    public boolean isAllInteractionsDisabled() {
        return interactionModifiers.size() == InteractionModifier.VALUES.size();
    }

    /**
     * Check if item placement is allowed inside this GUI.
     *
     * @return True if item placement is allowed for this GUI.
     * @author SecretX.
     * @since 3.0.0.
     */
    public boolean canPlaceItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
    }

    /**
     * Check if item retrieval is allowed inside this GUI.
     *
     * @return True if item retrieval is allowed inside this GUI.
     * @author SecretX.
     * @since 3.0.0.
     */
    public boolean canTakeItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
    }

    /**
     * Check if item swap is allowed inside this GUI.
     *
     * @return True if item swap is allowed for this GUI.
     * @author SecretX.
     * @since 3.0.0.
     */
    public boolean canSwapItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
    }

    /**
     * Check if item drop is allowed inside this GUI
     *
     * @return True if item drop is allowed for this GUI
     * @since 3.0.3
     */
    public boolean canDropItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
    }

    /**
     * Check if any other actions are allowed in this GUI
     *
     * @return True if other actions are allowed
     * @since 3.0.4
     */
    public boolean canDoOtherActions() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
    }

    /**
     * Gets the {@link GuiFiller} that it's used for filling up the GUI in specific ways.
     *
     * @return The {@link GuiFiller}.
     */
    @NotNull
    public GuiFiller<G> getFiller() {
        return filler;
    }

    /**
     * Gets an immutable {@link Map} with all the GUI items.
     *
     * @return The {@link Map} with all the {@link #guiItems}.
     */
    @NotNull
    public Map<@NotNull Integer, @NotNull GuiItem<G>> getGuiItems() {
        return guiItems;
    }

    /**
     * Gets the main {@link Inventory} of this GUI.
     *
     * @return Gets the {@link Inventory} from the holder.
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the new inventory of the GUI.
     *
     * @param inventory The new inventory.
     */
    public void setInventory(@NotNull final InventoryShadow<Inventory> inventory) {
        this.inventory = inventory;
    }

    /**
     * Gets the amount of {@link #rows}.
     *
     * @return The {@link #rows} of the GUI.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the {@link GuiType} in use.
     *
     * @return The {@link GuiType}.
     */
    @NotNull
    public GuiType guiType() {
        return guiType;
    }

    /**
     * Gets the default click resolver.
     */
    @Nullable
    GuiAction<InventoryClickEvent, G> getDefaultClickAction() {
        return defaultClickAction;
    }

    /**
     * Sets the {@link GuiAction} of a default click on any item.
     * See {@link InventoryClickEvent}.
     *
     * @param defaultClickAction {@link GuiAction} to resolve when any item is clicked.
     */
    public void setDefaultClickAction(@Nullable final GuiAction<@NotNull InventoryClickEvent, G> defaultClickAction) {
        this.defaultClickAction = defaultClickAction;
    }

    /**
     * Gets the default top click resolver.
     */
    @Nullable
    GuiAction<InventoryClickEvent, G> getDefaultTopClickAction() {
        return defaultTopClickAction;
    }

    /**
     * Sets the {@link GuiAction} of a default click on any item on the top part of the GUI.
     * Top inventory being for example chests etc, instead of the {@link Player} inventory.
     * See {@link InventoryClickEvent}.
     *
     * @param defaultTopClickAction {@link GuiAction} to resolve when clicking on the top inventory.
     */
    public void setDefaultTopClickAction(@Nullable final GuiAction<@NotNull InventoryClickEvent, G> defaultTopClickAction) {
        this.defaultTopClickAction = defaultTopClickAction;
    }

    /**
     * Gets the player inventory action.
     */
    @Nullable
    GuiAction<InventoryClickEvent, G> getPlayerInventoryAction() {
        return playerInventoryAction;
    }

    public void setPlayerInventoryAction(@Nullable final GuiAction<@NotNull InventoryClickEvent, G> playerInventoryAction) {
        this.playerInventoryAction = playerInventoryAction;
    }

    /**
     * Gets the default drag resolver.
     */
    @Nullable
    GuiAction<InventoryDragEvent, G> getDragAction() {
        return dragAction;
    }

    /**
     * Sets the {@link GuiAction} of a default drag action.
     * See {@link InventoryDragEvent}.
     *
     * @param dragAction {@link GuiAction} to resolve.
     */
    public void setDragAction(@Nullable final GuiAction<@NotNull InventoryDragEvent, G> dragAction) {
        this.dragAction = dragAction;
    }

    /**
     * Gets the close gui resolver.
     */
    @Nullable
    GuiAction<InventoryCloseEvent, G> getCloseGuiAction() {
        return closeGuiAction;
    }

    /**
     * Sets the {@link GuiAction} to run once the inventory is closed.
     * See {@link InventoryCloseEvent}.
     *
     * @param closeGuiAction {@link GuiAction} to resolve when the inventory is closed.
     */
    public void setCloseGuiAction(@Nullable final GuiAction<@NotNull InventoryCloseEvent, G> closeGuiAction) {
        this.closeGuiAction = closeGuiAction;
    }

    /**
     * Gets the open gui resolver.
     */
    @Nullable
    GuiAction<InventoryOpenEvent, G> getOpenGuiAction() {
        return openGuiAction;
    }

    /**
     * Sets the {@link GuiAction} to run when the GUI opens.
     * See {@link InventoryOpenEvent}.
     *
     * @param openGuiAction {@link GuiAction} to resolve when opening the inventory.
     */
    public void setOpenGuiAction(@Nullable final GuiAction<@NotNull InventoryOpenEvent, G> openGuiAction) {
        this.openGuiAction = openGuiAction;
    }

    /**
     * Gets the resolver for the outside click.
     */
    @Nullable
    GuiAction<InventoryClickEvent, G> getOutsideClickAction() {
        return outsideClickAction;
    }

    /**
     * Sets the {@link GuiAction} to run when clicking on the outside of the inventory.
     * See {@link InventoryClickEvent}.
     *
     * @param outsideClickAction {@link GuiAction} to resolve when clicking outside of the inventory.
     */
    public void setOutsideClickAction(@Nullable final GuiAction<@NotNull InventoryClickEvent, G> outsideClickAction) {
        this.outsideClickAction = outsideClickAction;
    }

    /**
     * Gets the action for the specified slot.
     *
     * @param slot The slot clicked.
     */
    @Nullable
    GuiAction<InventoryClickEvent, G> getSlotAction(final int slot) {
        return slotActions.get(slot);
    }

    boolean isCloseActionEnabled() {
        return runCloseAction;
    }

    boolean isOpenActionEnabled() {
        return runOpenAction;
    }

    /**
     * Gets the slot from the row and column passed.
     *
     * @param row The row.
     * @param col The column.
     * @return The slot needed.
     */
    int getSlotFromRowCol(final int row, final int col) {
        return col + row * 9;
    }

    /**
     * Checks if the slot introduces is a valid slot.
     *
     * @param slot The slot to check.
     */
    private void validateSlot(final int slot) {
        final int limit = guiType.getLimit();

        if (guiType == GuiType.CHEST) {
            if (slot < 0 || slot >= rows * limit) throwInvalidSlot(slot);
            return;
        }

        if (slot < 0 || slot > limit) throwInvalidSlot(slot);
    }

    /**
     * Throws an exception if the slot is invalid.
     *
     * @param slot The specific slot to display in the error message.
     */
    private void throwInvalidSlot(final int slot) {
        if (guiType == GuiType.CHEST) {
            throw new GuiException("Slot " + slot + " is not valid for the gui type - " + guiType.name() + " and rows - " + rows + "!");
        }

        throw new GuiException("Slot " + slot + " is not valid for the gui type - " + guiType.name() + "!");
    }

}
