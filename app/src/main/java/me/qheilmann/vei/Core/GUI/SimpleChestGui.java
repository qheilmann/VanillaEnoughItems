package me.qheilmann.vei.Core.GUI;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.Core.Slot.Implementation.ChestSlot;
import net.kyori.adventure.text.Component;

/**
 * A simple chest GUI who can be modified externally
 * <p>
 * Note: This GUI type must be omitted if you want to have complex logic in your GUI
 *
 * @see {@link BaseGui}
 */
public final class SimpleChestGui extends BaseGui<SimpleChestGui, ChestSlot> {

    /**
     * Create a chest GUI who can be modified externally. The GUI will have all 
     * 6 rows and have all interaction disabled
     * <p>
     * Note: This GUI type must be omitted if you want to save state and have 
     * a more complex GUI
     *
     * @see {@link BaseGui}
     * @param title The GUI's title
     */
    public SimpleChestGui(final @NotNull Component title) {
        this(title, 6, InteractionModifier.VALUES);
    }

    /**
     * Create a chest GUI who can be modified externally. The GUI will have all
     * interaction disabled
     * <p>
     * Note: This GUI type must be omitted if you want to save state and have 
     * a more complex GUI
     *
     * @see {@link BaseGui}
     * @param title                The GUI's title
     * @param rows                 The amount of rows the GUI should have
     */
    public SimpleChestGui(final @NotNull Component title, final int rows) {
        this(title, rows, InteractionModifier.VALUES);
    }

    /**
     * Create a chest GUI who can be modified externally
     * <p>
     * Note: This GUI type must be omitted if you want to save state and have 
     * a more complex GUI
     *
     * @param rows                 The amount of rows the GUI should have
     * @param title                The GUI's title
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     */
    public SimpleChestGui(final @NotNull Component title, final int rows, final @NotNull Set<InteractionModifier> interactionModifiers) {
        super((owner) -> BaseGui.plugin.getServer().createInventory(owner, rows*9,  title), interactionModifiers);
    }

    @Override
    public void setItem(final ChestSlot slot, @Nullable final GuiItem<SimpleChestGui> guiItem) {
        super.setItem(slot, guiItem);
    }

    @Override
    public void setItem(@NotNull final SlotSequence<ChestSlot> slots, @Nullable final GuiItem<SimpleChestGui> guiItem) {
        super.setItem(slots, guiItem);
    }

    @Override
    public final @NotNull HashMap<Integer, @NotNull ItemStack> addItem(@NotNull final List<GuiItem<SimpleChestGui>> items) {
        return super.addItem(items);
    }

    @Override
    public final @NotNull HashMap<Integer, @NotNull ItemStack> removeItem(@NotNull final List<GuiItem<SimpleChestGui>> items) {
        return super.removeItem(items);
    }
}
