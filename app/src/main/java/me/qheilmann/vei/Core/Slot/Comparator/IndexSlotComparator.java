package me.qheilmann.vei.Core.Slot.Comparator;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Slot.Slot;

/**
 * Compares two slots based on their index.
 */
public class IndexSlotComparator implements Comparator<Slot> {
    @Override
    public int compare(@NotNull Slot slot1, @NotNull Slot slot2) {
        if (slot1 == null && slot2 == null) {
            return 0;
        }
        if (slot1 == null) {
            return -1;
        }
        if (slot2 == null) {
            return 1;
        }
        return Integer.compare(slot1.getIndex(), slot2.getIndex());
    }
}
