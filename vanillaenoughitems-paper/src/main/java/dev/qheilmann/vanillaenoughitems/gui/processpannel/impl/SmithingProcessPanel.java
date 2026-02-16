package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiComponent;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.TrimMaterialHelper;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.PanelStaticItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;

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

    private final CyclicIngredient baseCyclic;
    private final CyclicIngredient additionCyclic;

     /**
     * Create a new SmithingProcessPanel.
     * @param recipe the smithing recipe
     * @param style the style to use
     */

    public SmithingProcessPanel(Recipe recipe, Style style) {
        this.recipe = recipe;
        this.style = style;
        this.seed = new Random().nextInt();

        // Cyclic ingredients shared between the caller and the dynamic result
        baseCyclic = new CyclicIngredient(seed, getSmithingRecipe().getBase());
        additionCyclic = new CyclicIngredient(seed, getSmithingRecipe().getAddition());
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
        ticked.put(BASE_SLOT, baseCyclic);
        ticked.put(ADDITION_SLOT, additionCyclic);
        return Map.copyOf(ticked);
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    @SuppressWarnings("null")
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return Map.of(OUTPUT_SLOT, getResult(seed, getSmithingRecipe(), baseCyclic, additionCyclic));
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
        
        ItemStack backgroundItem = RecipeGuiComponent.createFillerItem(false);
        ItemStack smithingItem = ItemType.SMITHING_TABLE.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (style.hasResourcePack()) {
            backgroundItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Background.Panel.SMITHING));
            smithingItem.editMeta(meta -> meta.setItemModel(VeiPack.ItemModel.Gui.Decoration.RECIPE_ARROW_SMALL));
        }

        statics.put(BACKGROUND_SLOT, new PanelStaticItem(backgroundItem, null));
        statics.put(DECORATION_SMITHING_SLOT, new PanelStaticItem(smithingItem, null));
        
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

    private static CyclicIngredient getResult(int seed, SmithingRecipe recipe, CyclicIngredient baseCyclic, CyclicIngredient additionCyclic) {
        
        // SmithingTrimRecipe is a complex recipe and returns air by default
        if (recipe instanceof SmithingTrimRecipe trimRecipe) {
            return getTrimResult(trimRecipe, baseCyclic, additionCyclic);
        }

        return new CyclicIngredient(seed, recipe.getResult());
    }

    /**
     * Get the result for a SmithingTrimRecipe.
     * As SmithingTrimRecipe#getResult() returns air, we need to create do some calculation from base and addition ingredients.
     * @param recipe the SmithingTrimRecipe (for TrimPattern)
     * @param baseCyclic the base ingredient
     * @param additionCyclic the addition ingredient
     * @return a dynamic result CyclicIngredient
     */
    private static CyclicIngredient getTrimResult(SmithingTrimRecipe recipe, CyclicIngredient baseCyclic, CyclicIngredient additionCyclic) {
        
        TrimPattern trimPattern = recipe.getTrimPattern();
        
        // Create a dependent CyclicIngredient that computes the result from the inputs
        return new CyclicIngredient((ItemStack... dependencyItems) -> {
            // dependencyItems[0] = base
            // dependencyItems[1] = addition
            
            ItemStack base = dependencyItems[0].clone();
            ItemStack addition = dependencyItems[1];
            
            TrimMaterial trimMaterial = TrimMaterialHelper.byMaterialName(addition.getType());
            ArmorTrim armorTrim = new ArmorTrim(trimMaterial, trimPattern);

            base.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(armorTrim));

            return base;
        }, baseCyclic, additionCyclic);
    }
}
