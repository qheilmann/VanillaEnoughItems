package me.qheilmann.vei.Core.Slot;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A sequence of slots.
 * <p>
 * Note: this is for the moment a dummy class exactly like ArrayList<T>
 * @param <T> the type of slots in this sequence
 */
public class SlotSequence extends ArrayList<Slot> {
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
    public SlotSequence(Collection<? extends Slot> slots) {
        super(slots);
    }
}
