package me.qheilmann.vei.Core.ProcessPanel.Panels;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanelSlot;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;

public class SmeltingProcessPanel extends ProcessPanel<FurnaceRecipe> {
    public static final ProcessPanelSlot NEXT_RECIPE_SLOT = new ProcessPanelSlot(3 , 0);
    public static final ProcessPanelSlot PREVIOUS_RECIPE_SLOT = new ProcessPanelSlot(1, 0);
    public static final ProcessPanelSlot FORWARD_RECIPE_SLOT = new ProcessPanelSlot(3, 4);
    public static final ProcessPanelSlot BACKWARD_RECIPE_SLOT = new ProcessPanelSlot(1, 4);
    public static final ProcessPanelSlot MOVE_INGREDIENTS_SLOT = new ProcessPanelSlot(5, 3);

    public static final ProcessPanelSlot INGREDIENT_SLOT = new ProcessPanelSlot(2, 1);
    public static final ProcessPanelSlot WORKBENCH_SLOT = new ProcessPanelSlot(2, 2);
    public static final ProcessPanelSlot COMBUSTIBLE_SLOT = new ProcessPanelSlot(2, 3);
    public static final ProcessPanelSlot RESULT_SLOT = new ProcessPanelSlot(5, 2);

    private static final Material WORKBENCH_DISPLAY_MATERIAL = Material.FURNACE;
    
    public SmeltingProcessPanel(@NotNull ProcessRecipeSet<FurnaceRecipe> recipes, int variant) {
        super(recipes, variant);
    }

    public SmeltingProcessPanel(@NotNull ProcessRecipeSet<FurnaceRecipe> recipes) {
        super(recipes);
    }

    @Override
    @Nullable
    protected ProcessPanelSlot getNextRecipeSlot() {
        return NEXT_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected ProcessPanelSlot getPreviousRecipeSlot() {
        return PREVIOUS_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected ProcessPanelSlot getForwardRecipeSlot() {
        return FORWARD_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected ProcessPanelSlot getBackwardRecipeSlot() {
        return BACKWARD_RECIPE_SLOT;
    }

    @Override
    @Nullable
    protected ProcessPanelSlot getMoveIngredientsSlot() {
        return MOVE_INGREDIENTS_SLOT;
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getIngredientSlots() {
        return new SlotSequence<ProcessPanelSlot>(List.of(INGREDIENT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getResultSlots() {
        return new SlotSequence<ProcessPanelSlot>(List.of(RESULT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getConsumableSlots() {
        return new SlotSequence<ProcessPanelSlot>(List.of(COMBUSTIBLE_SLOT));
    }

    @Override
    @Nullable
    protected ProcessPanelSlot getWorkbenchSlot() {
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
        VanillaEnoughItems.LOGGER.info("Simple cycle method called");
    }

    @Override
    public void render(EnumSet<AttachedButtonType> buttonsVisibility) {

        clear();

        placeWorkbench();

        FurnaceRecipe recipe = getCurrentRecipe();
        RecipeChoice recipeChoice = recipe.getInputChoice();
        if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            recipePanelSlots.put(INGREDIENT_SLOT, new GuiItem<RecipeMenu>(materialChoice.getItemStack()));
        }

        recipePanelSlots.put(COMBUSTIBLE_SLOT, new GuiItem<RecipeMenu>(Material.COAL));
        recipePanelSlots.put(RESULT_SLOT, new GuiItem<RecipeMenu>(recipe.getResult()));
        
        renderAttachedButtons(buttonsVisibility);
    }

    private void placeWorkbench() {
        GuiItem<RecipeMenu> noTooltipWorkbench = GuiItem.buildNoTooltipGuiItem(WORKBENCH_DISPLAY_MATERIAL);
        recipePanelSlots.put(WORKBENCH_SLOT, noTooltipWorkbench);
    }
}