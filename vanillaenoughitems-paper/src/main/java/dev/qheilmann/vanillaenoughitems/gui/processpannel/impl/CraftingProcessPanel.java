package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.Map;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.SharedButtonType;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

@NullMarked
public class CraftingProcessPanel extends AbstractProcessPanel {

    public CraftingProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        super(recipe, actions, context);
    }

    @Override
    public Map<ProcessPannelSlot, FastInvItem> renderRecipe(Map<SharedButtonType, FastInvItem> sharedButtons) {
        return Map.of();
    }

}
