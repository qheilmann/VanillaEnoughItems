package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiComponent;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

/**
 * Panel for all smithing recipes.
 */
@NullMarked
public class SmithingProcessPanel implements ProcessPanel {
    private static final ProcessPannelSlot TEMPLATE_SLOT = new ProcessPannelSlot(1, 2);
    private static final ProcessPannelSlot BASE_SLOT = new ProcessPannelSlot(2, 2);
    private static final ProcessPannelSlot ADDITION_SLOT = new ProcessPannelSlot(3, 2);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot DECORATION_SMITHING_SLOT = new ProcessPannelSlot(4, 2);
    private static final ProcessPannelSlot BACKGROUND_SLOT = new ProcessPannelSlot(0, 0);

    private final Recipe recipe;
    private final Style style;
    private final int seed;

    public SmithingProcessPanel(Recipe recipe, Style style) {
        this.recipe = recipe;
        this.style = style;
        this.seed = (int) (Math.random() * Integer.MAX_VALUE);
    }

    private SmithingRecipe getSmithingRecipe() {
        return (SmithingRecipe) recipe;
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
        ticked.put(TEMPLATE_SLOT, new CyclicIngredient(seed, getTemplateChoice(getSmithingRecipe())));
        ticked.put(BASE_SLOT, new CyclicIngredient(seed, getSmithingRecipe().getBase()));
        ticked.put(ADDITION_SLOT, new CyclicIngredient(seed, getSmithingRecipe().getAddition()));
        return Map.copyOf(ticked);
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    @SuppressWarnings("null")
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(seed, getSmithingRecipe().getResult()));
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
    public Map<ProcessPannelSlot, FastInvItem> getStaticItems() {
        Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();
        
        ItemStack backgroundItem = RecipeGuiComponent.createFillerItem(false);
        ItemStack smithingItem = ItemType.SMITHING_TABLE.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (style.hasResourcePack()) {
            backgroundItem.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Background.Panel.SMITHING);
            });
            smithingItem.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Decoration.RECIPE_ARROW_SMALL);
            });
        }

        statics.put(BACKGROUND_SLOT, new FastInvItem(backgroundItem, null));
        statics.put(DECORATION_SMITHING_SLOT, new FastInvItem(smithingItem, null));
        
        return Map.copyOf(statics);
    }

    private static RecipeChoice getTemplateChoice(SmithingRecipe recipe) {
        if (recipe instanceof SmithingTransformRecipe transformRecipe) {
            return transformRecipe.getTemplate();
        } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
            return trimRecipe.getTemplate();
        }

        throw new IllegalArgumentException("Unsupported SmithingRecipe subtype: " + recipe.getClass().getName());
    }
}
