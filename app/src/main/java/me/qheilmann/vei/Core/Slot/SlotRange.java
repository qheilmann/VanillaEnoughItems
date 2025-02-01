package me.qheilmann.vei.Core.Slot;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

/**
 * Defines a range of slots within a grid, specified by two corner slots.
 * The slots within the range are ordered row by row, starting from the first row in the first column,
 * then the first row in the second column, and so on.
*/
public class SlotRange<T extends Slot<T>> extends SlotSequence {

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
    }

    public SlotRange(@NotNull SlotRange<T> slotRange) {
        super(slotRange);
        this.topLeftSlot = slotRange.getTopLeftSlot();
        this.bottomRightSlot = slotRange.getBottomRightSlot();
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

        // Update the top left slot, sometimes the top left slot is not the top left 
        // slot anymore so we need to update both corners
        this.bottomRightSlot = getBottomRightSlot(topLeftSlot, bottomRightSlot);
        this.topLeftSlot = getTopLeftSlot(topLeftSlot, bottomRightSlot);
    }

    public void setBottomRightSlot(@NotNull T bottomRightSlot) {
        Preconditions.checkArgument(bottomRightSlot != null, "bottomRightSlot cannot be null");
        
        this.clear();
        this.addAll(getSlotsBetween(this.topLeftSlot, bottomRightSlot));

        // Update the bottom right slot, sometimes the bottom right slot is not the bottom right 
        // slot anymore so we need to update both corners
        this.bottomRightSlot = getBottomRightSlot(topLeftSlot, bottomRightSlot);
        this.topLeftSlot = getTopLeftSlot(topLeftSlot, bottomRightSlot);
    }

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
    private static <T extends Slot<T>> ArrayList<T> getSlotsBetween(@NotNull T cornerA, @NotNull T cornerB) {
        Preconditions.checkArgument(cornerA != null, "cornerA cannot be null");
        Preconditions.checkArgument(cornerB != null, "cornerB cannot be null");

        T topLeftSlot = getTopLeftSlot(cornerA, cornerB);
        T bottomRightSlot = getBottomRightSlot(cornerA, cornerB);

        ArrayList<T> slots = new ArrayList<>();
        for (int y = topLeftSlot.getY(); y <= bottomRightSlot.getY(); y++) {
            for (int x = topLeftSlot.getX(); x <= bottomRightSlot.getX(); x++) {
                T slot = cornerA.createDuplicate();
                slot.setX(x);
                slot.setY(y);
                slots.add(slot);
            }
        }
        return slots;
    }

    private static <T extends Slot<T>> T getTopLeftSlot(@NotNull T cornerA, @NotNull T cornerB) {
        int minXCoord = Math.min(cornerA.getX(), cornerB.getX());
        int minYCoord = Math.min(cornerA.getY(), cornerB.getY());

        T newSlot = cornerA.createDuplicate();
        newSlot.setX(minXCoord);
        newSlot.setY(minYCoord);
        return newSlot;
    }

    private static <T extends Slot<T>> T getBottomRightSlot(@NotNull T cornerA, @NotNull T cornerB) {
        int maxXCoord = Math.max(cornerA.getX(), cornerB.getX());
        int maxYCoord = Math.max(cornerA.getY(), cornerB.getY());

        T newSlot = cornerA.createDuplicate();
        newSlot.setX(maxXCoord);
        newSlot.setY(maxYCoord);
        return newSlot;
    }
}