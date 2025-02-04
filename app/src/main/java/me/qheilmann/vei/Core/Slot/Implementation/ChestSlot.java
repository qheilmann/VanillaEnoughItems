package me.qheilmann.vei.Core.Slot.Implementation;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.Slot.GridSlot;

/**
 * Represents a slot in a chest inventory with 9 columns and a variable number
 * of rows (max: 6)
 */
public class ChestSlot extends GridSlot {
    public static final int COLUMN_COUNT = 9;
    public static final int MAX_ROW_COUNT = 6;

    /**
     * Constructs a new ChestSlot with the specified index and row count
     */
    public ChestSlot(int index, int rowCount) {
        super(index, COLUMN_COUNT, checkRowCount(rowCount));
    }

    /**
     * Constructs a new ChestSlot with the specified x and y coordinates and row count
     */
    public ChestSlot(int x, int y, int rowCount) {
        super(x, y, COLUMN_COUNT, checkRowCount(rowCount));
    }

    /**
     * Copy constructor
     */
    public ChestSlot(ChestSlot slot) {
        super(slot);
    }

    @Override
    @NotNull
    public ChestSlot clone() {
        return new ChestSlot(this);
    }

    private static int checkRowCount(int rowCount) {
        Preconditions.checkArgument(rowCount > 0 && rowCount <= MAX_ROW_COUNT, "rowCount must be between 1 and %d, current value: %d", MAX_ROW_COUNT, rowCount);
        return rowCount;
    }
}
