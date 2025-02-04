package me.qheilmann.vei.Core.Slot;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.Slot.Collection.SlotRange;

/**
 * Defines a slot in a grid inventory (like a chest, dispenser, etc.)
 */
public abstract class GridSlot<T extends GridSlot<T>> extends Slot<T> {
    private final int columnCount;
    private final int rowCount;

    /**
     * Defines a slot in a grid
     * 
     * @param index The index of the slot
     * @param columnCount The number of columns in the grid
     * @param rowCount The number of rows in the grid
     */
    public GridSlot(int index, int columnCount, int rowCount) {
        super(
            checkIndex(
                index, 
                checkColumnCount(columnCount), 
                checkRowCount(rowCount)
            )
        );
        this.columnCount = columnCount;
        this.rowCount = rowCount;
    }

    /**
     * Defines a slot in a grid
     * 
     * @param x The x coordinate of the slot
     * @param y The y coordinate of the slot
     * @param columnCount The number of columns in the grid
     * @param rowCount The number of rows in the grid
     */
    public GridSlot(int x, int y, int columnCount, int rowCount) {
        // yeah I know, this is a bit ugly, but it's because of the way Java 
        // handles constructors and how we need to validate the arguments
        this(
            calcIndex(
                checkX(x, checkColumnCount(columnCount)),
                checkY(y, checkRowCount(rowCount)),
                columnCount
            ),
            columnCount,
            rowCount
        );
    }

    /**
     * Copy constructor
     */
    public GridSlot(@NotNull GridSlot<T> slot) {
        super(slot);
        this.columnCount = slot.getColumnCount();
        this.rowCount = slot.getRowCount();
    }

    @Override
    @NotNull
    public abstract GridSlot<T> clone();

    @Override
    public void setIndex(int index) {
        checkIndex(index, columnCount, rowCount);
        this.index = index;
    }

    /**
     * Get the coordinates of the slot
     * 
     * @return A Vector2i representing the coordinates of the slot
     */
    @NotNull
    public Vector2i getCoord() {
        return new Vector2i(getX(), getY());
    }

    /**
     * Set the coordinates of the slot
     * 
     * @param coord A Vector2i representing the coordinates of the slot
     */
    public void setCoord(@NotNull Vector2i coord) {
        Preconditions.checkNotNull(coord, "coord cannot be null");
        setX(coord.x);
        setY(coord.y);
    }

    /**
     * Get the column of the slot, starting from 0
     * 
     * @return The x coordinate of the slot
     */
    public int getX() {
        return index % columnCount;
    }

    /**
     * Set the column of the slot, starting from 0
     * 
     * <p>Note: If you need to set both X and Y coordinates at the same time, 
     * prefer using {@link #setCoord(Vector2i)} method.</p>
     * 
     * @param x The x coordinate of the slot
     */
    public void setX(int x) {
        checkX(x, columnCount);
        index = calcIndex(x, getY(), columnCount);
    }
    
    /**
     * Get the row of the slot, starting from 0
     * 
     * @return The y coordinate of the slot
     */
    public int getY() {
        return index / columnCount;
    }

    /**
     * Set the row of the slot, starting from 0
     * 
     * <p>Note: If you need to set both X and Y coordinates at the same time, 
     * prefer using {@link #setCoord(Vector2i)} method.</p>
     * 
     * @param y The y coordinate of the slot
     */
    public void setY(int y) {
        checkY(y, rowCount);
        index = calcIndex(getX(), y, columnCount);

    }

    /**
     * Get the number of rows in the grid
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Get the number of columns in the grid
     */
    public int getColumnCount() {
        return columnCount;
    }

    // Corner slots

