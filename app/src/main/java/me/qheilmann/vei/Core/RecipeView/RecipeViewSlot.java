package me.qheilmann.vei.Core.RecipeView;

import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Slot.GridSlot;
import me.qheilmann.vei.Core.Slot.Implementation.MaxChestSlot;

public class RecipeViewSlot extends GridSlot {
    public static final int ROW_COUNT = 5;
    public static final int COLUMN_COUNT = 7;

    public RecipeViewSlot(int x, int y) {
        super(x, y, COLUMN_COUNT, ROW_COUNT);
    }

    public RecipeViewSlot(int index) {
        super(index, COLUMN_COUNT, ROW_COUNT);
    }

    public RecipeViewSlot(RecipeViewSlot slot) {
        super(slot);
    }

    @Override
    @NotNull
    public RecipeViewSlot clone() {
        return new RecipeViewSlot(this);
    }

    public MaxChestSlot asMaxChestSlot() {
        return new MaxChestSlot(this.getX()+1, this.getY()+1);
    }
}