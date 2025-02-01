package me.qheilmann.vei.Core.Slot.Comparator;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.Slot.Slot;

/**
 * Compares two slots based on their index.
 */
public class IndexSlotComparator implements Comparator<Slot<?>> {
    @Override
    public int compare(@NotNull Slot<?> slot1, @NotNull Slot<?> slot2) {
        Preconditions.checkNotNull(slot1, "slot1 cannot be null");
        Preconditions.checkNotNull(slot2, "slot2 cannot be null");

        return Integer.compare(slot1.getIndex(), slot2.getIndex());
    }
}
