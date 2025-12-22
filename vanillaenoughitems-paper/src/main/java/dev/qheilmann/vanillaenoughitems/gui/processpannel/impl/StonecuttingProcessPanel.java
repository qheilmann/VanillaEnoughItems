package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

/**
 * Panel for stonecutting recipes.
 */
@NullMarked
public class StonecuttingProcessPanel implements ProcessPanel {
    private static final ProcessPannelSlot INPUT_SLOT = new ProcessPannelSlot(2, 2);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot DECORATION_FIRE_SLOT = new ProcessPannelSlot(3, 2);

    private final Recipe recipe;

    public StonecuttingProcessPanel(Recipe recipe) {
        this.recipe = recipe;
    }

    private StonecuttingRecipe getStonecuttingRecipe() {
        return (StonecuttingRecipe) recipe;
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
        ticked.put(INPUT_SLOT, new CyclicIngredient(getStonecuttingRecipe().getInputChoice()));
        return Map.copyOf(ticked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("null")
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(getStonecuttingRecipe().getResult()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, FastInvItem> getStaticItems() {
        Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();
        statics.put(DECORATION_FIRE_SLOT, new FastInvItem(new ItemStack(Material.STONECUTTER), null));
        return Map.copyOf(statics);
    }
}
