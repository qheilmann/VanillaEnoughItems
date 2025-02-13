package me.qheilmann.vei.Core.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

/**
 * A specialized SequenceSet implementation for managing objects.
 * This class ensures no duplicate elements and guarantees that no null elements
 * are added. The elements are stored in a sequence.
 * <p>
 * Unlike {@link NotNullSet}, this class maintains the order of elements as 
 * they were added.
 *
 * @param <E> the type of elements maintained by this set
 */
public class NotNullSequenceSet<E> extends NotNullSet<E> implements SequencedSet<E> {

    private final @NotNull SequencedSet<E> wrappedSequencedSet;

    /**
     * Constructs a new SequencedSet who can't store no null elements.
     * Use the provided set implementation to store the elements in a sequence.
     * <p>
     * If the provided set contains elements, they are removed.
     * 
     * @param setImplementation the set implementation to use (remove any
     * existing elements)
     */
    public NotNullSequenceSet(@NotNull SequencedSet<E> setImplementation) {
        this(setImplementation, Collections.emptyList());
    }

    /**
     * Constructs a new SequencedSet containing the elements in the specified
     * collection. The provided set implementation is used to store the
     * elements in a sequence.
     * <p>
     * If the provided set contains elements, they are removed.
     * <p>
     * Note: if you want to convert your current set instance to a NotNullSet,
     * and remove any null elements, place the same instance as the first and
     * second parameter.
     *
     * @param setImplementation the set implementation to use (remove any 
     * existing elements)
     * @param collection the collection whose elements are to be placed into
     * this set
     * @throws NullPointerException if the specified collection is null
     */
    public NotNullSequenceSet(@NotNull SequencedSet<E> setImplementation, @NotNull Collection<? extends E> collection) {
        super(setImplementation, collection);
        wrappedSequencedSet = setImplementation;
    }
    
    /**
     * Returns a reverse-ordered copy of this collection. The encounter order of 
     * elements in the returned copy is the inverse of the encounter order of 
     * elements in this collection.
     */
    @Override
    public NotNullSequenceSet<E> reversed() {
        return new NotNullSequenceSet<>(new LinkedHashSet<>(), wrappedSequencedSet.reversed());
    }

    @Override
    public void addFirst(@NotNull E element) {
        Preconditions.checkNotNull(element, "Element cannot be null");
        wrappedSequencedSet.addFirst(element);
    }

    @Override
    public void addLast(@NotNull E element) {
        Preconditions.checkNotNull(element, "Element cannot be null");
        wrappedSequencedSet.addLast(element);
    }

    @Override
    @NotNull
    public E removeFirst() {
        return wrappedSequencedSet.removeFirst();
    }

    @Override
    @NotNull
    public E removeLast() {
        return wrappedSequencedSet.removeLast();
    }
}
