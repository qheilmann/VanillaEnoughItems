package me.qheilmann.vei.Menu.RecipeView.ViewSlot;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class IngredientViewSlot extends ViewSlot {
    
    private MaterialChoice materialChoice;
    private int maxIndex;
    private int cycleIndex = 0;

    public IngredientViewSlot(Vector2i coord, @NotNull MaterialChoice materialChoice) {
        super(coord);
        setMaterialChoice(materialChoice);
    }

    public void setMaterialChoice(@NotNull MaterialChoice materialChoice) {
        this.materialChoice = materialChoice;
        this.maxIndex = materialChoice.getChoices().size() - 1;
    }

    public MaterialChoice getMaterialChoice() {
        return materialChoice;
    }

    @Override
    public ItemStack getCurrentItemStack() {
        return new ItemStack(materialChoice.getChoices().get(cycleIndex));
    }

    @Override
    public void updateCycle() {
        updateCycle(Cycle.POSITION, 1);
    }

    @Override
    public void updateCycle(@NotNull Cycle cycle, int step) {
        switch (cycle) {
            case POSITION:
                cycleIndex = Math.floorMod(cycleIndex + step, maxIndex + 1);
                break;
            case ORIGIN:
                cycleIndex = Math.floorMod(step, maxIndex + 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown "+ Cycle.class.getName() +" type: " + cycle);
        }
    }
}
