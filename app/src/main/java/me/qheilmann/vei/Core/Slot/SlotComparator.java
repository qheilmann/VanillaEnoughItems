package me.qheilmann.vei.Core.Slot;

import java.util.Comparator;

/**
 * Compares two slots based on their coordinates.
 * First, it sorts by the row using the Y coordinate.
 * If the Y coordinates are equal, it then sorts by the column using the X coordinate.
 */
public class SlotComparator implements Comparator<Slot> {
    @Override
    public int compare(Slot slot1, Slot slot2) {
        if (slot1.getY() == slot2.getY()) {
            return slot1.getX() - slot2.getX();
        }
        return slot1.getY() - slot2.getY();
    }
}
