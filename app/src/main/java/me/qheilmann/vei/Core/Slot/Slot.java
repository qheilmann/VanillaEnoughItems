package me.qheilmann.vei.Core.Slot;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import com.google.common.base.Preconditions;

/**
 * Represents a single slot in a 9x6 grid (max chest size)
 */
public class Slot {
    public static final Slot TOP_LEFT = new Slot(0, 0);
    public static final Slot TOP_RIGHT = new Slot(8, 0);
    public static final Slot BOTTOM_LEFT = new Slot(0, 5);
    public static final Slot BOTTOM_RIGHT = new Slot(8, 5);

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
        Preconditions.checkArgument(x >= 0 && x < 9, "x must be between 0 and 8, current value: %d", x);
        Preconditions.checkArgument(y >= 0 && y < 6, "y must be between 0 and 5, current value: %d", y);

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
        return coord.y * 9 + coord.x;
    }

    @NotNull
    private static Vector2i validateCoord(@NotNull Vector2i coord) {
        Preconditions.checkNotNull(coord, "coord cannot be null");
        return coord;
    }
}
