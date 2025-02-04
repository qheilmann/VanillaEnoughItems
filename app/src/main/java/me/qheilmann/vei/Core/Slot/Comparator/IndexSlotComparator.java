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
        // A method like this should not really check for null values each 
        // time, so here we don't check for null values
        return Integer.compare(slot1.getIndex(), slot2.getIndex());
    }
}
