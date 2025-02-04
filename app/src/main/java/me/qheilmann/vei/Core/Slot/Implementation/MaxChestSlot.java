package me.qheilmann.vei.Core.Slot.Implementation;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a single slot in an max chest inventory (6 rows)
 */
public class MaxChestSlot extends ChestSlot{

    public static final int ROW_COUNT = 6;

    public MaxChestSlot(int index) {
        super(index, ROW_COUNT);
    }

    public MaxChestSlot(int x, int y) {
        super(x, y, ROW_COUNT);
    }

    public MaxChestSlot(MaxChestSlot slot) {
        super(slot);
    }

    @Override
    @NotNull
    public ChestSlot clone() {
        return new MaxChestSlot(this);
    }
}
