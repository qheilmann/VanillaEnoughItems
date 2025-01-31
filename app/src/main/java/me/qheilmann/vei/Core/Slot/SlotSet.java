package me.qheilmann.vei.Core.Slot;

import java.util.Collection;
import java.util.HashSet;

public class SlotSet extends HashSet<Slot> {
    /**
     * Constructs a new, empty slotSet; the backing {@code HashMap} instance
     * has default initial capacity (16) and load factor (0.75).
     */
    public SlotSet() {
        super();
    }

    /**
     * Constructs a new slotSet containing the elements in the specified
     * collection. The {@code HashMap} is created with default load factor
     * (0.75) and an initial capacity sufficient to contain the elements in
     * the specified collection.
     *
     * @param slots the collection whose elements are to be placed into this
     * set
     * @throws NullPointerException if the specified collection is null
     */
    public SlotSet(Collection<? extends Slot> slots) {
        super(slots);
    }

    /**
     * Constructs a new, empty slotSet; the backing {@code HashMap} instance
     * has the specified initial capacity and the specified load factor.
     *
     * @apiNote
     * To create a {@code SlotSet} with an initial capacity that accommodates
     * an expected number of elements, use {@link #newHashSet(int) newHashSet}.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor the load factor of the hash map
     * @throws IllegalArgumentException if the initial capacity is less
     * than zero, or if the load factor is nonpositive
     */
    public SlotSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new, empty slotSet; the backing {@code HashMap} instance
     * has the specified initial capacity and default load factor (0.75).
     *
     * @apiNote
     * To create a {@code SlotSet} with an initial capacity that accommodates
     * an expected number of elements, use {@link #newHashSet(int) newHashSet}.
     *
     * @param initialCapacity the initial capacity of the hash table
     * @throws IllegalArgumentException if the initial capacity is less
     * than zero
     */
    public SlotSet(int initialCapacity) {
        super(initialCapacity);
    }
}
