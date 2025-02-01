package me.qheilmann.vei.Core.Slot;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import com.google.common.base.Preconditions;

/**
 * Represents a single slot in a 9x6 grid (max chest size)
 */
public abstract class Slot<T extends Slot<T>> implements ISlotFactory<T> {
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
        this.coord = new Vector2i();
        setX(x);
        setY(y);
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
     * Set the column of the slot, starting from 0
     * 
     * @param x The x coordinate of the slot
     */
    public void setX(int x) {
        Preconditions.checkArgument(x >= 0, "x must be greater than or equal to 0, current value: %d", x);
        coord.x = x;
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
     * Set the row of the slot, starting from 0
     * 
     * @param y The y coordinate of the slot
     */
    public void setY(int y) {
        Preconditions.checkArgument(y >= 0, "y must be greater than or equal to 0, current value: %d", y);
        coord.y = y;
    }

    /**
     * Get the index of the slot after flattening the 2D grid into a 1D array,
     * starting from 0. Used for slots in a chest inventory.
     * 
     * @return The index of the slot
     */
    public abstract int getIndex();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Slot)) return false;
        Slot<?> slot = (Slot<?>) obj;
        return getIndex() == slot.getIndex();
    }

    @Override
    public int hashCode() {
        return getIndex();
    }

    @NotNull
    private static Vector2i validateCoord(@NotNull Vector2i coord) {
        Preconditions.checkNotNull(coord, "coord cannot be null");
        return coord;
    }
}
