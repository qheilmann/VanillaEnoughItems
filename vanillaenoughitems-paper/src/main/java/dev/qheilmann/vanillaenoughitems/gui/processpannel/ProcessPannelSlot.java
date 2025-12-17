package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.utils.fastinv.Slots;

/**
 * Represents a slot position within a ProcessPanel's rendering area.
 * Uses relative coordinates (0-based) that are converted to absolute inventory slots.
 * The panel area spans columns 1-7 and rows 1-5 in the RecipeGui (9x6 inventory).
 */
@NullMarked
public class ProcessPannelSlot {
    private static final int PANEL_MIN_COLUMN = 0;
    private static final int PANEL_MAX_COLUMN = 6;
    private static final int PANEL_MIN_ROW = 0;
    private static final int PANEL_MAX_ROW = 4;
    
    private final int column;
    private final int row;

    /**
     * Create a ProcessPannelSlot at the specified position
     * @param column the column within the panel ({@link #PANEL_MIN_COLUMN}-{@link #PANEL_MAX_COLUMN})
     * @param row the row within the panel ({@link #PANEL_MIN_ROW}-{@link #PANEL_MAX_ROW})
     * @throws IndexOutOfBoundsException if column or row is out of bounds
     */
    public ProcessPannelSlot(int column, int row) {
        if (column < PANEL_MIN_COLUMN || column > PANEL_MAX_COLUMN) {
            throw new IndexOutOfBoundsException(
                "Column " + column + " is out of bounds. Must be between " + 
                PANEL_MIN_COLUMN + " and " + PANEL_MAX_COLUMN
            );
        }
        if (row < PANEL_MIN_ROW || row > PANEL_MAX_ROW) {
            throw new IndexOutOfBoundsException(
                "Row " + row + " is out of bounds. Must be between " + 
                PANEL_MIN_ROW + " and " + PANEL_MAX_ROW
            );
        }
        this.column = column;
        this.row = row;
    }

    /**
     * Convert this panel-relative position to an absolute inventory slot index.
     * Panel area starts at column 1, row 1 in the 9x6 inventory.
     * 
     * @return the absolute slot index
     */
    public int toSlotIndex() {
        // Panel area starts at column 1, row 1 (offset by 1 in both directions)
        return Slots.maxChestSlot(column + 1, row + 1);
    }
    
    /**
     * Get the panel-relative column
     * @return the column
     */
    public int column() {
        return column;
    }
    
    /**
     * Get the panel-relative row
     * @return the row
     */
    public int row() {
        return row;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ProcessPannelSlot other)) return false;
        return column == other.column && row == other.row;
    }

    @Override
    public int hashCode() {
        return 31 * column + row;
    }

    @Override
    public String toString() {
        return "ProcessPannelSlot[column=" + column + ", row=" + row + "]";
    }
}

