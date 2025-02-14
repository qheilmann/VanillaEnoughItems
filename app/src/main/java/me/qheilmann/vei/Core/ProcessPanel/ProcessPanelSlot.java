package me.qheilmann.vei.Core.ProcessPanel;

import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Slot.GridSlot;
import me.qheilmann.vei.Core.Slot.Implementation.MaxChestSlot;

/**
 * Represent a slot in a recipe panel.
 * <p>
 * The recipe panel slot is a grid slot with a fixed size of 7x5.
 */
public class ProcessPanelSlot extends GridSlot {
    public static final int ROW_COUNT = 5;
    public static final int COLUMN_COUNT = 7;

    public ProcessPanelSlot(int x, int y) {
        super(x, y, COLUMN_COUNT, ROW_COUNT);
    }

    public ProcessPanelSlot(int index) {
        super(index, COLUMN_COUNT, ROW_COUNT);
    }

    public ProcessPanelSlot(ProcessPanelSlot slot) {
        super(slot);
    }

    @Override
    @NotNull
    public ProcessPanelSlot clone() {
        return new ProcessPanelSlot(this);
    }

    /**
     * Convert this recipe panel slot to a max chest slot.
     *
     * @return a max chest slot instance
     */
    public MaxChestSlot asMaxChestSlot() {
        return new MaxChestSlot(this.getX()+1, this.getY()+1);
    }
}