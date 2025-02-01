package me.qheilmann.vei.Core.Slot;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

public interface ISlotSupplier<T extends Slot<T>> {
    /**
     * Returns a supplier that provides a new slot instance each time it is 
     * called
     * <p>
     * Note: The slot can be edited.
     */
    @NotNull
    public Supplier<T> getSupplier();
}