package me.qheilmann.vei.Core.ProcessPanel.Panels;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanelSlot;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Core.Slot.Collection.SlotRange;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.Core.Style.Styles.Style;
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
public class CraftingProcessPanel extends ProcessPanel<CraftingRecipe> {

    public static final ProcessPanelSlot NEXT_RECIPE_SLOT = new ProcessPanelSlot(3 , 0);
    public static final ProcessPanelSlot PREVIOUS_RECIPE_SLOT = new ProcessPanelSlot(1, 0);
    public static final ProcessPanelSlot FORWARD_RECIPE_SLOT = new ProcessPanelSlot(3, 4);
    public static final ProcessPanelSlot BACKWARD_RECIPE_SLOT = new ProcessPanelSlot(1, 4);
    public static final ProcessPanelSlot MOVE_INGREDIENTS_SLOT = new ProcessPanelSlot(5, 3);

    public static final SlotRange<ProcessPanelSlot> INGREDIENTS_SLOT_RANGE = new SlotRange<>(new ProcessPanelSlot(1, 1), new ProcessPanelSlot(3, 3));
    public static final ProcessPanelSlot RESULT_SLOT = new ProcessPanelSlot(5, 2);
    public static final ProcessPanelSlot WORKBENCH_SLOT = new ProcessPanelSlot(4, 2);

    private static final Material WORKBENCH_DISPLAY_MATERIAL = Material.CRAFTING_TABLE;

