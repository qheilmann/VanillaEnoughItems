package me.qheilmann.vei.Core.RecipePanel.Panels;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.RecipePanel.RecipePanel;
import me.qheilmann.vei.Core.RecipePanel.RecipePanelSlot;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;

public class FurnacePanelView extends RecipePanel<FurnaceRecipe> {
    public static final RecipePanelSlot NEXT_RECIPE_SLOT = new RecipePanelSlot(3 , 0);
    public static final RecipePanelSlot PREVIOUS_RECIPE_SLOT = new RecipePanelSlot(1, 0);
    public static final RecipePanelSlot FORWARD_RECIPE_SLOT = new RecipePanelSlot(3, 4);
    public static final RecipePanelSlot BACKWARD_RECIPE_SLOT = new RecipePanelSlot(1, 4);
    public static final RecipePanelSlot MOVE_INGREDIENTS_SLOT = new RecipePanelSlot(5, 3);

    public static final RecipePanelSlot INGREDIENT_SLOT = new RecipePanelSlot(2, 1);
    public static final RecipePanelSlot WORKBENCH_SLOT = new RecipePanelSlot(2, 2);
    public static final RecipePanelSlot COMBUSTIBLE_SLOT = new RecipePanelSlot(2, 3);
    public static final RecipePanelSlot RESULT_SLOT = new RecipePanelSlot(5, 2);

    private static final Material WORKBENCH_DISPLAY_MATERIAL = Material.FURNACE;

    public FurnacePanelView(@NotNull FurnaceRecipe recipe) {
        super(recipe);
        placeWorkbench();
    }

    @Override
    @Nullable
    protected RecipePanelSlot getNextRecipeSlot() {
        return NEXT_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipePanelSlot getPreviousRecipeSlot() {
        return PREVIOUS_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipePanelSlot getForwardRecipeSlot() {
        return FORWARD_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipePanelSlot getBackwardRecipeSlot() {
        return BACKWARD_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected RecipePanelSlot getMoveIngredientsSlot() {
        return MOVE_INGREDIENTS_SLOT;
    }

    @Override
    protected @NotNull SlotSequence<RecipePanelSlot> getIngredients() {
        return new SlotSequence<RecipePanelSlot>(List.of(INGREDIENT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<RecipePanelSlot> getResults() {
        return new SlotSequence<RecipePanelSlot>(List.of(RESULT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<RecipePanelSlot> getConsumables() {
        return new SlotSequence<RecipePanelSlot>(List.of(COMBUSTIBLE_SLOT));
    }

    @Override
    @Nullable
    protected RecipePanelSlot getWorkbenchSlot() {
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