package me.qheilmann.vei.Core.RecipeView.Views;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.RecipeView.RecipeView;
import me.qheilmann.vei.Core.RecipeView.RecipeViewSlot;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;

public class FurnaceRecipeView extends RecipeView<FurnaceRecipe> {
    public static final RecipeViewSlot NEXT_RECIPE_SLOT = new RecipeViewSlot(3 , 0);
    public static final RecipeViewSlot PREVIOUS_RECIPE_SLOT = new RecipeViewSlot(1, 0);
    public static final RecipeViewSlot FORWARD_RECIPE_SLOT = new RecipeViewSlot(3, 4);
    public static final RecipeViewSlot BACKWARD_RECIPE_SLOT = new RecipeViewSlot(1, 4);
    public static final RecipeViewSlot MOVE_INGREDIENTS_SLOT = new RecipeViewSlot(5, 3);

    public static final RecipeViewSlot INGREDIENT_SLOT = new RecipeViewSlot(2, 1);
    public static final RecipeViewSlot WORKBENCH_SLOT = new RecipeViewSlot(2, 2);
    public static final RecipeViewSlot COMBUSTIBLE_SLOT = new RecipeViewSlot(2, 3);
    public static final RecipeViewSlot RESULT_SLOT = new RecipeViewSlot(5, 2);

    private static final Material WORKBENCH_DISPLAY_MATERIAL = Material.FURNACE;

    public FurnaceRecipeView(@NotNull FurnaceRecipe recipe) {
        super(recipe);
        placeWorkbench();
    }

    @Override
    @Nullable
    protected RecipeViewSlot getNextRecipeSlot() {
        return NEXT_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipeViewSlot getPreviousRecipeSlot() {
        return PREVIOUS_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipeViewSlot getForwardRecipeSlot() {
        return FORWARD_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipeViewSlot getBackwardRecipeSlot() {
        return BACKWARD_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipeViewSlot getMoveIngredientsSlot() {
        return MOVE_INGREDIENTS_SLOT;
    }

    @Override
    protected @NotNull SlotSequence<RecipeViewSlot> getIngredientsSlotSequence() {
        return new SlotSequence<RecipeViewSlot>(List.of(INGREDIENT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<RecipeViewSlot> getResultsSlotSequence() {
        return new SlotSequence<RecipeViewSlot>(List.of(RESULT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<RecipeViewSlot> getConsumablesSlotSequence() {
        return new SlotSequence<RecipeViewSlot>(List.of(COMBUSTIBLE_SLOT));
    }

    @Override
    @Nullable
    protected RecipeViewSlot getWorkbenchSlot() {
        return WORKBENCH_SLOT;
    }

    @Override
    @Nullable
    protected Material getWorkbenchMaterial() {
        return WORKBENCH_DISPLAY_MATERIAL;
    }

    @Override
    public void cycle(EnumSet<SlotType> slotTypes) {
        // TODO implement cycle
    }

    @Override
    protected void populateCraftingSlots() {
        FurnaceRecipe recipe = getRecipe();

        RecipeChoice recipeChoice = recipe.getInputChoice();
        if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            recipeViewSlots.put(INGREDIENT_SLOT, new GuiItem<RecipeMenu>(materialChoice.getItemStack()));
        }

        recipeViewSlots.put(COMBUSTIBLE_SLOT, new GuiItem<RecipeMenu>(Material.COAL));
        recipeViewSlots.put(RESULT_SLOT, new GuiItem<RecipeMenu>(recipe.getResult()));

    }

    private void placeWorkbench() {
        GuiItem<RecipeMenu> noTooltipWorkbench = GuiItem.buildNoTooltipGuiItem(WORKBENCH_DISPLAY_MATERIAL);
        recipeViewSlots.put(WORKBENCH_SLOT, noTooltipWorkbench);
    }
}