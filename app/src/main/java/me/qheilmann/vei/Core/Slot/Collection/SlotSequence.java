package me.qheilmann.vei.Core.Slot.Collection;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.SequencedSet;

import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Slot.Slot;
import me.qheilmann.vei.Core.Utils.NotNullSequenceSet;

/**
 * Constructs a new SlotSequence for managing Slot objects.
 * <p>
 * This class ensures no duplicate elements, as defined by the
 * {@link Slot#equals(Object)} method, and guarantees that no null elements
 * are added.
 * <p>
 * The order of elements in a SlotSequence is the order in which they were
 * added, and can be iterated in that order.
 *
 * @param <T> the type of Slot elements maintained by this set
 */
public class SlotSequence<T extends Slot> extends NotNullSequenceSet<T> {

    /**
     * Constructs a new, empty SlotSequence. The backing {@code LinkedHashSet}
     * instance has default initial capacity (16) and load factor (0.75).
     */
    public SlotSequence() {
        super(new LinkedHashSet<T>());
    }

    /**
     * Constructs a new SlotSequence containing the elements in the specified
     * collection.
     * <p>
     * This class ensures no duppliacte elements, as defined by the
     * {@link Slot#equals(Object)} method, and guarantees that no null elements
     * are added.
     * <p>
     * The order of elements in a SlotSequence is the order in which they were
     * added, and can be iterated in that order.
     *
     * @param slots the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null or 
     * contains null elements
     */
    public SlotSequence(@NotNull Collection<? extends T> slots) {
        super(new LinkedHashSet<T>(), slots);
    }

    /**
     * Constructs a new SlotSequence containing the elements in the specified
     * collection.
     * <p>
     * This class ensures no duppliacte elements, as defined by the
     * {@link Slot#equals(Object)} method, and guarantees that no null elements
     * are added.
     * <p>
     * The order of elements in a SlotSequence is defined by the order or the
     * set implementation. (e.g. {@link LinkedHashSet}, insertion order or
     * {@link java.util.TreeSet}, natural order)
     * 
     * @param setImplementation
     * @param collection
     */
    protected SlotSequence(@NotNull SequencedSet<T> setImplementation, @NotNull Collection<? extends T> collection) {
        super(setImplementation, collection);
    }
}
