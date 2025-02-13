package me.qheilmann.vei.Core.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

/**
 * A specialized set implementation for managing objects.
 * This class ensures no duplicate elements and guarantees that no null elements
 * are added.
 *
 * @param <E> the type of elements maintained by this set
 */
public class NotNullSet<E>  implements Set<E> {
    private final @NotNull Set<E> wrappedSet;

    /**
     * Constructs a new set who can't store no null elements.
     * Use the provided set implementation to store the elements.
     * <p>
     * If the provided set contains elements, they are removed.
     * 
     * @param setImplementation the set implementation to use (remove any
     * existing elements)
     */
    public NotNullSet(@NotNull Set<E> setImplementation) {
        this(setImplementation, Collections.emptyList());
    }

    /**
     * Constructs a new set containing the elements in the specified
     * collection. The provided set implementation is used to store the
     * elements.
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
    public NotNullSet(@NotNull Set<E> setImplementation, @NotNull Collection<? extends E> collection) {
        Preconditions.checkNotNull(setImplementation, "The provided set cannot be null");
        Preconditions.checkNotNull(collection, "The provided collection cannot be null");
        
        this.wrappedSet = setImplementation;
        setImplementation.clear();

        for (E slot : collection) {
            Objects.requireNonNull(slot, "Slot cannot be null");
            wrappedSet.add(slot);
        }
    }

    /**
     * Adds the specified element to this set if it is not already present
     * (optional operation).  More formally, adds the specified element
     * {@code e} to this set if the set contains no element {@code e2}
     * such that
     * {@code Objects.equals(e, e2)}.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.  In combination with the
     * restriction on constructors, this ensures that sets never contain
     * duplicate elements. The added element must not be null.
     *
     * <p>The stipulation above does not imply that sets must accept all
     * elements; sets may refuse to add any particular element, including
     * {@code null}, and throw an exception, as described in the
     * specification for {@link Collection#add Collection.add}.
     * Individual set implementations should clearly document any
     * restrictions on the elements that they may contain.
     *
     * @param e element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     *         element
     * @throws UnsupportedOperationException if the {@code add} operation
     *         is not supported by this set
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this set
     * @throws NullPointerException if the specified element is null and this
     *         set does not permit null elements
     * @throws IllegalArgumentException if some property of the specified element
     *         prevents it from being added to this set
     */
    @Override
    public boolean add(@NotNull E element) {
        Objects.requireNonNull(element, "Slot cannot be null");
        return wrappedSet.add(element);
    }


    /**
     * Adds all of the elements in the specified collection to this set if
     * they're not already present (optional operation).  If the specified
     * collection is also a set, the {@code addAll} operation effectively
     * modifies this set so that its value is the <i>union</i> of the two
     * sets.  The behavior of this operation is undefined if the specified
     * collection is modified while the operation is in progress. Each element
     * must not be null.
     *
     * @param  c collection containing elements to be added to this set
     * @return {@code true} if this set changed as a result of the call
     *
     * @throws UnsupportedOperationException if the {@code addAll} operation
     *         is not supported by this set
     * @throws ClassCastException if the class of an element of the
     *         specified collection prevents it from being added to this set
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this set
     * @see #add(Object)
     */
    @Override
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        Objects.requireNonNull(collection, "Collection cannot be null");

        boolean modified = false;
        for (E element : collection) {
            modified |= add(element);
        }
        return modified;
    }

    @Override
    public boolean contains(@NotNull Object object) {
        return wrappedSet.contains(object);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return wrappedSet.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return wrappedSet.isEmpty();
    }

    @Override
    public int size() {
        return wrappedSet.size();
    }

    @Override
    public boolean remove(@NotNull Object object) {
        return wrappedSet.remove(object);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return wrappedSet.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return wrappedSet.retainAll(collection);
    }

    @Override
    public void clear() {
        wrappedSet.clear();
    }

    @Override
    @NotNull
    public Iterator<E> iterator() {
        return Collections.unmodifiableSet(wrappedSet).iterator();
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return wrappedSet.toArray();
    }

    @Override
    @NotNull
    public <T> T[] toArray(@NotNull T[] a) {
        return wrappedSet.toArray(a);
    }

    @Override
    @NotNull
    public String toString() {
        return wrappedSet.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return wrappedSet.equals(obj);
    }

    @Override
    public int hashCode() {
        return wrappedSet.hashCode();
    }
}
