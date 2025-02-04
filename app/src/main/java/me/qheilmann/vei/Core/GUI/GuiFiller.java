/**
 * MIT License
 *
 * Copyright (c) 2021 TriumphTeam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.qheilmann.vei.Core.GUI;

import me.qheilmann.vei.Core.Slot.GridSlot;

/*
 * 
 * @author Most original part come from Triumph GUI <a href="https://github.com/TriumphTeam/triumph-gui">TriumphTeam</a>
 */
public final class GuiFiller<G extends BaseGui<G, S>, S extends GridSlot> {

    private final BaseGui<G, S> gui;

    public GuiFiller(final BaseGui<G, S> gui) {
        this.gui = gui;
    }



    // TODO this need to be reworked, with the new GridSlot system



    // /**
    //  * Fills top portion of the GUI
    //  *
    //  * @param guiItem GuiItem
    //  */
    // public void fillTop(@NotNull final GuiItem<G> guiItem, @NotNull Supplier<S> specifiqueSlotSupplier) {
    //     fillTop(Collections.singletonList(guiItem), specifiqueSlotSupplier);
    // }

    // /**
    //  * Fills top portion of the GUI with alternation
    //  *
    //  * @param guiItems List of GuiItems
    //  */
    // public void fillTop(@NotNull final List<GuiItem<G>> guiItems, @NotNull Supplier<S> specifiqueSlotSupplier) {
    //     final List<GuiItem<G>> items = repeatList(guiItems);
    //     SlotRange<S> bottomRowSlots = GridSlot.getTopRow(specifiqueSlotSupplier);
    //     int i = 0;
    //     for (S slot : bottomRowSlots) {
    //         int slotIndex = slot.getIndex();
    //         if (gui.getGuiItems().get(slotIndex) == null) {
    //             gui.setItem(slot, items.get(i));
    //         }
    //         i++;
    //     }
    // }

    // /**
    //  * Fills bottom portion of the GUI
    //  *
    //  * @param guiItem GuiItem
    //  */
    // public void fillBottom(@NotNull final GuiItem<G> guiItem, @NotNull Supplier<S> specifiqueSlotSupplier) {
    //     fillBottom(Collections.singletonList(guiItem), specifiqueSlotSupplier);
    // }

    // /**
    //  * Fills bottom portion of the GUI with alternation
    //  *
    //  * @param guiItems GuiItem
    //  */
    // public void fillBottom(@NotNull final List<GuiItem<G>> guiItems, @NotNull Supplier<S> specifiqueSlotSupplier) {
    //     final List<GuiItem<G>> items = repeatList(guiItems);
    //     SlotRange<S> bottomRowSlots = GridSlot.getBottomRow(specifiqueSlotSupplier);
    //     int i = 0;
    //     for (S slot : bottomRowSlots) {
    //         if (gui.getGuiItem(slot) == null) {
    //             gui.setItem(slot, items.get(i)); // TODO warning here it will replace the item in the slot who are not GuiItem
    //         }
    //         i++;
    //     }
    // }

    // /**
    //  * Fills the outside section of the GUI with a GuiItem
    //  *
    //  * @param guiItem GuiItem
    //  */
    // public void fillBorder(@NotNull final GuiItem<G> guiItem) {
    //     fillBorder(Collections.singletonList(guiItem));
    // }

    // /**
    //  * Fill empty slots with Multiple GuiItems, goes through list and starts again
    //  *
    //  * @param guiItems GuiItem
    //  */
    // public void fillBorder(@NotNull final List<GuiItem<G>> guiItems) {
    //     final int rows = gui.getRows();
    //     if (rows <= 2) return;

    //     final List<GuiItem<G>> items = repeatList(guiItems);

    //     for (int i = 0; i < rows * 9; i++) {
    //         if ((i <= 8)
    //                 || (i >= (rows * 9) - 8) && (i <= (rows * 9) - 2)
    //                 || i % 9 == 0
    //                 || i % 9 == 8)
    //             gui.setItem(i, items.get(i));

    //     }
    // }

