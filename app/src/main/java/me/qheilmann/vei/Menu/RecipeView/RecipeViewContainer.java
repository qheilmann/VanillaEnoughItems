package me.qheilmann.vei.Menu.RecipeView;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.RecipeView.RecipeViewSlot;
import me.qheilmann.vei.Menu.RecipeView.ViewSlot.ViewSlot.Cycle;

public class RecipeViewContainer {
    private HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> recipeViewSlots;

    public RecipeViewContainer() {
        recipeViewSlots = new HashMap<>();
    }

    public void setViewSlot(@NotNull RecipeViewSlot slot, @NotNull GuiItem<RecipeMenu> guiItem) {
        recipeViewSlots.put(slot, guiItem);
    }

    public void updateCycle() {
        // for (RecipeViewSlot viewSlot : recipeViewSlots.keySet()) {
        //     // viewSlot.updateCycle();
        // }
    }

    public void updateCycle(Cycle cycle, int step) {
        // for (ViewSlot viewSlot : recipeViewSlots.values()) {
        //     viewSlot.updateCycle(cycle, step);
        // }
    }

    public HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> getContainer() {
        return recipeViewSlots;
    }

    public GuiItem<RecipeMenu> getGuiItem(@NotNull RecipeViewSlot slot) {
        return recipeViewSlots.get(slot);
    }
}