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
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
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
    protected Map<RecipeGuiSharedButton, ProcessPannelSlot> buildRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();
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
