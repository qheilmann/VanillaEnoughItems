package me.qheilmann.vei.Core.Slot;

import java.util.Comparator;

/**
 * Compares two slots based on their index.
 */
public class IndexSlotComparator implements Comparator<Slot<?>> {
    @Override
    public int compare(Slot<?> slot1, Slot<?> slot2) {
        return Integer.compare(slot1.getIndex(), slot2.getIndex());
    }
}
