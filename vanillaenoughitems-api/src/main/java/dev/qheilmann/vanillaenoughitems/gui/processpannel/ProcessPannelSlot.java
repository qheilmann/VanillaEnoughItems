package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiSharedButton;

/**
 * Represents a slot position within a ProcessPanel's rendering area.
 * Uses relative coordinates (0-based) that are converted to absolute inventory slots.
 * The panel area spans columns 1-7 and rows 1-5 in the RecipeGui (9x6 inventory).
 */
@NullMarked
public class ProcessPannelSlot implements Comparable<ProcessPannelSlot> {

    /** Default slot for the "Next Recipe" button */
    public static final ProcessPannelSlot DEFAULT_NEXT_RECIPE_SLOT = new ProcessPannelSlot(3, 0);
    /** Default slot for the "Previous Recipe" button */
    public static final ProcessPannelSlot DEFAULT_PREVIOUS_RECIPE_SLOT = new ProcessPannelSlot(1, 0);
    /** Default slot for the "History Forward" button */
    public static final ProcessPannelSlot DEFAULT_HISTORY_FORWARD_SLOT = new ProcessPannelSlot(3, 4);
    /** Default slot for the "History Backward" button */
    public static final ProcessPannelSlot DEFAULT_HISTORY_BACKWARD_SLOT = new ProcessPannelSlot(1, 4);
    /** Default slot for the "Quick Craft" button */
    public static final ProcessPannelSlot DEFAULT_QUICK_CRAFT_SLOT = new ProcessPannelSlot(5, 3);

    private static final int PANEL_COLUMN = 7;
    private static final int PANEL_ROW = 5;
    private static final int PANEL_COLUMNS_OFFSET = 1; // Panel starts at column 1 in the inventory
    private static final int PANEL_ROWS_OFFSET = 1;    // Panel starts at row 1 in the inventory

    /** Number of columns in a 9x6 inventory */
    private static final int INV_COLUMNS = 9;
    
    private final int column;
    private final int row;

    /**
     * Get a comparator that orders ProcessPannelSlots by row, then by column.
     * @return the comparator
     */
    public static Comparator<ProcessPannelSlot> comparator() {
        return Comparator.comparingInt(ProcessPannelSlot::row)
            .thenComparingInt(ProcessPannelSlot::column);
    }

    /**
     * Get the default mapping of shared buttons to their panel slots.
     * As used in most ProcessPanels, (e.g., Crafting, Smelting, etc).
     * @return the default shared button map
     */
    public static Map<RecipeGuiSharedButton, ProcessPannelSlot> defaultSharedButtonMap() {
        @SuppressWarnings("null")
        Map<RecipeGuiSharedButton, ProcessPannelSlot> shared = new EnumMap<>(RecipeGuiSharedButton.class);
        shared.put(RecipeGuiSharedButton.NEXT_RECIPE,      DEFAULT_NEXT_RECIPE_SLOT);
        shared.put(RecipeGuiSharedButton.PREVIOUS_RECIPE,  DEFAULT_PREVIOUS_RECIPE_SLOT);
        shared.put(RecipeGuiSharedButton.HISTORY_FORWARD,  DEFAULT_HISTORY_FORWARD_SLOT);
        shared.put(RecipeGuiSharedButton.HISTORY_BACKWARD, DEFAULT_HISTORY_BACKWARD_SLOT);
        shared.put(RecipeGuiSharedButton.QUICK_CRAFT,      DEFAULT_QUICK_CRAFT_SLOT);
        return Map.copyOf(shared);
    }

    /**
     * Get all slot indices within the panel area as absolute inventory slots.
     * 
     * @return set of all absolute slot indices in the panel area
     */
    public static SequencedSet<Integer> all() {
        int invMinColumn = PANEL_COLUMNS_OFFSET;
        int invMaxColumn = PANEL_COLUMNS_OFFSET + PANEL_COLUMN - 1;
        int invMinRow = PANEL_ROWS_OFFSET;
        int invMaxRow = PANEL_ROWS_OFFSET + PANEL_ROW - 1;

        LinkedHashSet<Integer> slots = new LinkedHashSet<>();
        for (int row = invMinRow; row <= invMaxRow; row++) {
            for (int col = invMinColumn; col <= invMaxColumn; col++) {
                slots.add(row * INV_COLUMNS + col);
            }
        }
        return slots;
    }

    /**
     * Create a ProcessPannelSlot at the specified position
     * @param column the column within the panel ({@link #PANEL_MIN_COLUMN}-{@link #PANEL_COLUMN})
     * @param row the row within the panel ({@link #PANEL_MIN_ROW}-{@link #PANEL_ROW})
     * @throws IndexOutOfBoundsException if column or row is out of bounds
     */
    public ProcessPannelSlot(int column, int row) {
        if (column < 0 || column >= PANEL_COLUMN) {
            throw new IndexOutOfBoundsException(
                "Column " + column + " is out of bounds. Must be between " + 
                0 + " and " + (PANEL_COLUMN-1)
            );
        }
        if (row < 0 || row >= PANEL_ROW) {
            throw new IndexOutOfBoundsException(
                "Row " + row + " is out of bounds. Must be between " + 
                0 + " and " + (PANEL_ROW-1)
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
        return (row + 1) * INV_COLUMNS + (column + 1);
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

    @Override
    public int compareTo(@SuppressWarnings("null") ProcessPannelSlot o) {
        return comparator().compare(this, o);
    }
}
