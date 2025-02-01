package me.qheilmann.vei.Core.Slot.Collection;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nullable;

import org.jspecify.annotations.NullMarked;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.Slot.Slot;

/**
 * A set of slots, there are no duplicates nor order
 * A {@link HashSet} implementation of the {@link Slot} interface.
 */
public class SlotSet<T extends Slot<T>> extends HashSet<T> {
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
    public SlotSet(Collection<? extends T> slots) {
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

    /**
     * Add a slot to the set, if the slot is null it will not be added
     * 
     * @param slot the slot to add
     * @return true if the slot was added, false otherwise
     */
    @Override
    public boolean add(@Nullable T slot) {
        if(slot == null) {
            return false;
        }
        return super.add(slot);
    }
}
