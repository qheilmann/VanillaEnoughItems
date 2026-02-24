package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.config.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiComponent;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.PanelStaticItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Panel for all crafting recipes.
 */
@NullMarked
public class CraftingProcessPanel implements ProcessPanel {

    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    public static final ProcessPannelSlot DECORATION_CRAFTING_TABLE_SLOT = new ProcessPannelSlot(4, 2);
    public static final ProcessPannelSlot BACKGROUND_SLOT = new ProcessPannelSlot(0, 0);
    public static final ProcessPannelSlot SHAPELESS_INDICATOR_SLOT = new ProcessPannelSlot(5, 1);
    private static final ProcessPannelSlot[][] CRAFTING_GRID_SLOTS = {
        { new ProcessPannelSlot(1, 1), new ProcessPannelSlot(2, 1), new ProcessPannelSlot(3, 1) },
        { new ProcessPannelSlot(1, 2), new ProcessPannelSlot(2, 2), new ProcessPannelSlot(3, 2) },
        { new ProcessPannelSlot(1, 3), new ProcessPannelSlot(2, 3), new ProcessPannelSlot(3, 3) }
    };

    private final Recipe recipe;
    private final Style style;
    private final int seed;

    public CraftingProcessPanel(Recipe recipe, Style style) {
        this.recipe = recipe;
        this.style = style;
        this.seed = new Random().nextInt();
    }

    public CraftingRecipe getCraftingRecipe() {
        return (CraftingRecipe) recipe;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient() {
        Map<ProcessPannelSlot, CyclicIngredient> ticked = new HashMap<>();
        ticked.putAll(mapRecipeMatrixToSlots(getRecipeMatrix(getCraftingRecipe()), seed));
        return Map.copyOf(ticked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(seed, getCraftingRecipe().getResult()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedOther() {
        return Map.of();
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Map<ProcessPannelSlot, PanelStaticItem> getStaticItems() {
        Map<ProcessPannelSlot, PanelStaticItem> statics = new HashMap<>();
        
        ItemStack isShapelessIndicatorItem = createIsShapelessIndicatorItem();
        ItemStack backgroundItem = RecipeGuiComponent.createFillerItem(false);
        ItemStack craftingTableItem = ItemType.CRAFTING_TABLE.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });
        
        if (style.hasResourcePack()) {
            backgroundItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Background.Panel.CRAFTING));
            craftingTableItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Decoration.RECIPE_ARROW_SMALL));
        }

