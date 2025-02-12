package me.qheilmann.vei.Core.RecipePanel.Panels;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.RecipePanel.RecipePanel;
import me.qheilmann.vei.Core.RecipePanel.RecipePanelSlot;
import me.qheilmann.vei.Core.Slot.Collection.SlotRange;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.foundation.gui.GuiItemService;

/**
 * <h1>ShapedRecipeView</h1>
 * This class is used to display a shaped recipe view (7x5) in a GUI.
 * <p>
 * GUI representation:
 * <pre> 
 *-> x 0  1  2  3  4  5  6
 * y +---------------------+
 * 0 |    <     >          |
 * 1 |    i  i  i          |
 * 2 |    i  i  i  w  o    |
 * 3 |    i  i  i     +    |
 * 4 |    ^     v          |
 *   +---------------------+
 * </pre>
 * <ul>
 * <li>i: inputs (crafting grid)</li>
 * <li>o: outputs</li>
 * <li>w: workbench</li>
 * <li><, >: next/previous recipe</li>
 * <li>^, v: back/forward recipe</li>
 * <li>+: move ingredients</li>
 * </ul>
 */
public class ShapedRecipePanel extends RecipePanel<ShapedRecipe> {

    public static final RecipePanelSlot NEXT_RECIPE_SLOT = new RecipePanelSlot(3 , 0);
    public static final RecipePanelSlot PREVIOUS_RECIPE_SLOT = new RecipePanelSlot(1, 0);
    public static final RecipePanelSlot FORWARD_RECIPE_SLOT = new RecipePanelSlot(3, 4);
    public static final RecipePanelSlot BACKWARD_RECIPE_SLOT = new RecipePanelSlot(1, 4);
    public static final RecipePanelSlot MOVE_INGREDIENTS_SLOT = new RecipePanelSlot(5, 3);

    public static final SlotRange<RecipePanelSlot> INGREDIENTS_SLOT_RANGE = new SlotRange<>(new RecipePanelSlot(1, 1), new RecipePanelSlot(3, 3));
    public static final RecipePanelSlot RESULT_SLOT = new RecipePanelSlot(5, 2);
    public static final RecipePanelSlot WORKBENCH_SLOT = new RecipePanelSlot(4, 2);

    private static final Material WORKBENCH_DISPLAY_MATERIAL = Material.CRAFTING_TABLE;

    public ShapedRecipePanel(@NotNull ShapedRecipe recipe) {
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
        return INGREDIENTS_SLOT_RANGE;
    }

    @Override
    protected @NotNull SlotSequence<RecipePanelSlot> getResults() {
        return new SlotSequence<RecipePanelSlot>(List.of(RESULT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<RecipePanelSlot> getConsumables() {
        return new SlotSequence<RecipePanelSlot>(List.of());
    }

    @Override
    @Nullable
    protected Material getWorkbenchMaterial() {
        return WORKBENCH_DISPLAY_MATERIAL;
    }

    @Override
    @Nullable
    protected RecipePanelSlot getWorkbenchSlot() {
        return WORKBENCH_SLOT;
    }

    @Override
    public void setRecipe(@NotNull ShapedRecipe recipe) {
        super.setRecipe(recipe);
    }

    @Override
    public void cycle(EnumSet<SlotType> slotTypes) {
        // TODO implement the cycle method
    }

    private void placeWorkbench() {
        GuiItem<RecipeMenu> noTooltipWorkbench = GuiItem.buildNoTooltipGuiItem(WORKBENCH_DISPLAY_MATERIAL);
        recipeViewSlots.put(WORKBENCH_SLOT, noTooltipWorkbench);
    }

    @Override
    protected void populateCraftingSlots() {
        
        clear();
        RecipeChoice[][] recipeMatrix = getRecipe3by3Matrix(getRecipe());

        // Inputs (crafting grid) // TODO replace with the slotSequence foreach
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                RecipeChoice recipeChoice = recipeMatrix[y][x];
                if(recipeChoice == null) {
                    continue;
                }
                if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    recipeViewSlots.put(new RecipePanelSlot(x+1,y+1), new GuiItem<RecipeMenu>(materialChoice.getItemStack()));
                }
                else {
                    // TODO remove the use a deprecated method for generating the warning item
                    new GuiItemService().CreateWarningItem("Conversion of the RecipeChoice type to %s is not supported".formatted(recipeChoice.getClass().getName()));
                }
            }
        }

        // Result
        recipeViewSlots.put(RESULT_SLOT, new GuiItem<>(getRecipe().getResult()));
    }

    /**
     * Get the recipe crafting grid matrix from the shaped recipe
     * ShapedRecipe choiceMap are not always 3x3, so we need to make some manipulation to correctly represent it in the GUI
     * @param shapedRecipe The shaped recipe to get the matrix from
     * @return A centered representation of the crafting grid (RecipeChoice[3][3])
     */
    private static RecipeChoice[][] getRecipe3by3Matrix(ShapedRecipe shapedRecipe) {
        RecipeChoice[][] recipeMatrix = new RecipeChoice[3][3];
        
        int recipeWidth = shapedRecipe.getShape()[0].length();
        int recipeHeight = shapedRecipe.getShape().length;
        RecipeChoice[] itemArray = shapedRecipe.getChoiceMap().values().toArray(new RecipeChoice[0]);

        int recipeIndex = 0; // index of the recipe array
        int craftingIndex = 0; // index of the crafting grid

        // If the recipe is 1 large, center horizontally the recipe in the crafting grid (eg: sword)
        if(recipeWidth == 1)
            craftingIndex++;

        // If the recipe is 1 tall, center vertically the recipe in the crafting grid (eg: slab)
        if(recipeHeight == 1)
            craftingIndex += 3;

        for(int i = 0; i < recipeHeight; i++) {
            for(int j = 0; j < recipeWidth; j++) {
                RecipeChoice recipeChoice = itemArray[recipeIndex];
                if(recipeChoice == null) {
                    craftingIndex++;
                    recipeIndex++;
                    continue;
                }
                recipeMatrix[craftingIndex / 3][craftingIndex % 3] = recipeChoice;
                craftingIndex++;
                recipeIndex++;
            }
            craftingIndex += 3 - recipeWidth; // got to the next crafting row
        }

        return recipeMatrix;
    }
}

