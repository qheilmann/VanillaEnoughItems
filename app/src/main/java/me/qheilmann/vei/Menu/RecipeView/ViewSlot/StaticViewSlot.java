package me.qheilmann.vei.Menu.RecipeView.ViewSlot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

/**
 * Represents a static slot in the view
 * This slot is not meant to be change between different recipes
 */
public class StaticViewSlot extends ViewSlot {
    
    ItemStack item;

    public StaticViewSlot(Vector2i coord, @NotNull ItemStack item) {
        super(coord);
        setItemStack(item);
    }

    public void setItemStack(@NotNull ItemStack item) {
        this.item = item;
    }

    @Override
    public void updateCycle() {
        return;
    }

    @Override
    public void updateCycle(@NotNull Cycle cycle, int step) {
        return;
    }

    @Override
    public ItemStack getCurrentItemStack() {
        return item.clone();
    }
}