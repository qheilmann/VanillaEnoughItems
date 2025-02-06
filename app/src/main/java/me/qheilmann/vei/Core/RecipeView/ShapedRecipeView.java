package me.qheilmann.vei.Core.RecipeView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Menu.RecipeView.RecipeViewContainer;
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

// TODO temporary implementation of the ShapelessRecipeView maybe not use of this container

public class ShapedRecipeView extends RecipeView<ShapedRecipe> {

    public static final RecipeViewSlot NEXT_RECIPE_SLOT = new RecipeViewSlot(3 , 0);
    public static final RecipeViewSlot PREVIOUS_RECIPE_SLOT = new RecipeViewSlot(1, 0);
    public static final RecipeViewSlot FORWARD_RECIPE_SLOT = new RecipeViewSlot(3, 4);
    public static final RecipeViewSlot BACKWARD_RECIPE_SLOT = new RecipeViewSlot(1, 4);
    public static final RecipeViewSlot MOVE_INGREDIENTS_SLOT = new RecipeViewSlot(5, 3);

    public static final RecipeViewSlot INPUTS_ORGIGIN_COORDS = new RecipeViewSlot(1, 1);
    public static final RecipeViewSlot OUTPUTS_COORDS = new RecipeViewSlot(5, 2);
    public static final RecipeViewSlot WORKBENCH_COORDS = new RecipeViewSlot(4, 2);

    private RecipeViewContainer recipeViewContainer;
    private ShapedRecipe shapedRecipe;
    private boolean hasRecipeChanged = true;

    public ShapedRecipeView(@NotNull ShapedRecipe recipe) {
        super(recipe);
        
        // TEMPS
        shapedRecipe = recipe;
        recipeViewContainer = new RecipeViewContainer();
        initInventory();
    }

    @Override
    public void setRecipe(@NotNull ShapedRecipe recipe) {
        super.setRecipe(recipe); // TODO recipe and shapedRecipe doublon
        shapedRecipe = recipe;
        hasRecipeChanged = true;
    }

    @Override
    public void clearRecipe() {

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                recipeViewContainer.setViewSlot(new RecipeViewSlot(7+1+x+y), new GuiItem<>(ItemStack.empty()));
            }
        }

        recipeViewContainer.setViewSlot(new RecipeViewSlot(19), new GuiItem<>(ItemStack.empty()));
    }

    @Override
    public void refresh() {
        if(!hasRecipeChanged) {
            return;
        }
        
        reloadView();
    }

    @Override
    public RecipeViewContainer getRecipeContainer() {
        VanillaEnoughItems.LOGGER.info("[LOG123]2]: " + shapedRecipe);
        refresh();
        return recipeViewContainer;
    }

    @Override
    public @NotNull Collection<ItemStack> getResults() {
        return Collections.singletonList(shapedRecipe.getResult());
    }

    @Override
    public @NotNull Collection<RecipeChoice> getIngredients() {
        return shapedRecipe.getChoiceMap().values();
    }

    private void initInventory() {
        // TODO
    }

    private void reloadView() {
        
        clearRecipe();
        RecipeChoice[][] recipeMatrix = getRecipe3by3Matrix(shapedRecipe);

        // Inputs (crafting grid)
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                RecipeChoice recipeChoice = recipeMatrix[y][x];
                if(recipeChoice == null) {
                    continue;
                }
                if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    recipeViewContainer.setViewSlot(new RecipeViewSlot(7+1+x+y), new GuiItem<RecipeMenu>(materialChoice.getItemStack()));
                }
                else {
                    new GuiItemService().CreateWarningItem("Conversion of the RecipeChoice type to %s is not supported".formatted(recipeChoice.getClass().getName()));
                }
            }
        }

        // Result
        recipeViewContainer.setViewSlot(new RecipeViewSlot(19), new GuiItem<>(shapedRecipe.getResult()));

        hasRecipeChanged = false; // reset the flag
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

    @Override
    public void clearRecipeSlot() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clearRecipeSlot'");
    }

    @Override
    public HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> getLastContent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLastContent'");
    }

    @Override
    public void cycle() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cycle'");
    }

    @Override
    public void attachMenuButton(RecipeViewButtonType buttonType, GuiItem<RecipeMenu> parentButton) {
        switch (buttonType) {
            case NEXT_RECIPE:
                recipeViewContainer.setViewSlot(NEXT_RECIPE_SLOT, parentButton);
                break;
            case PREVIOUS_RECIPE:
                recipeViewContainer.setViewSlot(PREVIOUS_RECIPE_SLOT, parentButton);
                break;
            case BACKWARD_RECIPE:
                recipeViewContainer.setViewSlot(BACKWARD_RECIPE_SLOT, parentButton);
                break;
            case FORWARD_RECIPE:
                recipeViewContainer.setViewSlot(FORWARD_RECIPE_SLOT, parentButton);
                break;
            case MOVE_INGREDIENTS:
                recipeViewContainer.setViewSlot(MOVE_INGREDIENTS_SLOT, parentButton);
                break;
            default:
                throw new IllegalArgumentException("Unknown button type: " + buttonType);
        }
    }
}

