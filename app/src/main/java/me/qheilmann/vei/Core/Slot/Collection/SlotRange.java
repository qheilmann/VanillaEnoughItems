package me.qheilmann.vei.Core.Slot.Collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import me.qheilmann.vei.Core.Slot.GridSlot;
import me.qheilmann.vei.Core.Slot.Slot;

/**
 * Defines a range of slots within a grid, specified by two corner slots.
 * The slots within the range are ordered row by row, starting from the first row in the first column,
 * then the first row in the second column, and so on.
*/
public class SlotRange<T extends GridSlot<T>> extends SlotSequence<T> {

    private T topLeftSlot;
    private T bottomRightSlot;

    /**
     * Defines a range of slots
     * 
     * @param cornerA     The first corner of the range
     * @param cornerB     The second corner of the range, on the opposite side
     * of cornerA
     */
    public SlotRange(@NotNull T cornerA, @NotNull T cornerB) {
        super(getSlotsBetween(cornerA, cornerB));
        this.topLeftSlot = getTopLeftSlot(cornerA, cornerB);
        this.bottomRightSlot = getBottomRightSlot(cornerA, cornerB);
    }

    /**
     * Deep copy constructor, creates a new SlotRange with the same slots as the 
     * given SlotRange
     * 
     * @param slotRange the SlotRange to copy
     */
    public SlotRange(@NotNull SlotRange<T> slotRange) {
        // The to array method is used to create a copy of the slots
        super(List.of(slotRange.toArray()));
        this.topLeftSlot = slotRange.getTopLeftSlot();
        this.bottomRightSlot = slotRange.getBottomRightSlot();
    }

    /**
     * Get the top left slot of the range
     * 
     * @return a new instance of the top left slot
     */
    @NotNull
    public T getTopLeftSlot() {
        return Slot.cloneSlot(topLeftSlot);
    }

    /**
     * Get the bottom right slot of the range
     * 
     * @return a new instance of the bottom right slot
     */
    @NotNull
    public T getBottomRightSlot() {
        return Slot.cloneSlot(bottomRightSlot);
    }

    /**
     * Set the top left slot of the range, can be on the other side 
     * 
     * @throws IllegalArgumentException if topLeftSlot is null
     */
    public void setTopLeftSlot(@NotNull T topLeftSlot) {
        Preconditions.checkArgument(topLeftSlot != null, "topLeftSlot cannot be null");

        super.clear();
        super.addAll(getSlotsBetween(topLeftSlot, this.bottomRightSlot));

        // Update the top left slot, sometimes the top left slot is not the top left 
        // slot anymore so we need to update both corners
        this.bottomRightSlot = getBottomRightSlot(topLeftSlot, bottomRightSlot);
        this.topLeftSlot = getTopLeftSlot(topLeftSlot, bottomRightSlot);
    }

    public void setBottomRightSlot(@NotNull T bottomRightSlot) {
        Preconditions.checkArgument(bottomRightSlot != null, "bottomRightSlot cannot be null");
        
        super.clear();
        super.addAll(getSlotsBetween(this.topLeftSlot, bottomRightSlot));

        // Update the bottom right slot, sometimes the bottom right slot is not the bottom right 
        // slot anymore so we need to update both corners
        this.bottomRightSlot = getBottomRightSlot(topLeftSlot, bottomRightSlot);
        this.topLeftSlot = getTopLeftSlot(topLeftSlot, bottomRightSlot);
    }

    // Make the range immutable

    @Override
    public boolean add(T slot) {
        throw new UnsupportedOperationException("Cannot add slots to a SlotRange");
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        throw new UnsupportedOperationException("Cannot add slots to a SlotRange");
    }

    @Override
    public void addFirst(T element) {
        throw new UnsupportedOperationException("Cannot add slots to a SlotRange");
    }

    @Override
    public void addLast(T element) {
        throw new UnsupportedOperationException("Cannot add slots to a SlotRange");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot clear slots in a SlotRange");
    }

    /**
     * Returns an iterator over a copy of slots described by this SlotRange.
     */
    @Override
    @NotNull
    public Iterator<T> iterator() {
        T[] slotArray = toArray();
        return Arrays.asList(slotArray).iterator();
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
    }

    /**
     * Returns an array containing a copy of each GridSlot in this set.
     * The elements are copied in the same order as the index (row by row).
     *
     * @return an array containing a copy of each GridSlot in this range
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray() {    
        T[] slotsArray = (T[]) java.lang.reflect.Array.newInstance(bottomRightSlot.getClass(), size());
        Iterator<T> iterator = super.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            T slot = iterator.next();
            T clonedSlot = Slot.cloneSlot(slot);
            slotsArray[index++] = clonedSlot;
        }

        return slotsArray;
    }

    /**
     * Returns an array containing a copy of all GridSlot in this range; the 
     * runtime type of the returned array is that of the specified array. If 
     * the set fits in the specified array, it is returned therein. Otherwise, 
     * a new array is allocated with the runtime type of the specified array 
     * and the size of this set.
     * 
     * If this set fits in the specified array with room to spare (i.e., the 
     * array has more elements than this set), the element in the array 
     * immediately following the end of the set is set to null. (This is useful 
     * in determining the length of this set only if the caller knows that this 
     * set does not contain any null elements.)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <U extends GridSlot<T>> U[] toArray(@NotNull U[] array) {
        U[] slotsArray = prepareArray(array, this.size());

        Iterator<T> iterator = super.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            T slot = iterator.next();
            T clonedSlot = Slot.cloneSlot(slot);
            try {
                slotsArray[index++] = (U) clonedSlot;
            } catch (ArrayStoreException | ClassCastException e) {
                throw new ClassCastException("Cannot store the cloned slot of " + clonedSlot.getClass().getName() + " in the array of " + array.getClass().getName());
            }
        }
        return slotsArray;
    }

    /**
     * Returns an array containing a copy of all GridSlot in this range; the 
     * runtime type of the returned array is that of the specified generator. 
     * If the set fits in the array, it is returned therein. Otherwise, 
     * a new array is allocated with the runtime type of the specified array 
     * and the size of this set.
     * 
     * If this set fits in the specified array with room to spare (i.e., the 
     * array has more elements than this set), the element in the array 
     * immediately following the end of the set is set to null. (This is useful 
     * in determining the length of this set only if the caller knows that this 
     * set does not contain any null elements.)
     */
    @NotNull
    @Override
    public <U> U[] toArray(@NotNull IntFunction<U[]> generator) {
        Preconditions.checkNotNull(generator, "generator cannot be null");
        return this.toArray(generator.apply(this.size()));
    }

