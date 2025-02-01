package me.qheilmann.vei.Core.Slot;

/**
 * Represents a single slot in an inventory
 */
public abstract class Slot<T extends Slot<T>> implements ISlotSupplier<T> {
    protected int index;

    /**
     * Get the index of the slot
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index of the slot
     */
    public void setIndex(int index) {
        this.index = index;
    }

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
}
