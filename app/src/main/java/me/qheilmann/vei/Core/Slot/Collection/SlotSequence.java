package me.qheilmann.vei.Core.Slot.Collection;

import java.util.ArrayList;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Slot.Slot;

/**
 * A sequence of slots.
 * <p>
 * Note: this is for the moment a dummy class exactly like ArrayList<T>
 * @param <T> the type of slots in this sequence
 */
public class SlotSequence<T extends Slot<T>> extends ArrayList<Slot<T>> {
    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public SlotSequence(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public SlotSequence() {
        super();
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param slots the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public SlotSequence(@NotNull Collection<? extends Slot<T>> slots) {
        super(slots);
    }
}