    /**
     * Get the first slot in the first row of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> T getTopLeft(@NotNull T slotTypeReference) {
        T topLeft = Slot.cloneSlot(slotTypeReference);
        topLeft.setX(0);
        topLeft.setY(0);
        return topLeft;
    }

    /**
     * Get the last slot in the first row of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> T getTopRight(@NotNull T slotTypeReference) {
        T topRight = Slot.cloneSlot(slotTypeReference);
        topRight.setX(topRight.getColumnCount() - 1);
        topRight.setY(0);
        return topRight;
    }

    /**
     * Get the first slot in the last row of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> T getBottomLeft(@NotNull T slotTypeReference) {
        T bottomLeft = Slot.cloneSlot(slotTypeReference);
        bottomLeft.setX(0);
        bottomLeft.setY(bottomLeft.getRowCount() - 1);
        return bottomLeft;
    }

    /**
     * Get the last slot in the last row of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> T getBottomRight(@NotNull T slotTypeReference) {
        T bottomRight = Slot.cloneSlot(slotTypeReference);
        bottomRight.setX(bottomRight.getColumnCount() - 1);
        bottomRight.setY(bottomRight.getRowCount() - 1);
        return bottomRight;
    }

    // All slots

    /**
     * Get all the slots in the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> SlotRange<T> getAllSlots(@NotNull T slotTypeReference) {
        T topLeft = getTopLeft(slotTypeReference);
        T bottomRight = getBottomRight(slotTypeReference);

        return new SlotRange<T>(topLeft, bottomRight);
    }

    // Row slots

    /**
     * Get all the slots in the first row of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> SlotRange<T> getTopRow(@NotNull T slotTypeReference) {
        T topLeft = getTopLeft(slotTypeReference);
        T topRight = getTopRight(slotTypeReference);

        return new SlotRange<T>(topLeft, topRight);
    }

    /**
     * Get all the slots in the last row of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> SlotRange<T> getBottomRow(@NotNull T slotTypeReference) {
        T bottomLeft = getBottomLeft(slotTypeReference);
        T bottomRight = getBottomRight(slotTypeReference);

        return new SlotRange<T>(bottomLeft, bottomRight);
    }

    /**
     * Get all the slots in a specific row of the grid
     * 
     * @param row The row number, starting from 0
     */
    @NotNull
    public static <T extends GridSlot<T>> SlotRange<T> getRow(int row, @NotNull T slotTypeReference) {
        T left = Slot.cloneSlot(slotTypeReference);
        left.setX(0);
        left.setY(row);

        T right = Slot.cloneSlot(slotTypeReference);
        right.setX(right.getColumnCount() - 1);
        right.setY(row);

        return new SlotRange<T>(left, right);
    }

    // Column slots

    /**
     * Get all the slots in the first column of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> SlotRange<T> getLeftColumn(@NotNull T slotTypeReference) {
        T topLeft = getTopLeft(slotTypeReference);
        T bottomLeft = getBottomLeft(slotTypeReference);

        return new SlotRange<T>(topLeft, bottomLeft);
    }

    /**
     * Get all the slots in the last column of the grid
     */
    @NotNull
    public static <T extends GridSlot<T>> SlotRange<T> getRightColumn(@NotNull T slotTypeReference) {
        T topRight = getTopRight(slotTypeReference);
        T bottomRight = getBottomRight(slotTypeReference);

        return new SlotRange<T>(topRight, bottomRight);
    }

    /**
     * Get all the slots in a specific column of the grid
     * 
     * @param column The column number, starting from 0
     */
    @NotNull
    public static <T extends GridSlot<T>> SlotRange<T> getColumn(int column, @NotNull T slotTypeReference) {
        T top = Slot.cloneSlot(slotTypeReference);
        top.setX(column);
        top.setY(0);

        T bottom = Slot.cloneSlot(slotTypeReference);
        bottom.setX(column);
        bottom.setY(bottom.getRowCount() - 1);

        return new SlotRange<T>(top, bottom);
    }
    
    protected static int calcIndex(int x, int y, int columnCount) {
        return y * columnCount + x;
    }
    
    // Check methods

    private static int checkX(int x, int columnCount) {
        Preconditions.checkArgument(x >= 0 && x < columnCount, "x must be between 0 and %d, current value: %d", columnCount, x);
        return x;
    }

    private static int checkY(int y, int rowCount) {
        Preconditions.checkArgument(y >= 0 && y < rowCount, "y must be between 0 and %d, current value: %d", rowCount, y);
        return y;
    }

    private static int checkRowCount(int rowCount) {
        Preconditions.checkArgument(rowCount > 0, "rowCount must be greater than 0, current value: %d", rowCount);
        return rowCount;
    }

    private static int checkColumnCount(int columnCount) {
        Preconditions.checkArgument(columnCount > 0, "columnCount must be greater than 0, current value: %d", columnCount);
        return columnCount;
    }

    private static int checkIndex(int index, int columnCount, int rowCount) {
        Preconditions.checkArgument(index >= 0 && index < columnCount * rowCount, "index must be between 0 and %d, current value: %d", columnCount * rowCount, index);
        return index;
    }
}
