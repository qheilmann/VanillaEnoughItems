package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiControlledButton;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

@NullMarked
public class CraftingProcessPanel extends AbstractProcessPanel {

    public CraftingProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        super(canHandleRecipe(recipe), actions, context);
    }

    public static Recipe canHandleRecipe(Recipe recipe) {
        if (!(recipe instanceof CraftingRecipe)) {
            throw new IllegalArgumentException("CraftingProcess requires a CraftingRecipe");
        }

        return recipe;
    }

    public CraftingRecipe getCraftingRecipe() {
        return (CraftingRecipe) recipe;
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
        ticked.put(new ProcessPannelSlot(0, 0), new CyclicIngredient(ItemType.COBBLESTONE.createItemStack()));
        ticked.put(new ProcessPannelSlot(5, 2), new CyclicIngredient(getCraftingRecipe().getResult()));
        return Map.copyOf(ticked);
    }


    @Override
    protected Map<ProcessPannelSlot, FastInvItem> buildStaticItems() {
        Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();
        statics.put(new ProcessPannelSlot(4, 2), new FastInvItem(ItemType.CRAFTING_TABLE.createItemStack(), null));
        return Map.copyOf(statics);
    }
}
