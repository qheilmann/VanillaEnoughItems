package me.qheilmann.vei.Menu.RecipeView.ViewSlot;

import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.joml.Vector2i;

public abstract class ViewSlot {

    private int x, y;

    abstract public void updateCycle();
    abstract public void updateCycle(@NotNull Cycle cycle, int step);
    abstract public ItemStack getCurrentItemStack();

    public ViewSlot(Vector2i coord) {
        setX(coord.x);
        setY(coord.y);
    }

    public Vector2i getCoord() {
        return new Vector2i(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(@Range(from = 0, to = 6) int x) {
        Validate.inclusiveBetween(0, 6, x);
        this.x = x;
    }

    public void setY(@Range(from = 0, to = 4) int y) {
        Validate.inclusiveBetween(0, 4, y);
        this.y = y;
    }

    public enum Cycle {
        POSITION,
        ORIGIN
    }
}