    // /**
    //  * Fills rectangle from points within the GUI
    //  *
    //  * @param rowFrom Row point 1
    //  * @param colFrom Col point 1
    //  * @param rowTo   Row point 2
    //  * @param colTo   Col point 2
    //  * @param guiItem Item to fill with
    //  * @author Harolds
    //  */
    // public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, @NotNull final GuiItem<G, S> guiItem) {
    //     fillBetweenPoints(rowFrom, colFrom, rowTo, colTo, Collections.singletonList(guiItem));
    // }

    // /**
    //  * Fills rectangle from points within the GUI
    //  *
    //  * @param rowFrom  Row point 1
    //  * @param colFrom  Col point 1
    //  * @param rowTo    Row point 2
    //  * @param colTo    Col point 2
    //  * @param guiItems Item to fill with
    //  * @author Harolds
    //  */
    // public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, @NotNull final List<GuiItem<G, S>> guiItems) {
    //     final int minRow = Math.min(rowFrom, rowTo);
    //     final int maxRow = Math.max(rowFrom, rowTo);
    //     final int minCol = Math.min(colFrom, colTo);
    //     final int maxCol = Math.max(colFrom, colTo);

    //     final int rows = gui.getRows();
    //     final List<GuiItem<G>> items = repeatList(guiItems);

    //     for (int row = 1; row <= rows; row++) {
    //         for (int col = 1; col <= 9; col++) {
    //             final int slot = getSlotFromRowCol(row, col);
    //             if (!((row >= minRow && row <= maxRow) && (col >= minCol && col <= maxCol)))
    //                 continue;

    //             gui.setItem(slot, items.get(slot));
    //         }
    //     }
    // }

    // /**
    //  * Sets an GuiItem to fill up the entire inventory where there is no other item
    //  *
    //  * @param guiItem The item to use as fill
    //  */
    // public void fill(@NotNull final GuiItem<G> guiItem) {
    //     fill(Collections.singletonList(guiItem));
    // }

    // /**
    //  * Fill empty slots with Multiple GuiItems, goes through list and starts again
    //  *
    //  * @param guiItems GuiItem
    //  */
    // public void fill(@NotNull final List<GuiItem<G>> guiItems) {

    //     final GuiType type = gui.guiType();

    //     final int fill;
    //     if (type == GuiType.CHEST) {
    //         fill = gui.getRows() * type.getLimit();
    //     } else {
    //         fill = type.getLimit();
    //     }

    //     final List<GuiItem<G>> items = repeatList(guiItems);
    //     for (int i = 0; i < fill; i++) {
    //         if (gui.getGuiItems().get(i) == null) gui.setItem(i, items.get(i));
    //     }
    // }

    // /**
    //  * Fills specified side of the GUI with a GuiItem
    //  *
    //  * @param guiItems GuiItem
    //  */
    // public void fillSide(@NotNull final Side side, @NotNull final List<GuiItem<G>> guiItems) {
    //     switch (side) {
    //         case LEFT:
    //             this.fillBetweenPoints(1, 1, gui.getRows(), 1, guiItems);
    //         case RIGHT:
    //             this.fillBetweenPoints(1, 9, gui.getRows(), 9, guiItems);
    //         case BOTH:
    //             this.fillBetweenPoints(1, 1, gui.getRows(), 1, guiItems);
    //             this.fillBetweenPoints(1, 9, gui.getRows(), 9, guiItems);
    //     }
    // }

    // /**
    //  * Repeats a list of items. Allows for alternating items
    //  * Stores references to existing objects -> Does not create new objects
    //  *
    //  * @param guiItems List of items to repeat
    //  * @return New list
    //  */
    // private List<GuiItem<G>> repeatList(@NotNull final List<GuiItem<G>> guiItems) {
    //     final List<GuiItem<G>> repeated = new ArrayList<>();
    //     Collections.nCopies(gui.getRows() * 9, guiItems).forEach(repeated::addAll);
    //     return repeated;
    // }

    // /**
    //  * Gets the slot from the row and col passed
    //  *
    //  * @param row The row
    //  * @param col The col
    //  * @return The new slot
    //  */
    // private int getSlotFromRowCol(final int row, final int col) {
    //     return (col + (row - 1) * 9) - 1;
    // }

    // public enum Side {
    //     LEFT,
    //     RIGHT,
    //     BOTH
    // }
}
