package me.qheilmann.vei.Core.RecipeView;

import java.util.EnumSet;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.Slot.Collection.SlotRange;
import me.qheilmann.vei.foundation.gui.GuiItemService;
import net.kyori.adventure.text.Component;

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

    public static final SlotRange<RecipeViewSlot> INGREDIENTS_SLOT_RANGE = new SlotRange<>(new RecipeViewSlot(1, 1), new RecipeViewSlot(3, 3));
    public static final RecipeViewSlot RESULT_COORDS = new RecipeViewSlot(5, 2);
    public static final RecipeViewSlot WORKBENCH_COORDS = new RecipeViewSlot(4, 2);

    private ShapedRecipe shapedRecipe;
    private static final Material WORKBENCH_DISPLAY_MATERIAL = Material.CRAFTING_TABLE;

    public ShapedRecipeView(@NotNull ShapedRecipe recipe) {
        super(recipe);
        
        // TODO TEMPORARY here we have double recipe and shapedRecipe (protected recipe and always cast (mprivate methode))
        shapedRecipe = recipe;
        placeWorkbench();
        reloadView();
    }

    @Override
    public void setRecipe(@NotNull ShapedRecipe recipe) {
        super.setRecipe(recipe); // TODO recipe and shapedRecipe doublon
        shapedRecipe = recipe;
    }

    private void reloadView() {
        
        clear();
        RecipeChoice[][] recipeMatrix = getRecipe3by3Matrix(shapedRecipe);

        // Inputs (crafting grid)
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                RecipeChoice recipeChoice = recipeMatrix[y][x];
                if(recipeChoice == null) {
                    continue;
                }
                if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    recipeViewSlots.put(new RecipeViewSlot(x+1,y+1), new GuiItem<RecipeMenu>(materialChoice.getItemStack()));
                }
                else {
                    // TODO remove the use a deprecated method for generating the warning item
                    new GuiItemService().CreateWarningItem("Conversion of the RecipeChoice type to %s is not supported".formatted(recipeChoice.getClass().getName()));
                }
            }
        }

        // Result
        recipeViewSlots.put(new RecipeViewSlot(19), new GuiItem<>(shapedRecipe.getResult()));
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

    @Override // TODO split this to each slot type and push this to super class (derived class can override each slot type)
    public void clear(EnumSet<SlotType> slotTypes) {

        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (RecipeViewSlot slot : INGREDIENTS_SLOT_RANGE) {
                recipeViewSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        // No consumables in shaped recipes

        if (slotTypes.contains(SlotType.RESULTS)) {
            recipeViewSlots.put(RESULT_COORDS, new GuiItem<>(ItemStack.empty()));
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            recipeViewSlots.put(WORKBENCH_COORDS, new GuiItem<>(ItemStack.empty()));
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            recipeViewSlots.put(NEXT_RECIPE_SLOT, new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(PREVIOUS_RECIPE_SLOT, new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(FORWARD_RECIPE_SLOT, new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(BACKWARD_RECIPE_SLOT, new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(MOVE_INGREDIENTS_SLOT, new GuiItem<>(ItemStack.empty()));
        }

        // No other slots in shaped recipes (except padding) // TODO what about padding?
    }

    @Override
    public void cycle(EnumSet<SlotType> slotTypes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cycle'");
    }

    @Override
    public HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> getContentView(EnumSet<SlotType> slotTypes) {
        HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> contentView = new HashMap<>();
        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (RecipeViewSlot slot : INGREDIENTS_SLOT_RANGE) {
                contentView.put(slot, recipeViewSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.RESULTS)) {
            contentView.put(RESULT_COORDS, recipeViewSlots.get(RESULT_COORDS));
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            contentView.put(WORKBENCH_COORDS, recipeViewSlots.get(WORKBENCH_COORDS));
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            contentView.put(NEXT_RECIPE_SLOT, recipeViewSlots.get(NEXT_RECIPE_SLOT));
            contentView.put(PREVIOUS_RECIPE_SLOT, recipeViewSlots.get(PREVIOUS_RECIPE_SLOT));
            contentView.put(FORWARD_RECIPE_SLOT, recipeViewSlots.get(FORWARD_RECIPE_SLOT));
            contentView.put(BACKWARD_RECIPE_SLOT, recipeViewSlots.get(BACKWARD_RECIPE_SLOT));
            contentView.put(MOVE_INGREDIENTS_SLOT, recipeViewSlots.get(MOVE_INGREDIENTS_SLOT));
        }

        return contentView;
    }

    @Override
    public void attachMenuButton(ButtonType buttonType, GuiItem<RecipeMenu> parentButton) {
        switch (buttonType) {
            case NEXT_RECIPE:
                recipeViewSlots.put(NEXT_RECIPE_SLOT, parentButton);
                break;
            case PREVIOUS_RECIPE:
                recipeViewSlots.put(PREVIOUS_RECIPE_SLOT, parentButton);
                break;
            case BACKWARD_RECIPE:
                recipeViewSlots.put(BACKWARD_RECIPE_SLOT, parentButton);
                break;
            case FORWARD_RECIPE:
                recipeViewSlots.put(FORWARD_RECIPE_SLOT, parentButton);
                break;
            case MOVE_INGREDIENTS:
                recipeViewSlots.put(MOVE_INGREDIENTS_SLOT, parentButton);
                break;
            default:
                throw new IllegalArgumentException("Unknown button type: " + buttonType);
        }
    }

    private void placeWorkbench() {
        GuiItem<RecipeMenu> workbenchDisplayItem = new GuiItem<>(WORKBENCH_DISPLAY_MATERIAL);
        ItemMeta meta = workbenchDisplayItem.getItemMeta();
        meta.displayName(Component.empty());
        meta.setMaxStackSize(1);
        meta.setHideTooltip(true);
        workbenchDisplayItem.setItemMeta(meta);
        recipeViewSlots.put(WORKBENCH_COORDS, workbenchDisplayItem);
    }
}

