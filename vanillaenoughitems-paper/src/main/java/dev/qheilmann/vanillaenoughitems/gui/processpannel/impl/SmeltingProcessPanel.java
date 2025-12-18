package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiControlledButton;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

/**
 * Panel for furnace smelting recipes.
 */
@NullMarked
public class SmeltingProcessPanel extends AbstractProcessPanel {
    private static final ProcessPannelSlot INPUT_SLOT = new ProcessPannelSlot(2, 1);
    private static final ProcessPannelSlot FUEL_SLOT = new ProcessPannelSlot(2, 3);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot FLAME_SLOT = new ProcessPannelSlot(2, 2);

    public SmeltingProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        super(checkRecipe(recipe), actions, context);
    }

    private static Recipe checkRecipe(Recipe recipe) {
        if (!(recipe instanceof FurnaceRecipe)) {
            throw new IllegalArgumentException("SmeltingProcess requires a FurnaceRecipe");
        }

        return recipe;
    }

    private FurnaceRecipe getFurnaceRecipe() {
        return (FurnaceRecipe) recipe;
    }

    @Override
    protected Map<RecipeGuiControlledButton, ProcessPannelSlot> buildRecipeGuiButtonMap() {
        Map<RecipeGuiControlledButton, ProcessPannelSlot> shared = new HashMap<>();
        shared.put(RecipeGuiControlledButton.NEXT_RECIPE,      new ProcessPannelSlot(1, 0));
        shared.put(RecipeGuiControlledButton.PREVIOUS_RECIPE,  new ProcessPannelSlot(3, 0));
        shared.put(RecipeGuiControlledButton.HISTORY_BACKWARD, new ProcessPannelSlot(1, 4));
        shared.put(RecipeGuiControlledButton.HISTORY_FORWARD,  new ProcessPannelSlot(3, 4));
        shared.put(RecipeGuiControlledButton.QUICK_CRAFT,      new ProcessPannelSlot(5, 3));
        return Map.copyOf(shared);
    }

    @Override
    protected Map<ProcessPannelSlot, CyclicIngredient> buildTickedItems() {
        Map<ProcessPannelSlot, CyclicIngredient> ticked = new HashMap<>();
        ticked.put(INPUT_SLOT, new CyclicIngredient(getFurnaceRecipe().getInputChoice()));
        ticked.put(FUEL_SLOT, new CyclicIngredient(Fuels.FUELS.toArray(new ItemStack[0])));
        return Map.copyOf(ticked);
    }

    @Override
    protected Map<ProcessPannelSlot, FastInvItem> buildStaticItems() {
        Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();
        statics.put(OUTPUT_SLOT, new FastInvItem(getFurnaceRecipe().getResult(), null));
        statics.put(FLAME_SLOT, new FastInvItem(new ItemStack(Material.FIRE_CHARGE), null));
        return Map.copyOf(statics);
    }
}
