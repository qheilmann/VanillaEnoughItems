package me.qheilmann.vei.Core.Slot.Collection;

import java.util.Collection;
import java.util.LinkedHashSet;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Slot.Slot;
import me.qheilmann.vei.Core.Utils.NotNullSequenceSet;

/**
 * A specialized set implementation for managing Slot objects.
 * This class ensures no duplicate elements, as defined by the
 * {@link Slot#equals(Object)} method, and guarantees that no null elements
 * are added. Unlike {@link SlotSet}, this class maintains the order of
 * elements as they were added.
 *
 * @param <T> the type of Slot elements maintained by this set
 * @see SlotSet
 */
public class SlotSequence<T extends Slot<T>> extends NotNullSequenceSet<T> {

    /**
     * Constructs a new, empty SlotSequence. The backing {@code LinkedHashSet}
     * instance has default initial capacity (16) and load factor (0.75).
     */
    public SlotSequence() {
        super(new LinkedHashSet<T>());
    }

    /**
     * Constructs a new SlotSequence containing the elements in the specified
     * collection. The {@code LinkedHashSet} is created with default load factor
     * (0.75) and an initial capacity sufficient to contain the elements in
     * the specified collection.
     *
     * @param slots the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null or 
     * contains null elements
     */
    public SlotSequence(@NotNull Collection<? extends T> slots) {
        super(new LinkedHashSet<T>(), slots);
    }
}
