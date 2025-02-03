package me.qheilmann.vei.Core.GUI;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.Slot.Collection.SlotSet;
import me.qheilmann.vei.Core.Slot.Implementation.ChestSlot;
import net.kyori.adventure.text.Component;

public class Gui extends BaseGui<Gui, ChestSlot> { // TODO this Gui is a chestGui for now (one ctor use number of row)
    public GuiItem<Gui> lapizGui = new GuiItem<>(Material.LAPIS_BLOCK);

    /**
     * Main constructor for the GUI
     *
     * @param rows                 The amount of rows the GUI should have
     * @param title                The GUI's title
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     */
    public Gui(final int rows, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }

    /**
     * Alternative constructor that takes both a {@link GuiType} and a set of {@link InteractionModifier}
     *
     * @param guiType              The {@link GuiType} to be used
     * @param title                The GUI's title
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     */
    public Gui(@NotNull final GuiType guiType, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(guiType, title, interactionModifiers);
    }

    @Override
    public void setItem(final ChestSlot slot, @Nullable final GuiItem<Gui> guiItem) {
        super.setItem(slot, guiItem);
    }

    @Override
    public void setItem(@NotNull final SlotSet<ChestSlot> slots, @NotNull final GuiItem<Gui> guiItem) {
        super.setItem(slots, guiItem);
    }

    @Override
    public final @NotNull HashMap<Integer, @NotNull ItemStack> addItem(@NotNull final List<GuiItem<Gui>> items) {
        return super.addItem(items);
    }

    @Override
    public final @NotNull HashMap<Integer, @NotNull ItemStack> removeItem(@NotNull final List<GuiItem<Gui>> items) {
        return super.removeItem(items);
    }
}
