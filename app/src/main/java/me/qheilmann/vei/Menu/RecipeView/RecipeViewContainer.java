package me.qheilmann.vei.Menu.RecipeView;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.RecipePanel.RecipePanelSlot;
import me.qheilmann.vei.Menu.RecipeView.ViewSlot.ViewSlot.Cycle;

public class RecipeViewContainer {
    private HashMap<RecipePanelSlot, GuiItem<RecipeMenu>> recipeViewSlots;

    public RecipeViewContainer() {
        recipeViewSlots = new HashMap<>();
    }

    public void setViewSlot(@NotNull RecipePanelSlot slot, @NotNull GuiItem<RecipeMenu> guiItem) {
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

    public HashMap<RecipePanelSlot, GuiItem<RecipeMenu>> getContainer() {
        return recipeViewSlots;
    }

    public GuiItem<RecipeMenu> getGuiItem(@NotNull RecipePanelSlot slot) {
        return recipeViewSlots.get(slot);
    }
}