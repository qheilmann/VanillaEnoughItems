package dev.qheilmann.vanillaenoughitems.utils.fastinv;

import java.util.LinkedHashSet;
import org.bukkit.inventory.MenuType;

import net.kyori.adventure.key.Key;

public class Slots {

    public static int slot(int column, int row, MenuType menuType) {
                
        Key menuKey = menuType.key();

        if (menuKey.equals(MenuType.GENERIC_9X1.key())) {
            return generic9x1(column, row);
        }
        if (menuKey.equals(MenuType.GENERIC_9X2.key())) {
            return generic9x2(column, row);
        }
        if (menuKey.equals(MenuType.GENERIC_9X3.key())) {
            return generic9x3(column, row);
        }
        if (menuKey.equals(MenuType.GENERIC_9X4.key())) {
            return generic9x4(column, row);
        }
        if (menuKey.equals(MenuType.GENERIC_9X5.key())) {
            return generic9x5(column, row);
        }
        if (menuKey.equals(MenuType.GENERIC_9X6.key())) {
            return generic9x6(column, row);
        }        
        if (menuKey.equals(MenuType.GENERIC_3X3.key())) {
            return generic3x3(column, row);
        }
        throw new IllegalArgumentException("Unsupported MenuType: " + menuType);
    }

    /**
     * Calculates the slot index for a 3x3 generic inventory.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int generic3x3(int column, int row) {
        checkColumnBounds(column, 3);
        checkRowBounds(row, 3);
        return row * 3 + column;
    }

    /**
     * Calculates the slot index for a 9x1 generic inventory.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int generic9x1(int column, int row) {
        checkColumnBounds(column, 9);
        checkRowBounds(row, 1);
        return row * 9 + column;
    }

    /**
     * Calculates the slot index for a 9x2 generic inventory.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int generic9x2(int column, int row) {
        checkColumnBounds(column, 9);
        checkRowBounds(row, 2);
        return row * 9 + column;
    }

    /**
     * Calculates the slot index for a 9x3 generic inventory.<br>
     * Equivalent to {@link #smallChestSlot(int, int)}.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int generic9x3(int column, int row) {
        checkColumnBounds(column, 9);
        checkRowBounds(row, 3);
        return row * 9 + column;
    }

    /**
     * Calculates the slot index for a 9x4 generic inventory.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int generic9x4(int column, int row) {
        checkColumnBounds(column, 9);
        checkRowBounds(row, 4);
        return row * 9 + column;
    }

    /**
     * Calculates the slot index for a 9x5 generic inventory.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int generic9x5(int column, int row) {
        checkColumnBounds(column, 9);
        checkRowBounds(row, 5);
        return row * 9 + column;
    }

    /**
     * Calculates the slot index for a 9x6 generic inventory.<br>
     * Equivalent to {@link #maxChestSlot(int, int)}.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int generic9x6(int column, int row) {
        checkColumnBounds(column, 9);
        checkRowBounds(row, 6);
        return row * 9 + column;
    }

    /**
     * Calculates the slot index for a small chest inventory (9x3).<br>
     * Equivalent to {@link #generic9x3(int, int)}.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int smallChestSlot(int column, int row) {
        return generic9x3(column, row);
    }

    /**
     * Calculates the slot index for a large chest inventory (9x6).<br>
     * Equivalent to {@link #generic9x6(int, int)}.
     *
     * @param column the column index (0-based)
     * @param row the row index (0-based)
     * @return the slot index
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public static int maxChestSlot(int column, int row) {
        return generic9x6(column, row);
    }

    /**
     * Creates a set of slot indices in the specified range.
     *
     * @param start the starting slot index (inclusive)
     * @param end the ending slot index (inclusive)
     * @return a set of slot indices from start to end
     */
    public static LinkedHashSet<Integer> range(int start, int end) {
        LinkedHashSet<Integer> slots = new LinkedHashSet<>();
        for (int i = start; i <= end; i++) {
            slots.add(i);
        }
        return slots;
    }

    /**
     * Creates a set of slot indices in a grid range for a 9-column inventory.
     *
     * @param startCol the starting column index (0-based, inclusive)
     * @param startRow the starting row index (0-based, inclusive)
     * @param endCol the ending column index (0-based, inclusive)
     * @param endRow the ending row index (0-based, inclusive)
     * @return a set of slot indices in the specified grid range
     */
    public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
        final int columns = 9;
        return gridRange(startCol, startRow, endCol, endRow, columns);
    }

    /**
     * Creates a set of slot indices in a grid range with specified number of columns.
     *
     * @param startCol the starting column index (0-based, inclusive)
     * @param startRow the starting row index (0-based, inclusive)
     * @param endCol the ending column index (0-based, inclusive)
     * @param endRow the ending row index (0-based, inclusive)
     * @param columns the number of columns in the inventory
     * @return a set of slot indices in the specified grid range
     */
    public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow, int columns) {
        checkColumnBounds(startCol, columns);
        checkColumnBounds(endCol, columns);
        
        LinkedHashSet<Integer> slots = new LinkedHashSet<>();
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                slots.add(row * columns + col);
            }
        }
        return slots;
    }

    private static void checkRowBounds(int row, int maxRows) {
        if (row < 0 || row >= maxRows) {
            throw new IndexOutOfBoundsException("Row " + row + " is out of bounds (0-" + (maxRows - 1) + ")");
        }
    }

    private static void checkColumnBounds(int column, int maxColumns) {
        if (column < 0 || column >= maxColumns) {
            throw new IndexOutOfBoundsException("Column " + column + " is out of bounds (0-" + (maxColumns - 1) + ")");
        }
    }
}
