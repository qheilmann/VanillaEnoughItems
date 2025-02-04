package me.qheilmann.vei.Core.Slot;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

/**
 * Represents a single slot in an inventory
 */
public abstract class Slot<T extends Slot<T>> implements ISlotSupplier<T> {
    protected int index;

    /**
     * Constructs a new Slot with the specified index
     */
    public Slot(int index) {
        this.index = index;
    }

    /**
     * Copy constructor
     */
    public Slot(@NotNull Slot<T> slot) {
        Preconditions.checkNotNull(slot, "slot cannot be null");
        this.index = slot.getIndex();
    }

    /**
     * Returns an exact deep copy of the slot
     * 
     * Note: This method must be overridden in each subclass to return the correct
     * deep copy in the correct type
     */
    @NotNull
    public abstract Slot<T> clone();

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

    /**
     * Clone the slot, cast to the type of the original slot
     * 
     * @param slotTypeReference The slot to clone
     * @return The cloned slot
     * @throws ClassCastException if the cloned slot cannot be cast to the type
     * of the original slot, in case the original slot type does not correctly
     * implement the clone method
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Slot<T>> T cloneSlot(@NotNull T slotTypeReference) {
        Preconditions.checkNotNull(slotTypeReference, "slotTypeReference cannot be null");
        try {
            return (T) slotTypeReference.clone();
        } catch (ClassCastException e) {
            throw new ClassCastException(
                String.format(
                    "Cannot cast the cloned slot to the type %s. %nEnsure the clone method of the slotTypeReference type (%s) is correctly implemented.",
                    slotTypeReference.getClass().getName(),
                    slotTypeReference.getClass().getName()
                )
            );
        }
    }
}
