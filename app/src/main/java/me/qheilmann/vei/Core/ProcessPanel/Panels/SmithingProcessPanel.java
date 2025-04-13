package me.qheilmann.vei.Core.ProcessPanel.Panels;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanelSlot;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.Core.Style.Styles.Style;
import me.qheilmann.vei.foundation.gui.GuiItemService;

public class SmithingProcessPanel extends ProcessPanel<SmithingRecipe> {
    public static final ProcessPanelSlot NEXT_RECIPE_SLOT = new ProcessPanelSlot(3 , 0);
    public static final ProcessPanelSlot PREVIOUS_RECIPE_SLOT = new ProcessPanelSlot(1, 0);
    public static final ProcessPanelSlot FORWARD_RECIPE_SLOT = new ProcessPanelSlot(3, 4);
    public static final ProcessPanelSlot BACKWARD_RECIPE_SLOT = new ProcessPanelSlot(1, 4);
    public static final ProcessPanelSlot MOVE_INGREDIENTS_SLOT = new ProcessPanelSlot(5, 3);

    public static final ProcessPanelSlot TEMPLATE_SLOT = new ProcessPanelSlot(1, 2);
    public static final ProcessPanelSlot BASE_SLOT = new ProcessPanelSlot(2, 2);
    public static final ProcessPanelSlot ADDITION_SLOT = new ProcessPanelSlot(3, 2);
    public static final ProcessPanelSlot WORKBENCH_SLOT = new ProcessPanelSlot(4, 2);
    public static final ProcessPanelSlot RESULT_SLOT = new ProcessPanelSlot(5, 2);

    private static final Material WORKBENCH_DISPLAY_MATERIAL = Material.SMITHING_TABLE;
    
    public SmithingProcessPanel(@NotNull Style style, @NotNull RecipeIndexService recipeIndex, @NotNull ProcessRecipeReader<SmithingRecipe> recipeReader) {
        super(style, recipeIndex, recipeReader);
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
        return new SlotSequence<ProcessPanelSlot>(List.of());
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getResultSlots() {
        return new SlotSequence<ProcessPanelSlot>(List.of(RESULT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getConsumableSlots() {
        return new SlotSequence<ProcessPanelSlot>(List.of(TEMPLATE_SLOT, BASE_SLOT, ADDITION_SLOT));
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
        super.render(buttonsVisibility);

        placeWorkbench();


        SmithingRecipe recipe = getCurrentRecipe();
        RecipeChoice baseChoice = recipe.getBase();
        RecipeChoice additionChoice = recipe.getAddition();

        if (recipe instanceof SmithingTransformRecipe transformRecipe) {
            RecipeChoice templateChoice = transformRecipe.getTemplate();
            populateSmithingTransformAndTrimRecipe(templateChoice, baseChoice, additionChoice);
        } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
            RecipeChoice templateChoice = trimRecipe.getTemplate();
            populateSmithingTransformAndTrimRecipe(templateChoice, baseChoice, additionChoice);
        }

        recipePanelSlots.put(RESULT_SLOT, new GuiItem<>(recipe.getResult()));        
    }

    private void populateSmithingTransformAndTrimRecipe(RecipeChoice templateChoice, RecipeChoice baseChoice, RecipeChoice additionChoice) {
        GuiItem<RecipeMenu> templateItem;
        GuiItem<RecipeMenu> baseItem;
        GuiItem<RecipeMenu> additionItem;
        
        if (templateChoice instanceof RecipeChoice.MaterialChoice) {
            templateItem = buildNewRecipeGuiItem(((RecipeChoice.MaterialChoice) templateChoice).getItemStack());
        } else {
            // TODO remove the use a deprecated method for generating the warning item
            templateItem = new GuiItem<>(new GuiItemService().CreateWarningItem(
                "Conversion of the template item to a MaterialChoice is not supported. The current tempalte type is %s".formatted(templateChoice.getClass().getName())
            ));
        }
        if (baseChoice instanceof RecipeChoice.MaterialChoice) {
            baseItem = buildNewRecipeGuiItem(((RecipeChoice.MaterialChoice) baseChoice).getItemStack());
        } else {
            baseItem = new GuiItem<>(new GuiItemService().CreateWarningItem(
                "Conversion of the base item to a MaterialChoice is not supported. The current base type is %s".formatted(baseChoice.getClass().getName())
            ));
        }
        if (additionChoice instanceof RecipeChoice.MaterialChoice) {
            additionItem = buildNewRecipeGuiItem(((RecipeChoice.MaterialChoice) additionChoice).getItemStack());
        } else {
            additionItem = new GuiItem<>(new GuiItemService().CreateWarningItem(
                "Conversion of the addition item to a MaterialChoice is not supported. The current addition type is %s".formatted(additionChoice.getClass().getName())
            ));
        }
        
        recipePanelSlots.put(TEMPLATE_SLOT, templateItem);
        recipePanelSlots.put(BASE_SLOT, baseItem);
        recipePanelSlots.put(ADDITION_SLOT, additionItem);
    }

    private void placeWorkbench() {
        GuiItem<RecipeMenu> noTooltipWorkbench = GuiItem.buildNoTooltipGuiItem(WORKBENCH_DISPLAY_MATERIAL);
        recipePanelSlots.put(WORKBENCH_SLOT, noTooltipWorkbench);
    }
}