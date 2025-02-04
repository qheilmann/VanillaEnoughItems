package me.qheilmann.vei.Core.Slot.Collection;

import java.util.Collection;
import java.util.HashSet;

import me.qheilmann.vei.Core.Slot.Slot;
import me.qheilmann.vei.Core.Utils.NotNullSet;

/**
 * A specialized set implementation for managing Slot objects.
 * This class contains no duplicate elements, as defined by the
 * {@link Slot#equals(Object)} method, and ensures that no null elements
 * are added.
 *
 * Unlike {@link SlotSequence}, this class does not maintain the order of
 * elements as they were added.
 *
 * @param <T> the type of Slot elements maintained by this set
 * @see SlotSequence
 */
public class SlotSet<T extends Slot> extends NotNullSet<T> {

    /**
     * Constructs a new, empty SlotSet; the backing {@code HashSet} instance
     * has default initial capacity (16) and load factor (0.75).
     */
    public SlotSet() {
        super(new HashSet<T>());
    }

    /**
     * Constructs a new SlotSet containing the elements in the specified
     * collection. The {@code HashSet} is created with default load factor
     * (0.75) and an initial capacity sufficient to contain the elements in
     * the specified collection.
     *
     * @param slots the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null or 
     * contains null elements
     */
    public SlotSet(Collection<? extends T> slots) {
        super(new HashSet<T>(), slots);
    }
}
