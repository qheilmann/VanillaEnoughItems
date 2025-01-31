package me.qheilmann.vei.Core.Slot;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import com.google.common.base.Preconditions;

/**
 * Represents a single slot in a 9x6 grid (max chest size)
 */
public class Slot {
    public static final int ROW_COUNT = 6;
    public static final int COLUMN_COUNT = 9;

    public static final Slot TOP_LEFT = new Slot(0, 0);
    public static final Slot TOP_RIGHT = new Slot(COLUMN_COUNT-1, 0);
    public static final Slot BOTTOM_LEFT = new Slot(0, ROW_COUNT-1);
    public static final Slot BOTTOM_RIGHT = new Slot(COLUMN_COUNT-1, ROW_COUNT-1);
    
    // All of this are mutable, so they can't be used
    // public static final SlotRange ALL = new SlotRange(TOP_LEFT, BOTTOM_RIGHT);
    
    // public static final SlotRange TOP_ROW = new SlotRange(TOP_LEFT, TOP_RIGHT);
    // public static final SlotRange SECOND_ROW = new SlotRange(new Slot(0, 1), new Slot(COLUMN_COUNT-1, 1));
    // public static final SlotRange THIRD_ROW = new SlotRange(new Slot(0, 2), new Slot(COLUMN_COUNT-1, 2));
    // public static final SlotRange FOURTH_ROW = new SlotRange(new Slot(0, 3), new Slot(COLUMN_COUNT-1, 3));
    // public static final SlotRange FIFTH_ROW = new SlotRange(new Slot(0, 4), new Slot(COLUMN_COUNT-1, 4));
    // public static final SlotRange BOTTOM_ROW = new SlotRange(BOTTOM_LEFT, BOTTOM_RIGHT);
    
    
    // public static final SlotRange LEFT_COLUMN = new SlotRange(TOP_LEFT, BOTTOM_LEFT);
    // public static final SlotRange SECOND_COLUMN = new SlotRange(new Slot(1, 0), new Slot(1, ROW_COUNT-1));
    // public static final SlotRange THIRD_COLUMN = new SlotRange(new Slot(2, 0), new Slot(2, ROW_COUNT-1));
    // public static final SlotRange FOURTH_COLUMN = new SlotRange(new Slot(3, 0), new Slot(3, ROW_COUNT-1));
    // public static final SlotRange FIFTH_COLUMN = new SlotRange(new Slot(4, 0), new Slot(4, ROW_COUNT-1));
    // public static final SlotRange SIXTH_COLUMN = new SlotRange(new Slot(5, 0), new Slot(5, ROW_COUNT-1));
    // public static final SlotRange SEVENTH_COLUMN = new SlotRange(new Slot(6, 0), new Slot(6, ROW_COUNT-1));
    // public static final SlotRange EIGHTH_COLUMN = new SlotRange(new Slot(7, 0), new Slot(7, ROW_COUNT-1));
    // public static final SlotRange RIGHT_COLUMN = new SlotRange(TOP_RIGHT, BOTTOM_RIGHT);

    private final Vector2i coord;

    /**
     * Defines a slot in a 9x6 grid (max chest size)
     * 
     * @param coord The coordinates of the slot
     */
    public Slot(@NotNull Vector2i coord) {
        this(validateCoord(coord).x, coord.y);
    }

    /**
     * Defines a slot in a 9x6 grid (max chest size)
     * 
     * @param x The x coordinate of the slot
     * @param y The y coordinate of the slot
     */
    public Slot(int x, int y) {
        Preconditions.checkArgument(x >= 0 && x < COLUMN_COUNT, "x must be between %d and %d, current value: %d", 0, COLUMN_COUNT, x);
        Preconditions.checkArgument(y >= 0 && y < ROW_COUNT, "y must be between %d and %d, current value: %d", 0, ROW_COUNT, y);

        this.coord = new Vector2i(x, y);
    }

    /**
     * Get the coordinates of the slot
     * 
     * @return A Vector2i representing the coordinates of the slot
     */
    @NotNull
    public Vector2i getCoord() {
        return coord;
    }

    /**
     * Get the column of the slot, starting from 0
     * 
     * @return The x coordinate of the slot
     */
    public int getX() {
        return coord.x;
    }

    /**
     * Get the row of the slot, starting from 0
     * 
     * @return The y coordinate of the slot
     */
    public int getY() {
        return coord.y;
    }

    /**
     * Get the index of the slot after flattening the 2D grid into a 1D array,
     * starting from 0. Used for slots in a chest inventory.
     * 
     * @return The index of the slot
     */
    public int getIndex() {
        return coord.y * COLUMN_COUNT + coord.x;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Slot slot = (Slot) obj;
        return getX() == slot.getX() && getY() == slot.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord.x, coord.y);
    }

    @NotNull
    private static Vector2i validateCoord(@NotNull Vector2i coord) {
        Preconditions.checkNotNull(coord, "coord cannot be null");
        return coord;
    }
}
