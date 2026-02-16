package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiComponent;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.PanelStaticItem;

/**
 * Panel for furnace smelting recipes.
 */
@NullMarked
public class SmeltingProcessPanel implements ProcessPanel {
    private static final ProcessPannelSlot INPUT_SLOT = new ProcessPannelSlot(1, 1);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot FUEL_SLOT = new ProcessPannelSlot(1, 3);
    private static final ProcessPannelSlot DECORATION_FIRE_SLOT = new ProcessPannelSlot(1, 2);
    private static final ProcessPannelSlot DECORATION_PROGRESS_SLOT = new ProcessPannelSlot(3, 2);
    private static final ProcessPannelSlot BACKGROUND_SLOT = new ProcessPannelSlot(0, 0);

    private final Recipe recipe;
    private final Style style;
    private final int seed;

    public SmeltingProcessPanel(Recipe recipe, Style style) {
        this.recipe = recipe;
        this.style = style;
        this.seed = new Random().nextInt();
    }

    private FurnaceRecipe getFurnaceRecipe() {
        return (FurnaceRecipe) recipe;
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
        ticked.put(INPUT_SLOT, new CyclicIngredient(seed, getFurnaceRecipe().getInputChoice()));
        return Map.copyOf(ticked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("null")
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(seed, getFurnaceRecipe().getResult()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedOther() {
        Map<ProcessPannelSlot, CyclicIngredient> ticked = new HashMap<>();
        ticked.put(FUEL_SLOT, new CyclicIngredient(seed, Fuels.FUELS.toArray(new ItemStack[0])));
        return Map.copyOf(ticked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, PanelStaticItem> getStaticItems() {
        Map<ProcessPannelSlot, PanelStaticItem> statics = new HashMap<>();

        ItemStack backgroundItem = RecipeGuiComponent.createFillerItem(false);
        ItemStack progressItem = RecipeGuiComponent.createFillerItem(true);
        ItemStack furnaceItem = ItemType.FURNACE.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (style.hasResourcePack()) {
            backgroundItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Background.Panel.SMELTING));
            progressItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Decoration.RECIPE_PROGRESS));
            furnaceItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Decoration.COOKING_FLAME));
        }

        statics.put(BACKGROUND_SLOT, new PanelStaticItem(backgroundItem, null));
        statics.put(DECORATION_PROGRESS_SLOT, new PanelStaticItem(progressItem, null));
        statics.put(DECORATION_FIRE_SLOT, new PanelStaticItem(furnaceItem, null));
        
        return Map.copyOf(statics);
    }
}
