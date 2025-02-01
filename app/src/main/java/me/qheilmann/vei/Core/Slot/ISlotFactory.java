package me.qheilmann.vei.Core.Slot;

public interface ISlotFactory<T extends Slot<T>> {
    T createDuplicate();
}