    @NotNull
    @Override
    public void forEach(@NotNull Consumer<? super T> action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        for (T slot : this) {
            T clonedSlot = Slot.cloneSlot(slot);
            action.accept(clonedSlot);
        }
    }

    /**
     * Get a copy of the first slot in the range
     */
    @NotNull
    @Override
    public T getFirst() {
        T first = super.getFirst();
        return Slot.cloneSlot(first);
    }

    /**
     * Get a copy of the last slot in the range
     */
    @Override
    public T getLast() {
        T last = super.getLast();
        return Slot.cloneSlot(last);
    }

    /**
     * Returns a sequential Stream with a copy of slots described by this SlotRange.
     */
    @Override
    public Stream<T> parallelStream() {
        T[] slotArray = toArray();
        return Arrays.stream(slotArray).parallel();
    }

    @Override
    public T removeFirst() {
        throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
    }

    @Override
    public T removeLast() {
        throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
    }

    /**
     * Creates a Spliterator over a copy of slots described by this SlotRange.
     */
    @Override
    public Spliterator<T> spliterator() {
        T[] slotArray = toArray();
        return Arrays.spliterator(slotArray);
    }

    /**
     * Returns a sequential Stream with a copy of slots described by this SlotRange.
     */
    @Override
    public Stream<T> stream() {
        T[] slotArray = toArray();
        return Arrays.stream(slotArray);
    }

    // Static methods

    /**
     * Get all the slots between the two corners. The slots within the range are
     * ordered row by row, starting from the first row in the first column, then
     * the first row in the second column, and so on.
     * 
     * @param cornerA The first corner of the range.
     * @param cornerB The second corner of the range, on the opposite side of
     * cornerA.
     * @return A list of slots between the corners
     * @throws IllegalArgumentException if either cornerA or cornerB is null.
     */
    @NotNull
    private static <T extends GridSlot<T>> ArrayList<T> getSlotsBetween(@NotNull T cornerA, @NotNull T cornerB) {
        Preconditions.checkArgument(cornerA != null, "cornerA cannot be null");
        Preconditions.checkArgument(cornerB != null, "cornerB cannot be null");

        T topLeftSlot = getTopLeftSlot(cornerA, cornerB);
        T bottomRightSlot = getBottomRightSlot(cornerA, cornerB);

        ArrayList<T> slots = new ArrayList<>();
        int bottomRightX = bottomRightSlot.getX();
        int bottomRightY = bottomRightSlot.getY();
        for (int y = topLeftSlot.getY(); y <= bottomRightY; y++) {
            for (int x = topLeftSlot.getX(); x <= bottomRightX; x++) {
                T clonedSlot = Slot.cloneSlot(cornerA);
                clonedSlot.setX(x);
                clonedSlot.setY(y);
                slots.add(clonedSlot);
            }
        }
        return slots;
    }

    @NotNull
    private static <T extends GridSlot<T>> T getTopLeftSlot(@NotNull T cornerA, @NotNull T cornerB) {
        int minXCoord = Math.min(cornerA.getX(), cornerB.getX());
        int minYCoord = Math.min(cornerA.getY(), cornerB.getY());

        T clonedSlot = Slot.cloneSlot(cornerA);
        clonedSlot.setX(minXCoord);
        clonedSlot.setY(minYCoord);
        return clonedSlot;
    }

    @NotNull
    private static <T extends GridSlot<T>> T getBottomRightSlot(@NotNull T cornerA, @NotNull T cornerB) {
        int maxXCoord = Math.max(cornerA.getX(), cornerB.getX());
        int maxYCoord = Math.max(cornerA.getY(), cornerB.getY());

        T clonedSlot = Slot.cloneSlot(cornerA);
        clonedSlot.setX(maxXCoord);
        clonedSlot.setY(maxYCoord);
        return clonedSlot;
    }
    
    /**
     * Prepares the array for {@link SlotRange#toArray(U[])} implementation.
     * If supplied array is smaller than this map size, a new array is allocated.
     * If supplied array is bigger than this map size, a null is written at size index.
     *
     * @param array an original array passed to {@code toArray()} method
     * @param <U> type of array elements
     * @return an array ready to be filled and returned from {@code toArray()} method.
     */
    @SuppressWarnings("unchecked")
    private <U extends GridSlot<T>> U[] prepareArray(@NotNull U[] array, int setSize) {
        Preconditions.checkNotNull(array, "array cannot be null");
        if (array.length < setSize) {
            return (U[]) java.lang.reflect.Array
                    .newInstance(array.getClass().getComponentType(), setSize);
        }
        if (array.length > setSize) {
            array[setSize] = null;
        }
        return array;
    }
}