        statics.put(BACKGROUND_SLOT, new PanelStaticItem(backgroundItem, null));
        statics.put(DECORATION_CRAFTING_TABLE_SLOT, new PanelStaticItem(craftingTableItem, null));
        statics.put(SHAPELESS_INDICATOR_SLOT, new PanelStaticItem(isShapelessIndicatorItem, null));
        return statics;
    }

    // Helpers

    private static Map<ProcessPannelSlot, CyclicIngredient> mapRecipeMatrixToSlots(RecipeChoice[][] recipeMatrix, int seed) {
        Map<ProcessPannelSlot, CyclicIngredient> mapped = new HashMap<>();

        for (int y = 0; y < recipeMatrix.length; y++) {
            for (int x = 0; x < recipeMatrix[y].length; x++) {
                RecipeChoice choice = recipeMatrix[y][x];
                if (choice != null) {
                    mapped.put(CRAFTING_GRID_SLOTS[y][x], new CyclicIngredient(seed, choice));
                }
            }
        }

        return mapped;
    }

    /**
     * Get the recipe crafting grid matrix from the crafting recipe
     * @param craftingRecipe The crafting recipe to get the matrix from
     * @return A representation of the crafting grid (RecipeChoice[3][3])
     */
    private static RecipeChoice[][] getRecipeMatrix(CraftingRecipe craftingRecipe) {
        return switch (craftingRecipe) {
          case ShapedRecipe shapedRecipe -> getRecipe3by3MatrixShaped(shapedRecipe);
          case ShapelessRecipe shapelessRecipe -> getRecipeMatrixShapeless(shapelessRecipe);
          case TransmuteRecipe transmuteRecipe -> getRecipeMatrixTransmute(transmuteRecipe);
          default -> throw new IllegalArgumentException("Unsupported CraftingRecipe type: " + craftingRecipe.getClass().getName());
        };
    }

    /**
     * Get the recipe crafting grid matrix from the transmute recipe
     * @param transmuteRecipe The transmute recipe to get the matrix from
     * @return A representation of the crafting grid (RecipeChoice[3][3])
     */
    private static RecipeChoice[][] getRecipeMatrixTransmute(TransmuteRecipe transmuteRecipe) {
        RecipeChoice[][] recipeMatrix = new RecipeChoice[3][3];
        recipeMatrix[1][1] = transmuteRecipe.getInput();
        recipeMatrix[1][2] = transmuteRecipe.getMaterial();
        return recipeMatrix;
    }

    /**
     * Get the recipe crafting grid matrix from the shapeless recipe
     * @param shapelessRecipe The shapeless recipe to get the matrix from
     * @return A representation of the crafting grid (RecipeChoice[3][3])
     */
    private static RecipeChoice[][] getRecipeMatrixShapeless(ShapelessRecipe shapelessRecipe) {
        RecipeChoice[][] recipeMatrix = new RecipeChoice[3][3];
        List<RecipeChoice> recipeChoices = shapelessRecipe.getChoiceList();

        // max, min item position in the crafting grid (for alignment)
        int maxGridX = 2;
        int maxGridY = 2;
        int minGridX = 0;
        int minGridY = 0;

        // If the recipe can be made inside made in a 2x2 crafting grid, we will display it in a 2x2 grid
        // so the player can better understand he can make it direclty in is inventory crafting grid
        if (recipeChoices.size() <= 4) {
            maxGridX = 1;
        }

        // If the recipe a single item, we will center it in the crafting grid
        if (recipeChoices.size() == 1) {
            minGridX = 1;
            maxGridX = 1;
            minGridY = 1;
            maxGridY = 1;
        }

        // Formating the recipeMatrix
        int recipeIndex = 0; 
        for(int gridY = minGridY; gridY <= maxGridY; gridY++) {
            for(int gridX = minGridX; gridX <= maxGridX; gridX++) {
                if(recipeIndex >= recipeChoices.size()) break; // When recipe have less items than the grid size (e.g. fire charge)
                RecipeChoice recipeChoice = recipeChoices.get(recipeIndex++);
                if(recipeChoice != null) {
                    recipeMatrix[gridY][gridX] = recipeChoice;
                }
            }
        }

        return recipeMatrix;
    }

    /**
     * Get the recipe crafting grid matrix from the shaped recipe
     * ShapedRecipe choiceMap are not always 3x3, so we need to make some manipulation to correctly represent it in the GUI
     * (center, align left/top, etc.)
     * @param shapedRecipe The shaped recipe to get the matrix from
     * @return A centered representation of the crafting grid (RecipeChoice[3][3])
     */
    private static RecipeChoice[][] getRecipe3by3MatrixShaped(ShapedRecipe shapedRecipe) {
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

    /**
     * Create the shapeless indicator item stack, or a filler item if the recipe is not shapeless
     * @return The shapeless indicator item stack
     */
    private ItemStack createIsShapelessIndicatorItem() {
        ItemStack shapelessIndicatorItem = RecipeGuiComponent.createFillerItem(style.hasResourcePack());
        if ((recipe instanceof ShapelessRecipe) ||
            (recipe instanceof TransmuteRecipe)
        ) {
            shapelessIndicatorItem = ItemType.LIGHT_GRAY_DYE.createItemStack();
            if (style.hasResourcePack()) {
                shapelessIndicatorItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Decoration.SHAPELESS_INDICATOR));
            }
        }

        shapelessIndicatorItem.editMeta(meta ->
            meta.displayName(Component.text("Shapeless Recipe", VanillaEnoughItems.veiConfig().style().colorPrimary()).decoration(TextDecoration.ITALIC, false))
        );

        return shapelessIndicatorItem;
    }
}
