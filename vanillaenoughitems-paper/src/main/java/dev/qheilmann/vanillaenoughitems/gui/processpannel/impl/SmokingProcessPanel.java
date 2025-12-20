package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmokingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

/**
 * Panel for smoking recipes.
 */
@NullMarked
public class SmokingProcessPanel extends AbstractProcessPanel {
    private static final ProcessPannelSlot INPUT_SLOT = new ProcessPannelSlot(2, 1);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot FUEL_SLOT = new ProcessPannelSlot(2, 3);
    private static final ProcessPannelSlot DECORATION_FIRE_SLOT = new ProcessPannelSlot(2, 2);

    public SmokingProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        super(recipe, actions, context);
    }

    private SmokingRecipe getSmokingRecipe() {
        return (SmokingRecipe) recipe;
    }

    @Override
    protected Map<RecipeGuiSharedButton, ProcessPannelSlot> buildRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();
    }

    @Override
    protected Map<ProcessPannelSlot, CyclicIngredient> buildTickedIngredient() {
        Map<ProcessPannelSlot, CyclicIngredient> ticked = new HashMap<>();
        ticked.put(INPUT_SLOT, new CyclicIngredient(getSmokingRecipe().getInputChoice()));
        ticked.put(FUEL_SLOT, new CyclicIngredient(Fuels.FUELS.toArray(new ItemStack[0])));
        return Map.copyOf(ticked);
    }
    
    @Override
    @SuppressWarnings("null")
    protected Map<ProcessPannelSlot, CyclicIngredient> buildTickedResult() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(getSmokingRecipe().getResult()));
    }

    @Override
    protected Map<ProcessPannelSlot, FastInvItem> buildStaticItems() {
        Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();
        statics.put(DECORATION_FIRE_SLOT, new FastInvItem(new ItemStack(Material.SMOKER), null));
        return Map.copyOf(statics);
    }
}
