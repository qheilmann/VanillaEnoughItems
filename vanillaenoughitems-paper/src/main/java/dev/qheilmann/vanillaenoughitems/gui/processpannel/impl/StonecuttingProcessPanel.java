package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

/**
 * Panel for stonecutting recipes.
 */
@NullMarked
public class StonecuttingProcessPanel extends AbstractProcessPanel {
    private static final ProcessPannelSlot INPUT_SLOT = new ProcessPannelSlot(2, 2);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot DECORATION_FIRE_SLOT = new ProcessPannelSlot(3, 2);

    public StonecuttingProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        super(recipe, actions, context);
    }

    private StonecuttingRecipe getStonecuttingRecipe() {
        return (StonecuttingRecipe) recipe;
    }

    @Override
    protected Map<RecipeGuiSharedButton, ProcessPannelSlot> buildRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();
    }

    @Override
    protected Map<ProcessPannelSlot, CyclicIngredient> buildTickedItems() {
        Map<ProcessPannelSlot, CyclicIngredient> ticked = new HashMap<>();
        ticked.put(INPUT_SLOT, new CyclicIngredient(getStonecuttingRecipe().getInputChoice()));
        ticked.put(OUTPUT_SLOT, new CyclicIngredient(getStonecuttingRecipe().getResult()));
        return Map.copyOf(ticked);
    }

    @Override
    protected Map<ProcessPannelSlot, FastInvItem> buildStaticItems() {
        Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();
        statics.put(DECORATION_FIRE_SLOT, new FastInvItem(new ItemStack(Material.STONECUTTER), null));
        return Map.copyOf(statics);
    }
}
