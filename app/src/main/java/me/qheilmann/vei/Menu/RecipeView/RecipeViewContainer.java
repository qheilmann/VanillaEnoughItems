package me.qheilmann.vei.Menu.RecipeView;

import java.util.Collection;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import me.qheilmann.vei.Menu.RecipeView.ViewSlot.ViewSlot;
import me.qheilmann.vei.Menu.RecipeView.ViewSlot.ViewSlot.Cycle;

public class RecipeViewContainer {
    private HashMap<Vector2i, ViewSlot> viewSlots;

    public RecipeViewContainer() {
        viewSlots = new HashMap<>();
    }

    public void setViewSlot(@NotNull ViewSlot viewSlot) {
        viewSlots.put(viewSlot.getCoord(), viewSlot);
    }

    public void updateCycle() {
        for (ViewSlot viewSlot : viewSlots.values()) {
            viewSlot.updateCycle();
        }
    }

    public void updateCycle(Cycle cycle, int step) {
        for (ViewSlot viewSlot : viewSlots.values()) {
            viewSlot.updateCycle(cycle, step);
        }
    }

    public Collection<ViewSlot> getSlots() {
        return viewSlots.values();
    }

    public ViewSlot getSlot(@NotNull Vector2i coordinates) {
        return viewSlots.get(coordinates);
    }
}