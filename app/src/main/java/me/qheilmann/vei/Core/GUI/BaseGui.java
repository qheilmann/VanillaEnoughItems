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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.components.exception.GuiException;
import dev.triumphteam.gui.components.util.VersionHelper;
import dev.triumphteam.gui.guis.InteractionModifierListener;
import me.qheilmann.vei.Core.Slot.Slot;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.Menu.InventoryShadow;
import net.kyori.adventure.text.Component;

/*
 * Base class that every GUI extends
 * 
 * @author Most original part come from Triumph GUI <a href="https://github.com/TriumphTeam/triumph-gui">TriumphTeam</a>
 */
public abstract class BaseGui<G extends BaseGui<G, S>, S extends Slot> implements InventoryHolder {

    // The plugin instance for the close delay.
    protected static Plugin plugin = null;
    private static boolean isEnabled;

    private static Method GET_SCHEDULER_METHOD = null;
    private static Method EXECUTE_METHOD = null;

    static {
        try {
            GET_SCHEDULER_METHOD = Entity.class.getMethod("getScheduler");
            final Class<?> entityScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            EXECUTE_METHOD = entityScheduler.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, long.class);
        } 
        catch (NoSuchMethodException | ClassNotFoundException ignored)
        {}
    }

    // Main inventory who also holds the GuiItems
    private InventoryShadow<Inventory> inventory;
    // inventory provider
    private final GuiInventoryProvider<G> inventoryProvider;
    // Actions for specific slots.
    private final Map<S, GuiAction<InventoryClickEvent, G>> slotActions;
    // Dummy slot action, this element is the slot action access interface
    // It can change index each time an action slot is accessed with a index
    private S dummyActionSlot = null;
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
     * Creates a BaseGui with the wrapped inventory and the selected
     * interaction modifiers.
     *
     * @param wrappedInventoryProvider The supplier for the wrapped inventory.
     * @param interactionModifiers Modifiers to select which interactions are allowed.
     */
    @SuppressWarnings("unchecked")
    protected BaseGui(final @NotNull GuiInventoryProvider<G> wrappedInventoryProvider, @NotNull final Set<@NotNull InteractionModifier> interactionModifiers) {
        if(!isEnabled) {
            throw new GuiException("The BaseGui is not enabled. Use BaseGui.onEnable(Plugin) to enable it before creating a GUI instance.");
        }

        this.interactionModifiers = safeCopyOf(interactionModifiers);
        this.inventoryProvider = wrappedInventoryProvider;
        Inventory wrappedInventory = this.inventoryProvider.create((G)this); // safe because of CRTP
        this.inventory = new InventoryShadow<Inventory>(wrappedInventory);
        this.slotActions = new LinkedHashMap<>(wrappedInventory.getSize());
    }

    public static void onEnable(@NotNull final Plugin plugin) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null.");

        BaseGui.plugin = plugin;
        BaseGui.registerListener(BaseGui.plugin);

        isEnabled = true;
    }

    /**
     * Copy a set into an EnumSet, required because {@link EnumSet#copyOf(EnumSet)} throws an exception if the collection passed as argument is empty.
     *
     * @param set The set to be copied.
     * @return An EnumSet with the provided elements from the original set.
     */
    private static @NotNull Set<@NotNull InteractionModifier> safeCopyOf(@NotNull final Set<@NotNull InteractionModifier> set) {
        return set.isEmpty() ? EnumSet.noneOf(InteractionModifier.class) : EnumSet.copyOf(set);
    }

    /**
     * Registers the listener for the GUI.
     */
    private static void registerListener(@NotNull final Plugin plugin) {
        Preconditions.checkState(plugin != null, "Plugin cannot be null.");

        Bukkit.getPluginManager().registerEvents(new GuiListener<>(), plugin);
        Bukkit.getPluginManager().registerEvents(new InteractionModifierListener(), plugin);
    }

    /**
     * Gets the GUI's title.
     *
     * @return The GUI's title.
     */
    @NotNull
    protected Component getTitle() {
        return title;
    }

    /**
     * Sets the {@link GuiItem} to a specific slot on the GUI.
     *
     * @param slot    The GUI slot.
     * @param guiItem The {@link GuiItem} to add to the slot.
     */
    protected void setItem(final S slot, @Nullable final GuiItem<G> guiItem) {
        inventory.setItem(slot.getIndex(), guiItem);
    }

    /**
     * Alternative {@link #setItem(int, GuiItem)} to set item that takes a {@link List} of slots instead.
     *
     * @param slots   The slots in which the item should go.
     * @param guiItem The {@link GuiItem} to add to the slots.
     */
    protected void setItem(@NotNull final SlotSequence<S> slots, @NotNull final GuiItem<G> guiItem) {
        Preconditions.checkArgument(slots != null, "Slots cannot be null.");

        for (final S slot : slots) {
            setItem(slot, guiItem);
        }
    }

    /**
     * Removes the given ItemStacks from the storage contents of the inventory<p>
     * It will try to remove 'as much as possible' from the types and amounts you give as arguments.
     * 
     * @return A HashMap containing the ItemStacks (not GuiItem) that were not removed, or null if all stacks were removed.
     * @see Inventory#removeItem(ItemStack)
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected HashMap<Integer, ItemStack> removeItem(@NotNull final List<GuiItem<G>> items) {
        Preconditions.checkNotNull(items, "Items cannot be null.");
        GuiItem<G>[] itemsArray = items.toArray(new GuiItem[0]);
        return inventory.removeItem(itemsArray);
    }

    /**
     * Removes the {@link GuiItem} in the specific slot.
     *
     * @param slot The GUI slot.
     */
    protected void removeSlot(final S slot) {
        inventory.setItem(slot.getIndex(), null);
    }

    /**
     * Replace all the items matching the pattern item with the new item.
     * The pattern can be null to replace all the empty slots.
     *
     * @param patternItem The item to replace.
     * @param newItem     The new item to replace with.
     */
    protected void replaceItem(@Nullable final GuiItem<G> patternItem, @NotNull final GuiItem<G> newItem) {
        for (int i = 0; i < inventory.getSize(); i++) {
            replaceItemInSlot(i, patternItem, newItem);
        }
    }

    /**
     * Replace all the items matching the pattern which are in the slots sequence
     * with the new item. The pattern can be null to replace all the empty slots.
     *
     * @param slots       The slots to search for the pattern item.
     * @param patternItem The item to replace.
     * @param newItem     The new item to replace with.
     */
    protected void replaceItem(@NotNull final SlotSequence<S> slots, @Nullable final GuiItem<G> patternItem, @NotNull final GuiItem<G> newItem) {
        Preconditions.checkNotNull(slots, "Slots cannot be null.");

        for (final S slot : slots) {
            replaceItemInSlot(slot.getIndex(), patternItem, newItem);
        }
    }

    private void replaceItemInSlot(int index, @Nullable GuiItem<G> patternItem, @NotNull GuiItem<G> newItem) {
        boolean shouldReplaceNull = patternItem == null;
        ItemStack oldItem = inventory.getItem(index);
        if ((shouldReplaceNull && oldItem == null) || (oldItem != null && oldItem.equals(patternItem))) {
            inventory.setItem(index, newItem);
        }
    }

    /**
     * Fills all the empty slots with the given item.
     *
     * @param guiItem The item to fill the empty slots with.
     */
    protected void fillEmpty(@Nullable final GuiItem<G> guiItem) {
        replaceItem(null, guiItem);
    }

    /**
     * Fills all the empty slots in the given sequence with the given item.
     *
     * @param slots   The slots to fill with the item.
     * @param guiItem The item to fill the empty slots with.
     */
    protected void fillEmpty(@NotNull final SlotSequence<S> slots, @NotNull final GuiItem<G> guiItem) {
        replaceItem(slots, null, guiItem);
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
    @SuppressWarnings("unchecked")
    @NotNull
    protected HashMap<Integer, ItemStack> addItem(@NotNull final List<GuiItem<G>> items) {
        Preconditions.checkNotNull(items, "Items cannot be null.");
        GuiItem<@NotNull G>[] itemsArray = items.toArray(new GuiItem[0]);
        return inventory.addItem(itemsArray);
    }

    /**
     * Assigns a {@link GuiAction} to be executed when a specific slot is clicked.
     *
     * @param slot       The slot that will trigger the {@link GuiAction}.
     * @param slotAction The {@link GuiAction} to execute when the specified slot is clicked.
     */
    protected void setSlotAction(final S slot, @Nullable final GuiAction<InventoryClickEvent, G> slotAction) {
        if (dummyActionSlot == null) {
            dummyActionSlot = slot;
        }
        slotActions.put(slot, slotAction);
    }

    /**
     * Gets a specific {@link GuiItem} on the slot.
     *
     * @param slot The slot of the item.
     * @return The {@link GuiItem} on the introduced slot or {@code null} if not a GuiItem or if doesn't exist.
     */
    @Nullable
    protected GuiItem<G> getGuiItem(@NotNull final S slot) {
        Preconditions.checkNotNull(slot, "Slot cannot be null.");
        return this.getGuiItem(slot.getIndex());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected GuiItem<G> getGuiItem(final int slot) {
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
    protected boolean isUpdating() {
        return updating;
    }

    /**
     * Sets the updating status of the GUI.
     *
     * @param updating Sets the GUI to the updating status.
     */
    protected void setUpdating(final boolean updating) {
        this.updating = updating;
    }

    /**
     * Opens the GUI for a {@link HumanEntity}.
     *
     * @param humanEntity The {@link HumanEntity} to open the GUI to.
     */
    @Nullable
    public InventoryView open(@NotNull final HumanEntity humanEntity) {
        Preconditions.checkArgument(humanEntity != null, "Player cannot be null.");

        if (humanEntity.isSleeping())
            return null;

        return humanEntity.openInventory(inventory.getOriginalInventory());
    }

    /**
     * Closes the GUI with a {@code 2 tick} delay (to prevent items from being taken from the {@link Inventory}).
     *
     * @param humanEntity The {@link HumanEntity} to close the GUI to.
     */
    public void close(@NotNull final HumanEntity humanEntity) {
        close(humanEntity, true);
    }

    /**
     * Closes the GUI with a {@code 2 tick} delay (to prevent items from being taken from the {@link Inventory}).
     *
     * @param humanEntity         The {@link HumanEntity} to close the GUI to.
     * @param runCloseAction If should or not run the close action.
     */
    public void close(@NotNull final HumanEntity humanEntity, final boolean runCloseAction) {
        Preconditions.checkArgument(humanEntity != null, "HumanEntity cannot be null.");

        Runnable task = () -> {
            this.runCloseAction = runCloseAction;
            humanEntity.closeInventory();
            this.runCloseAction = true;
        };

        if (VersionHelper.IS_FOLIA) {
            if (GET_SCHEDULER_METHOD == null || EXECUTE_METHOD == null) {
                throw new GuiException("Could not find Folia Scheduler methods.");
            }

            try {
                EXECUTE_METHOD.invoke(GET_SCHEDULER_METHOD.invoke(humanEntity), plugin, task, null, 2L);
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
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) {
            if (viewer instanceof Player player) {
                player.updateInventory();
            }
        }
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
    @SuppressWarnings("unchecked")
    protected BaseGui<G, S> updateTitle(@NotNull final Component title) {
        updating = true;

        final List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());

        inventory = new InventoryShadow<Inventory>(inventoryProvider.create((G)this)); // safe because of CRTP

        for (final HumanEntity humanEntity : viewers) {
            open(humanEntity);
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
    protected BaseGui<G, S> disableItemPlace() {
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
    protected BaseGui<G, S> disableItemTake() {
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
    protected BaseGui<G, S> disableItemSwap() {
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
    protected BaseGui<G, S> disableItemDrop() {
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
    protected BaseGui<G, S> disableOtherActions() {
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
    protected BaseGui<G, S> disableAllInteractions() {
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
    protected BaseGui<G, S> enableItemPlace() {
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
    protected BaseGui<G, S> enableItemTake() {
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
    protected BaseGui<G, S> enableItemSwap() {
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
    protected BaseGui<G, S> enableItemDrop() {
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
    protected BaseGui<G, S> enableOtherActions() {
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
    protected BaseGui<G, S> enableAllInteractions() {
        interactionModifiers.clear();
        return this;
    }

    protected boolean isAllInteractionsDisabled() {
        return interactionModifiers.size() == InteractionModifier.VALUES.size();
    }

    /**
     * Check if item placement is allowed inside this GUI.
     *
     * @return True if item placement is allowed for this GUI.
     * @author SecretX.
     * @since 3.0.0.
     */
    protected boolean canPlaceItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
    }

    /**
     * Check if item retrieval is allowed inside this GUI.
     *
     * @return True if item retrieval is allowed inside this GUI.
     * @author SecretX.
     * @since 3.0.0.
     */
    protected boolean canTakeItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
    }

    /**
     * Check if item swap is allowed inside this GUI.
     *
     * @return True if item swap is allowed for this GUI.
     * @author SecretX.
     * @since 3.0.0.
     */
    protected boolean canSwapItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
    }

    /**
     * Check if item drop is allowed inside this GUI
     *
     * @return True if item drop is allowed for this GUI
     * @since 3.0.3
     */
    protected boolean canDropItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
    }

    /**
     * Check if any other actions are allowed in this GUI
     *
     * @return True if other actions are allowed
     * @since 3.0.4
     */
    protected boolean canDoOtherActions() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
    }

    /**
     * Gets an view {@link Map} of all the GUI items.
     * <p>
     * Note: ItemStack that are not {@link GuiItem} will be ignored, and not 
     * returned. The key is the slot index and the value is the {@link GuiItem}
     * in that slot.
     *
     * @return The {@link Map} with all the {@link #guiItems}.
     */
    @NotNull
    protected Map<Integer, GuiItem<G>> getGuiItems() {
        Map<Integer, GuiItem<G>> guiItems = new LinkedHashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            GuiItem<G> guiItem = this.getGuiItem(i);
            if (guiItem != null) {
                guiItems.put(i, guiItem);
            }
        }
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
        return inventory; // for the moment return the shadow inventory, if needed to get the original inventory, use getOriginalInventory()
    }

    /**
     * Sets the new inventory of the GUI.
     *
     * @param inventory The new inventory.
     */
    protected void setInventory(@NotNull final InventoryShadow<Inventory> inventory) {
        Preconditions.checkArgument(inventory != null, "Inventory cannot be null.");

        this.inventory = inventory;
    }

    /**
     * Gets the amount of {@link #rows}.
     *
     * @return The {@link #rows} of the GUI.
     */
    protected int getRows() {
        return rows;
    }

    /**
     * Gets the {@link GuiType} in use.
     *
     * @return The {@link GuiType}.
     */
    @NotNull
    protected GuiType guiType() {
        return guiType;
    }

    /**
     * Gets the default click resolver.
     */
    @Nullable
    protected GuiAction<InventoryClickEvent, G> getDefaultClickAction() {
        return defaultClickAction;
    }

    /**
     * Sets the {@link GuiAction} of a default click on any item.
     * See {@link InventoryClickEvent}.
     *
     * @param defaultClickAction {@link GuiAction} to resolve when any item is clicked.
     */
    protected void setDefaultClickAction(@Nullable final GuiAction<@NotNull InventoryClickEvent, G> defaultClickAction) {
        this.defaultClickAction = defaultClickAction;
    }

    /**
     * Gets the default top click resolver.
     */
    @Nullable
    protected GuiAction<InventoryClickEvent, G> getDefaultTopClickAction() {
        return defaultTopClickAction;
    }

    /**
     * Sets the {@link GuiAction} of a default click on any item on the top part of the GUI.
     * Top inventory being for example chests etc, instead of the {@link Player} inventory.
     * See {@link InventoryClickEvent}.
     *
     * @param defaultTopClickAction {@link GuiAction} to resolve when clicking on the top inventory.
     */
    protected void setDefaultTopClickAction(@Nullable final GuiAction<InventoryClickEvent, G> defaultTopClickAction) {
        this.defaultTopClickAction = defaultTopClickAction;
    }

    /**
     * Gets the player inventory action.
     */
    @Nullable
    protected GuiAction<InventoryClickEvent, G> getPlayerInventoryAction() {
        return playerInventoryAction;
    }

    protected void setPlayerInventoryAction(@Nullable final GuiAction<InventoryClickEvent, G> playerInventoryAction) {
        this.playerInventoryAction = playerInventoryAction;
    }

    /**
     * Gets the default drag resolver.
     */
    @Nullable
    protected GuiAction<InventoryDragEvent, G> getDragAction() {
        return dragAction;
    }

    /**
     * Sets the {@link GuiAction} of a default drag action.
     * See {@link InventoryDragEvent}.
     *
     * @param dragAction {@link GuiAction} to resolve.
     */
    protected void setDragAction(@Nullable final GuiAction<InventoryDragEvent, G> dragAction) {
        this.dragAction = dragAction;
    }

    /**
     * Gets the close gui resolver.
     */
    @Nullable
    protected GuiAction<InventoryCloseEvent, G> getCloseGuiAction() {
        return closeGuiAction;
    }

    /**
     * Sets the {@link GuiAction} to run once the inventory is closed.
     * See {@link InventoryCloseEvent}.
     *
     * @param closeGuiAction {@link GuiAction} to resolve when the inventory is closed.
     */
    protected void setCloseGuiAction(@Nullable final GuiAction<InventoryCloseEvent, G> closeGuiAction) {
        this.closeGuiAction = closeGuiAction;
    }

    /**
     * Gets the open gui resolver.
     */
    @Nullable
    protected GuiAction<InventoryOpenEvent, G> getOpenGuiAction() {
        return openGuiAction;
    }

    /**
     * Sets the {@link GuiAction} to run when the GUI opens.
     * See {@link InventoryOpenEvent}.
     *
     * @param openGuiAction {@link GuiAction} to resolve when opening the inventory.
     */
    protected void setOpenGuiAction(@Nullable final GuiAction<InventoryOpenEvent, G> openGuiAction) {
        this.openGuiAction = openGuiAction;
    }

    /**
     * Gets the resolver for the outside click.
     */
    @Nullable
    protected GuiAction<InventoryClickEvent, G> getOutsideClickAction() {
        return outsideClickAction;
    }

    /**
     * Sets the {@link GuiAction} to run when clicking on the outside of the inventory.
     * See {@link InventoryClickEvent}.
     *
     * @param outsideClickAction {@link GuiAction} to resolve when clicking outside of the inventory.
     */
    protected void setOutsideClickAction(@Nullable final GuiAction<InventoryClickEvent, G> outsideClickAction) {
        this.outsideClickAction = outsideClickAction;
    }

    /**
     * Gets the action for the specified slot. If there is no action defined at 
     * this slot or if no slot action has been defined, it will return null.
     *
     * @param slot The slot clicked.
     * @return The action for the specified slot or null if no action is defined.
     * @throws IllegalArgumentException If the slot index is not valid.
     */
    @Nullable
    protected GuiAction<InventoryClickEvent, G> getSlotAction(final int slot) {
        if (dummyActionSlot == null) return null; // if no slot action has been defined
        dummyActionSlot.setIndex(slot);
        return slotActions.get(dummyActionSlot);
    }

    /**
     * Gets the action for the specified slot.
     *
     * @param slot The slot clicked.
     */
    @Nullable
    protected GuiAction<InventoryClickEvent, G> getSlotAction(final S slot) {
        return slotActions.get(slot);
    }

    protected boolean isCloseActionEnabled() {
        return runCloseAction;
    }

    protected boolean isOpenActionEnabled() {
        return runOpenAction;
    }
}
