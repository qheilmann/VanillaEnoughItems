package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

/**
 * Panel for all smithing recipes.
 */
@NullMarked
public class SmithingProcessPanel extends AbstractProcessPanel {
    private static final ProcessPannelSlot TEMPLATE_SLOT = new ProcessPannelSlot(1, 2);
    private static final ProcessPannelSlot BASE_SLOT = new ProcessPannelSlot(2, 2);
    private static final ProcessPannelSlot ADDITION_SLOT = new ProcessPannelSlot(3, 2);
    private static final ProcessPannelSlot DECORATION_FIRE_SLOT = new ProcessPannelSlot(4, 2);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);

    public SmithingProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        super(recipe, actions, context);
    }

    private SmithingRecipe getSmithingRecipe() {
        return (SmithingRecipe) recipe;
    }

    @Override
    protected Map<RecipeGuiSharedButton, ProcessPannelSlot> buildRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();
    }

    @Override
    protected Map<ProcessPannelSlot, CyclicIngredient> buildTickedIngredient() {
        Map<ProcessPannelSlot, CyclicIngredient> ticked = new HashMap<>();
        ticked.put(TEMPLATE_SLOT, new CyclicIngredient(getTemplateChoice(getSmithingRecipe())));
        ticked.put(BASE_SLOT, new CyclicIngredient(getSmithingRecipe().getBase()));
        ticked.put(ADDITION_SLOT, new CyclicIngredient(getSmithingRecipe().getAddition()));
        return Map.copyOf(ticked);
    }

    @Override
    @SuppressWarnings("null")
    protected Map<ProcessPannelSlot, CyclicIngredient> buildTickedResult() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(getSmithingRecipe().getResult()));
    }

    @Override
    protected Map<ProcessPannelSlot, FastInvItem> buildStaticItems() {
        Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();
        statics.put(DECORATION_FIRE_SLOT, new FastInvItem(new ItemStack(Material.STONECUTTER), null));
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
