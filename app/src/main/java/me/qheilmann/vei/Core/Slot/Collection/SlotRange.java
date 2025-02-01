package me.qheilmann.vei.Core.Slot.Collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.Slot.GridSlot;
import me.qheilmann.vei.Core.Slot.Slot;
import me.qheilmann.vei.Core.Slot.Comparator.IndexSlotComparator;

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
     * @param cornerB     The second corner of the range, on the opposite side of cornerA
     */
    public SlotRange(@NotNull T cornerA, @NotNull T cornerB) {
        super(getSlotsBetween(cornerA, cornerB));
        this.topLeftSlot = getTopLeftSlot(cornerA, cornerB);
        this.bottomRightSlot = getBottomRightSlot(cornerA, cornerB);
        super.sort(new IndexSlotComparator());
    }

    public SlotRange(@NotNull SlotRange<T> slotRange) {
        super(slotRange);
        this.topLeftSlot = slotRange.getTopLeftSlot();
        this.bottomRightSlot = slotRange.getBottomRightSlot();
        super.sort(new IndexSlotComparator());
    }

    /**
     * Get the top left slot of the range
     * 
     * @return The top left slot
     */
    @NotNull
    public T getTopLeftSlot() {
        return topLeftSlot;
    }

    /**
     * Get the bottom right slot of the range
     * 
     * @return The bottom right slot
     */
    @NotNull
    public T getBottomRightSlot() {
        return bottomRightSlot;
    }

    /**
     * Set the top left slot of the range, can be on the other side 
     * 
     * @throws IllegalArgumentException if topLeftSlot is null
     */
    public void setTopLeftSlot(@NotNull T topLeftSlot) {
        Preconditions.checkArgument(topLeftSlot != null, "topLeftSlot cannot be null");

        this.clear();
        this.addAll(getSlotsBetween(topLeftSlot, this.bottomRightSlot));
        super.sort(new IndexSlotComparator());

        // Update the top left slot, sometimes the top left slot is not the top left 
        // slot anymore so we need to update both corners
        this.bottomRightSlot = getBottomRightSlot(topLeftSlot, bottomRightSlot);
        this.topLeftSlot = getTopLeftSlot(topLeftSlot, bottomRightSlot);
    }

    public void setBottomRightSlot(@NotNull T bottomRightSlot) {
        Preconditions.checkArgument(bottomRightSlot != null, "bottomRightSlot cannot be null");
        
        this.clear();
        this.addAll(getSlotsBetween(this.topLeftSlot, bottomRightSlot));
        super.sort(new IndexSlotComparator());

        // Update the bottom right slot, sometimes the bottom right slot is not the bottom right 
        // slot anymore so we need to update both corners
        this.bottomRightSlot = getBottomRightSlot(topLeftSlot, bottomRightSlot);
        this.topLeftSlot = getTopLeftSlot(topLeftSlot, bottomRightSlot);
    }

    // Make the range immutable

    @Override
    public Slot<T> set(int index, Slot<T> element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Slot<T> slot) {
        throw new UnsupportedOperationException("Cannot add slots to a SlotRange");
    }

    @Override
    public Slot<T> remove(int index) {
        throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Slot<T>> collection) {
        throw new UnsupportedOperationException("Cannot add slots to a SlotRange");
    }

    @Override
    public void replaceAll(UnaryOperator<Slot<T>> operator) {
        throw new UnsupportedOperationException("Cannot modify slots in a SlotRange");
    }

    @Override
    public void sort(Comparator<? super Slot<T>> collection) {
        throw new UnsupportedOperationException("Cannot sort slots in a SlotRange with a comparator, the slots are already ordered");
    }

    @Override
    @NotNull
    public ListIterator<Slot<T>> listIterator(final int index) {
        var list = this;

        return new ListIterator<>() {
            private final ListIterator<? extends Slot<T>> i
                = list.listIterator(index);

            public boolean hasNext()     {return i.hasNext();}
            public Slot<T> next()        {return i.next();}
            public boolean hasPrevious() {return i.hasPrevious();}
            public Slot<T> previous()    {return i.previous();}
            public int nextIndex()       {return i.nextIndex();}
            public int previousIndex()   {return i.previousIndex();}

            public void remove() {
                throw new UnsupportedOperationException("Cannot remove slots from a SlotRange");
            }
            public void set(Slot<T> e) {
                throw new UnsupportedOperationException("Cannot modify slots in a SlotRange");
            }
            public void add(Slot<T> e) {
                throw new UnsupportedOperationException("Cannot add slots to a SlotRange");
            }

            @Override
            public void forEachRemaining(Consumer<? super Slot<T>> action) {
                i.forEachRemaining(action);
            }
        };
    }

    @Override
    public List<Slot<T>> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Cannot create a sublist of a SlotRange");
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
        for (int y = topLeftSlot.getY(); y <= bottomRightSlot.getY(); y++) {
            for (int x = topLeftSlot.getX(); x <= bottomRightSlot.getX(); x++) {
                T slot = validateAndSupplySlot(cornerA.getSupplier());
                slot.setX(x);
                slot.setY(y);
                slots.add(slot);
            }
        }
        return slots;
    }

    @NotNull
    private static <T extends GridSlot<T>> T getTopLeftSlot(@NotNull T cornerA, @NotNull T cornerB) {
        int minXCoord = Math.min(cornerA.getX(), cornerB.getX());
        int minYCoord = Math.min(cornerA.getY(), cornerB.getY());

        T newSlot = validateAndSupplySlot(cornerA.getSupplier());
        newSlot.setX(minXCoord);
        newSlot.setY(minYCoord);
        return newSlot;
    }

    @NotNull
    private static <T extends GridSlot<T>> T getBottomRightSlot(@NotNull T cornerA, @NotNull T cornerB) {
        int maxXCoord = Math.max(cornerA.getX(), cornerB.getX());
        int maxYCoord = Math.max(cornerA.getY(), cornerB.getY());

        T newSlot = validateAndSupplySlot(cornerA.getSupplier());
        newSlot.setX(maxXCoord);
        newSlot.setY(maxYCoord);
        return newSlot;
    }

    @NotNull
    private static <T extends GridSlot<T>> T validateAndSupplySlot(@NotNull Supplier<T> specifiqueSlotSupplier) {
        Preconditions.checkNotNull(specifiqueSlotSupplier, "specifiqueSlotSupplier cannot be null");
        T slot = specifiqueSlotSupplier.get();
        Preconditions.checkNotNull(slot, "The slot supplier must not return null");
        return slot;
    }
}