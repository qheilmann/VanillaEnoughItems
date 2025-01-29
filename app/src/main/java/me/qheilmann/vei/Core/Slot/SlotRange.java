package me.qheilmann.vei.Core.Slot;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * Represents a range of slots in a 9x6 grid (max chest size)
 */
public class SlotRange {

    public static final SlotRange TOP_ROW = new SlotRange(Slot.TOP_LEFT, Slot.TOP_RIGHT);
    public static final SlotRange BOTTOM_ROW = new SlotRange(Slot.BOTTOM_LEFT, Slot.BOTTOM_RIGHT);
    public static final SlotRange LEFT_COLUMN = new SlotRange(Slot.TOP_LEFT, Slot.BOTTOM_LEFT);
    public static final SlotRange RIGHT_COLUMN = new SlotRange(Slot.TOP_RIGHT, Slot.BOTTOM_RIGHT);
    public static final SlotRange ALL = new SlotRange(Slot.TOP_LEFT, Slot.BOTTOM_RIGHT);
    public static final SlotRange EMPTY = new SlotRange(new LinkedHashSet<>());

    private final Set<Slot> slots;
    private final Slot topLeftSlot;
    private final Slot bottomRightSlot;

    /**
     * Defines a range of slots in a 9x6 grid (max chest size)
     * 
     * @param cornerA     The first corner of the range
     * @param cornerB     The second corner of the range, on the opposite side of cornerA
     */
    public SlotRange(Slot cornerA, Slot cornerB) {
        Preconditions.checkArgument(cornerA != null, "cornerA cannot be null");
        Preconditions.checkArgument(cornerB != null, "cornerB cannot be null");

        // Adjust the corners so that topLeftSlot is always the top left corner and bottomRightSlot is always the bottom right corner
        int topLeftX = Math.min(cornerA.getX(), cornerB.getX());
        int topLeftY = Math.min(cornerA.getY(), cornerB.getY());
        int bottomRightX = Math.max(cornerA.getX(), cornerB.getX());
        int bottomRightY = Math.max(cornerA.getY(), cornerB.getY());

        this.topLeftSlot = new Slot(topLeftX, topLeftY);
        this.bottomRightSlot = new Slot(bottomRightX, bottomRightY);

        Set<Slot> cslots = new LinkedHashSet<>();
        for (int x = topLeftSlot.getX(); x <= bottomRightSlot.getX(); x++) {
            for (int y = topLeftSlot.getY(); y <= bottomRightSlot.getY(); y++) {
                cslots.add(new Slot(x, y));
            }
        }

        this.slots = Collections.unmodifiableSet(cslots);
    }

    /**
     * Defines a list of slots in a 9x6 grid (max chest size)
     * @param slots The slots to include in the range
     */
    public SlotRange(Set<Slot> slots) {
        this.slots = Collections.unmodifiableSet(slots);

        this.topLeftSlot = slots.stream()
            .min(new SlotComparator())
            .orElse(null);
        
        this.bottomRightSlot = slots.stream()
            .max(new SlotComparator())
            .orElse(null);
    }

    /**
     * Get the top left slot of the range
     * 
     * @return The top left slot
     */
    public Slot getTopLeftSlot() {
        return topLeftSlot;
    }

    /**
     * Get the bottom right slot of the range
     * 
     * @return The bottom right slot
     */
    public Slot getBottomRightSlot() {
        return bottomRightSlot;
    }

    /**
     * Get all the slots in the range
     * 
     * @return All slots in the range
     */
    public Set<Slot> getSlots() {
        return slots;
    }
}