    public CraftingProcessPanel(@NotNull Style style, @NotNull RecipeIndexService recipeIndex, @NotNull ProcessRecipeReader<CraftingRecipe> recipeReader) {
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
        return INGREDIENTS_SLOT_RANGE;
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getResultSlots() {
        return new SlotSequence<ProcessPanelSlot>(List.of(RESULT_SLOT));
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getConsumableSlots() {
        return new SlotSequence<ProcessPanelSlot>(List.of());
    }

    @Override
    @Nullable
    protected Material getWorkbenchMaterial() {
        return WORKBENCH_DISPLAY_MATERIAL;
    }

    @Override
    @Nullable
    protected ProcessPanelSlot getWorkbenchSlot() {
        return WORKBENCH_SLOT;
    }

    @Override
    public void cycle(EnumSet<SlotType> slotTypes) {
        // TODO implement the cycle method
        VanillaEnoughItems.LOGGER.info("Simulated cycle method called");
    }

    private void placeWorkbench() {
        GuiItem<RecipeMenu> noTooltipWorkbench = GuiItem.buildNoTooltipGuiItem(WORKBENCH_DISPLAY_MATERIAL);
        recipePanelSlots.put(WORKBENCH_SLOT, noTooltipWorkbench);
    }

    @Override
    public void render(EnumSet<AttachedButtonType> buttonsVisibility) {
        super.render(buttonsVisibility);

        placeWorkbench();

        CraftingRecipe currentRecipe = getCurrentRecipe();
        if (currentRecipe instanceof ShapedRecipe shapedRecipe) {
            populateShapedRecipeCraftingsSlot(shapedRecipe);
        } else if (currentRecipe instanceof ShapelessRecipe shapelessRecipe) {
            populateShapelessRecipeCraftingsSlot(shapelessRecipe);
        }

        // Result
        recipePanelSlots.put(RESULT_SLOT, new GuiItem<>(currentRecipe.getResult()));
    }

    private void populateShapedRecipeCraftingsSlot(ShapedRecipe shapedRecipe) {
        RecipeChoice[][] recipeMatrix = getRecipe3by3Matrix(shapedRecipe);

        // Inputs (crafting grid) // TODO replace with the slotSequence foreach
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                RecipeChoice recipeChoice = recipeMatrix[y][x];
                if(recipeChoice == null) {
                    continue;
                }
                if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    GuiItem<RecipeMenu> recipeGuiItem = buildNewRecipeGuiItem(materialChoice.getItemStack());
                    recipePanelSlots.put(new ProcessPanelSlot(x+1,y+1), recipeGuiItem); // +1 +1 is because of the crafting grid offset
                }
                else {
                    // TODO remove the use a deprecated method for generating the warning item
                    new GuiItemService().CreateWarningItem("Conversion of the RecipeChoice type to %s is not supported".formatted(recipeChoice.getClass().getName()));
                }
            }
        }
    }

    private void populateShapelessRecipeCraftingsSlot(ShapelessRecipe shapelessRecipe) {
        List<RecipeChoice> recipeChoices = shapelessRecipe.getChoiceList();
        int x = 0;
        int y = 0;

        // If the recipe can be made inside made in a 2x2 crafting grid, we will display it in a 2x2 grid
        // so the player can better understand he can make it direclty in is inventory crafting grid
        boolean isMax2Col = false;
        if (recipeChoices.size() <= 4) {
            isMax2Col = true;
        }

        // If the recipe a single item, we will center it in the crafting grid
        if (recipeChoices.size() == 1) {
            x = 1;
            y = 1;
        }

        // Crafting grid
        for(RecipeChoice recipeChoice : recipeChoices) {
            if(recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
                GuiItem<RecipeMenu> recipeGuiItem = buildNewRecipeGuiItem(materialChoice.getItemStack());
                recipePanelSlots.put(new ProcessPanelSlot(x+1, y+1), recipeGuiItem); // +1 +1 is because of the crafting grid offset
            }
            else {
                // TODO remove the use a deprecated method for generating the warning item
                new GuiItemService().CreateWarningItem("Conversion of the RecipeChoice type to %s is not supported".formatted(recipeChoice.getClass().getName()));
            }
            x++;
            if((!isMax2Col && x > 2) || (isMax2Col && x > 1)) {
                x = 0;
                y++;
            }
        }
    }

    /**
     * Get the recipe crafting grid matrix from the shaped recipe
     * ShapedRecipe choiceMap are not always 3x3, so we need to make some manipulation to correctly represent it in the GUI
     * @param shapedRecipe The shaped recipe to get the matrix from
     * @return A centered representation of the crafting grid (RecipeChoice[3][3])
     */
    private static RecipeChoice[][] getRecipe3by3Matrix(ShapedRecipe shapedRecipe) {
        RecipeChoice[][] recipeMatrix = new RecipeChoice[3][3];
        RecipeChoice[] itemArray = shapedRecipe.getChoiceMap().values().toArray(new RecipeChoice[0]);
        
        int recipeWidth = shapedRecipe.getShape()[0].length();
        int recipeHeight = shapedRecipe.getShape().length;

        // max, min item position in the crafting grid (for alignment)
        int maxGridX = 2;
        int maxGridY = 2;
        int minGridX = 0;
        int minGridY = 0;

        // Horizontal alignment
        // If the recipe is 1 large, center horizontally the recipe in the crafting grid (eg: sword)
        if(recipeWidth == 1) {
            minGridX = 1;
            maxGridX = 1;
        }
        // If the recipe is 2 large, aligne the recipe to the left of the crafting grid (eg: iron_door)
        else if (recipeWidth == 2) {
            minGridX = 0;
            maxGridX = 1;
        }

        // Vertical alignment
        // If the recipe is 1 tall, center vertically the recipe in the crafting grid (eg: slab)
        if (recipeHeight == 1) {
            minGridY = 1;
            maxGridY = 1;
        }
        // If the recipe is 2 tall and 3 large, aligne the recipe to the bottom of the crafting grid (eg: repeater)
        else if (recipeHeight == 2 && recipeWidth == 3) {
            minGridY = 1;
            maxGridY = 2;
        }
        // If the recipe is 2 tall and 2 or 1 large, aligne the recipe to the top left of the crafting grid (eg: glowstone or stick)
        // So the player can better understand he can make it direclty in is inventory crafting grid
        else if (recipeHeight == 2 && recipeWidth < 3) {
            minGridY = 0;
            maxGridY = 1;
        }

        // Formating the recipeMatrix
        int recipeIndex = 0; 
        for(int gridY = minGridY; gridY <= maxGridY; gridY++) {
            for(int gridX = minGridX; gridX <= maxGridX; gridX++) {
                RecipeChoice recipeChoice = itemArray[recipeIndex++];
                if(recipeChoice == null) continue; // Recipe without a rectangular shape like stairs containe null recipeChoice
                recipeMatrix[gridY][gridX] = recipeChoice;
            }
        }

        return recipeMatrix;
    }
}

