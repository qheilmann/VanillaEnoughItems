package dev.qheilmann.vanillaenoughitems.utils.fastinv;

import java.util.LinkedHashSet;

public class Slots {

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
     * Creates a set of slot indices in a grid range with specified number of columns.
     *
     * @param startCol the starting column index (0-based, inclusive)
     * @param startRow the starting row index (0-based, inclusive)
     * @param endCol the ending column index (0-based, inclusive)
     * @param endRow the ending row index (0-based, inclusive)
     * @param inventoryColumnsNb the number of columns in the inventory
     * @return a set of slot indices in the specified grid range
     */
    public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow, int inventoryColumnsNb) {
        checkColumnBounds(startCol, inventoryColumnsNb);
        checkColumnBounds(endCol, inventoryColumnsNb);
        
        LinkedHashSet<Integer> slots = new LinkedHashSet<>();
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                slots.add(row * inventoryColumnsNb + col);
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

    /**
     * Utility class for 9x1 generic inventories.
     */
    public static class Generic9x1 {
        public static final int WIDTH = 9;
        public static final int HEIGHT = 1;
        public static final int SIZE = WIDTH * HEIGHT;
        
        public static int slot(int column, int row) {
            checkColumnBounds(column, WIDTH);
            checkRowBounds(row, HEIGHT);
            return row * WIDTH + column;
        }

        public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
            return Slots.gridRange(startCol, startRow, endCol, endRow, WIDTH);
        }

        public static LinkedHashSet<Integer> range(int start, int end) {
            return Slots.range(start, end);
        }

        public static LinkedHashSet<Integer> all() {
            return gridRange(0, 0, WIDTH - 1, HEIGHT - 1);
        }
    }

    /**
     * Utility class for 9x2 generic inventories.
     */
    public static class Generic9x2 {
        public static final int WIDTH = 9;
        public static final int HEIGHT = 2;
        public static final int SIZE = WIDTH * HEIGHT;
        
        public static int slot(int column, int row) {
            checkColumnBounds(column, WIDTH);
            checkRowBounds(row, HEIGHT);
            return row * WIDTH + column;
        }

        public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
            return Slots.gridRange(startCol, startRow, endCol, endRow, WIDTH);
        }

        public static LinkedHashSet<Integer> range(int start, int end) {
            return Slots.range(start, end);
        }

        public static LinkedHashSet<Integer> all() {
            return gridRange(0, 0, WIDTH - 1, HEIGHT - 1);
        }
    }

    /**
     * Utility class for 9x3 generic inventories (e.g., Small Chest).
     */
    public static class Generic9x3 {
        public static final int WIDTH = 9;
        public static final int HEIGHT = 3;
        public static final int SIZE = WIDTH * HEIGHT;
        
        public static int slot(int column, int row) {
            checkColumnBounds(column, WIDTH);
            checkRowBounds(row, HEIGHT);
            return row * WIDTH + column;
        }

        public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
            return Slots.gridRange(startCol, startRow, endCol, endRow, WIDTH);
        }

        public static LinkedHashSet<Integer> range(int start, int end) {
            return Slots.range(start, end);
        }

        public static LinkedHashSet<Integer> all() {
            return gridRange(0, 0, WIDTH - 1, HEIGHT - 1);
        }
    }

    /**
     * Utility class for 9x4 generic inventories.
     */
    public static class Generic9x4 {
        public static final int WIDTH = 9;
        public static final int HEIGHT = 4;
        public static final int SIZE = WIDTH * HEIGHT;
        
        public static int slot(int column, int row) {
            checkColumnBounds(column, WIDTH);
            checkRowBounds(row, HEIGHT);
            return row * WIDTH + column;
        }

        public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
            return Slots.gridRange(startCol, startRow, endCol, endRow, WIDTH);
        }

        public static LinkedHashSet<Integer> range(int start, int end) {
            return Slots.range(start, end);
        }

        public static LinkedHashSet<Integer> all() {
            return gridRange(0, 0, WIDTH - 1, HEIGHT - 1);
        }
    }

    /**
     * Utility class for 9x5 generic inventories.
     */
    public static class Generic9x5 {
        public static final int WIDTH = 9;
        public static final int HEIGHT = 5;
        public static final int SIZE = WIDTH * HEIGHT;
        
        public static int slot(int column, int row) {
            checkColumnBounds(column, WIDTH);
            checkRowBounds(row, HEIGHT);
            return row * WIDTH + column;
        }

        public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
            return Slots.gridRange(startCol, startRow, endCol, endRow, WIDTH);
        }

        public static LinkedHashSet<Integer> range(int start, int end) {
            return Slots.range(start, end);
        }

        public static LinkedHashSet<Integer> all() {
            return gridRange(0, 0, WIDTH - 1, HEIGHT - 1);
        }
    }

    /**
     * Utility class for 9x6 generic inventories (e.g., Large Chest).
     */
    public static class Generic9x6 {
        public static final int WIDTH = 9;
        public static final int HEIGHT = 6;
        public static final int SIZE = WIDTH * HEIGHT;
        
        public static int slot(int column, int row) {
            checkColumnBounds(column, WIDTH);
            checkRowBounds(row, HEIGHT);
            return row * WIDTH + column;
        }

        public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
            return Slots.gridRange(startCol, startRow, endCol, endRow, WIDTH);
        }

        public static LinkedHashSet<Integer> range(int start, int end) {
            return Slots.range(start, end);
        }

        public static LinkedHashSet<Integer> all() {
            return gridRange(0, 0, WIDTH - 1, HEIGHT - 1);
        }
    }

    /**
     * Utility class for 3x3 generic inventories (e.g., Dispenser, Dropper, Crafting Table).
     */
    public static class Generic3x3 {
        public static final int WIDTH = 3;
        public static final int HEIGHT = 3;
        
        public static int slot(int column, int row) {
            checkColumnBounds(column, WIDTH);
            checkRowBounds(row, HEIGHT);
            return row * WIDTH + column;
        }

        public static LinkedHashSet<Integer> gridRange(int startCol, int startRow, int endCol, int endRow) {
            return Slots.gridRange(startCol, startRow, endCol, endRow, WIDTH);
        }

        public static LinkedHashSet<Integer> range(int start, int end) {
            return Slots.range(start, end);
        }

        public static LinkedHashSet<Integer> all() {
            return gridRange(0, 0, WIDTH - 1, HEIGHT - 1);
        }
    }
}
