package me.qheilmann.vei.Menu.RecipeView.ViewSlot;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.NotNull;

public class IngredientViewSlot extends ViewSlot {
    
    private MaterialChoice materialChoice;
    private int maxIndex;
    private int cycleIndex = 0;

    public IngredientViewSlot(int x, int y, @NotNull MaterialChoice materialChoice) {
        super(x, y);
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
        updateCycle(Cycle.FORWARD, 1);
    }

    @Override
    public void updateCycle(@NotNull Cycle cycle, int step) {
        switch (cycle) {
            case FORWARD:
                cycleIndex = (cycleIndex + step) % (maxIndex + 1);
                break;
            case BACKWARD:
                cycleIndex = (cycleIndex - step) % (maxIndex + 1);
                if (cycleIndex < 0) {
                    cycleIndex += (maxIndex + 1);
                }
                break;
            case CYCLE_INDEX:
                cycleIndex = step % (maxIndex + 1);
                break;
        }
    }
}
