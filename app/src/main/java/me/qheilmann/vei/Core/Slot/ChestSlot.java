package me.qheilmann.vei.Core.Slot;

import com.google.common.base.Preconditions;

public class ChestSlot extends Slot<ChestSlot> {
    public static final int ROW_COUNT = 6;
    public static final int COLUMN_COUNT = 9;

    public ChestSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public void setX(int x) {
        Preconditions.checkArgument(x >= 0 && x < COLUMN_COUNT, "x must be between %d and %d, current value: %d", 0, COLUMN_COUNT, x);
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        Preconditions.checkArgument(y >= 0 && y < ROW_COUNT, "y must be between %d and %d, current value: %d", 0, ROW_COUNT, y);
        super.setY(y);
    }

    @Override
    public ChestSlot createDuplicate() {
        return new ChestSlot(getX(), getY());
    }

    @Override
    public int getIndex() {
        return getY() * COLUMN_COUNT + getX();
    }

    // Static methods for common chest slots

    // Corner slots
    public static ChestSlot TOP_LEFT() {
        return new ChestSlot(0, 0);
    }

    public static ChestSlot TOP_RIGHT() {
        return new ChestSlot(COLUMN_COUNT-1, 0);
    }

    public static ChestSlot BOTTOM_LEFT() {
        return new ChestSlot(0, ROW_COUNT-1);
    }

    public static ChestSlot BOTTOM_RIGHT() {
        return new ChestSlot(COLUMN_COUNT-1, ROW_COUNT-1);
    }

    // All slots
    public static SlotRange<ChestSlot> getAllSlot() {
        return new SlotRange<>(TOP_LEFT(), BOTTOM_RIGHT());
    }

    // Row slots
    public static SlotRange<ChestSlot> getTopRow() {
        return new SlotRange<>(TOP_LEFT(), TOP_RIGHT());
    }

    public static SlotRange<ChestSlot> getSecondRow() {
        return new SlotRange<>(new ChestSlot(0, 1), new ChestSlot(COLUMN_COUNT-1, 1));
    }

    public static SlotRange<ChestSlot> getThirdRow() {
        return new SlotRange<>(new ChestSlot(0, 2), new ChestSlot(COLUMN_COUNT-1, 2));
    }

    public static SlotRange<ChestSlot> getFourthRow() {
        return new SlotRange<>(new ChestSlot(0, 3), new ChestSlot(COLUMN_COUNT-1, 3));
    }

    public static SlotRange<ChestSlot> getFifthRow() {
        return new SlotRange<>(new ChestSlot(0, 4), new ChestSlot(COLUMN_COUNT-1, 4));
    }

    public static SlotRange<ChestSlot> getBottomRow() {
        return new SlotRange<>(BOTTOM_LEFT(), BOTTOM_RIGHT());
    }

    // Column slots
    public static SlotRange<ChestSlot> getLeftColumn() {
        return new SlotRange<>(TOP_LEFT(), BOTTOM_LEFT());
    }

    public static SlotRange<ChestSlot> getSecondColumn() {
        return new SlotRange<>(new ChestSlot(1, 0), new ChestSlot(1, ROW_COUNT-1));
    }

    public static SlotRange<ChestSlot> getThirdColumn() {
        return new SlotRange<>(new ChestSlot(2, 0), new ChestSlot(2, ROW_COUNT-1));
    }

    public static SlotRange<ChestSlot> getFourthColumn() {
        return new SlotRange<>(new ChestSlot(3, 0), new ChestSlot(3, ROW_COUNT-1));
    }

    public static SlotRange<ChestSlot> getFifthColumn() {
        return new SlotRange<>(new ChestSlot(4, 0), new ChestSlot(4, ROW_COUNT-1));
    }

    public static SlotRange<ChestSlot> getSixthColumn() {
        return new SlotRange<>(new ChestSlot(5, 0), new ChestSlot(5, ROW_COUNT-1));
    }

    public static SlotRange<ChestSlot> getSeventhColumn() {
        return new SlotRange<>(new ChestSlot(6, 0), new ChestSlot(6, ROW_COUNT-1));
    }

    public static SlotRange<ChestSlot> getEighthColumn() {
        return new SlotRange<>(new ChestSlot(7, 0), new ChestSlot(7, ROW_COUNT-1));
    }

    public static SlotRange<ChestSlot> getRightColumn() {
        return new SlotRange<>(TOP_RIGHT(), BOTTOM_RIGHT());
    }
}
