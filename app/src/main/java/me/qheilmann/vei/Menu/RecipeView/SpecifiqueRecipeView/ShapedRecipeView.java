package me.qheilmann.vei.Menu.RecipeView.SpecifiqueRecipeView;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Menu.RecipeView.IRecipeView;
import me.qheilmann.vei.Menu.RecipeView.RecipeViewContainer;
import me.qheilmann.vei.Menu.RecipeView.ViewSlot.IngredientViewSlot;
import me.qheilmann.vei.Menu.RecipeView.ViewSlot.StaticViewSlot;
import me.qheilmann.vei.foundation.gui.ActionType;
import me.qheilmann.vei.foundation.gui.GuiItemService;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

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
public class ShapedRecipeView implements IRecipeView<ShapedRecipe> {

    public static final Vector2i INPUTS_ORGIGIN_COORDS = new Vector2i(1, 1);
    public static final Vector2i OUTPUTS_COORDS = new Vector2i(5, 2);
    public static final Vector2i WORKBENCH_COORDS = new Vector2i(4, 2);
    public static final Vector2i NEXT_RECIPE_COORDS = new Vector2i(3, 0);
    public static final Vector2i PREVIOUS_RECIPE_COORDS = new Vector2i(1, 0);
    public static final Vector2i BACK_RECIPE_COORDS = new Vector2i(1, 4);
    public static final Vector2i FORWARD_RECIPE_COORDS = new Vector2i(3, 4);
    public static final Vector2i MOVE_INGREDIENTS_COORDS = new Vector2i(5, 3);

    private RecipeViewContainer recipeViewContainer;
    private ShapedRecipe shapedRecipe;
    private boolean hasRecipeChanged = true;
    private final GuiItemService guiItemService;

    public ShapedRecipeView(@NotNull GuiItemService guiItemService, @NotNull ShapedRecipe recipe) {
        Preconditions.checkNotNull(guiItemService, "GuiItemService cannot be null");
        Preconditions.checkNotNull(recipe, "Recipe cannot be null");
        
        this.guiItemService = guiItemService;
        recipeViewContainer = new RecipeViewContainer();
        initInventory();
        setRecipe(recipe);
    }

    @Override
    public void setRecipe(@NotNull ShapedRecipe recipe) {
        shapedRecipe = recipe;
        hasRecipeChanged = true;
    }

    @Override
    public void clearRecipe() {

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                recipeViewContainer.setViewSlot(new StaticViewSlot(INPUTS_ORGIGIN_COORDS.add(x, y, new Vector2i()), new ItemStack(Material.AIR)));
            }
        }

        recipeViewContainer.setViewSlot(new StaticViewSlot(OUTPUTS_COORDS, new ItemStack(Material.AIR)));
    }

    @Override
    public void refreshView() {
        if(!hasRecipeChanged) {
            return;
        }
        
        reloadView();
    }

    @Override
    public RecipeViewContainer getRecipeContainer() {
        refreshView();
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
        VeiStyle style = VeiStyle.LIGHT;
        recipeViewContainer.setViewSlot(new StaticViewSlot(NEXT_RECIPE_COORDS       , guiItemService.CreateActionItem(ActionType.NEXT_RECIPE, style)));
        recipeViewContainer.setViewSlot(new StaticViewSlot(PREVIOUS_RECIPE_COORDS   , guiItemService.CreateActionItem(ActionType.PREVIOUS_RECIPE, style)));
        recipeViewContainer.setViewSlot(new StaticViewSlot(BACK_RECIPE_COORDS       , guiItemService.CreateActionItem(ActionType.BACK_RECIPE, style)));
        recipeViewContainer.setViewSlot(new StaticViewSlot(FORWARD_RECIPE_COORDS    , guiItemService.CreateActionItem(ActionType.FORWARD_RECIPE, style)));
        recipeViewContainer.setViewSlot(new StaticViewSlot(MOVE_INGREDIENTS_COORDS  , guiItemService.CreateActionItem(ActionType.MOVE_INGREDIENTS, style)));

        recipeViewContainer.setViewSlot(new StaticViewSlot(WORKBENCH_COORDS, new ItemStack(Material.CRAFTING_TABLE)));
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
                    recipeViewContainer.setViewSlot(new IngredientViewSlot(INPUTS_ORGIGIN_COORDS.add(x, y, new Vector2i()), materialChoice));
                }
                else {
                    ItemStack warningItem = new ItemStack(Material.BARRIER);
                    warningItem.editMeta(meta -> meta.displayName(Component.text("Warning: Conversion of the RecipeChoice type to " + recipeChoice.getClass().getName() + " is not supported", TextColor.color(255, 0, 0)))); // TODO add Gui Item Error (with args comment)
                    recipeViewContainer.setViewSlot(new StaticViewSlot(INPUTS_ORGIGIN_COORDS.add(x, y, new Vector2i()), warningItem));
                }
            }
        }

        // Result
        recipeViewContainer.setViewSlot(new StaticViewSlot(OUTPUTS_COORDS, shapedRecipe.getResult()));

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
}
