package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.Map;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.SharedButtonType;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

@NullMarked
public class SmeltingProcessPanel extends AbstractProcessPanel{

    public static final ProcessPannelSlot INPUT = new ProcessPannelSlot(2, 1);
    public static final ProcessPannelSlot FUEL = new ProcessPannelSlot(2, 3);
    public static final ProcessPannelSlot OUTPUT = new ProcessPannelSlot(5, 2);

    public SmeltingProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        super(recipe, actions, context);
    }

    @SuppressWarnings("null")
    @Override
    public Map<ProcessPannelSlot, FastInvItem> renderRecipe(Map<SharedButtonType, FastInvItem> sharedButtons) {
        FurnaceRecipe furnaceRecipe = (FurnaceRecipe) getRecipe();

        // TODO impl it correctly
        return Map.of(
            INPUT, new FastInvItem(furnaceRecipe.getInput().asOne(), null),
            OUTPUT, new FastInvItem(furnaceRecipe.getResult().asOne(), null),
            FUEL, new FastInvItem(Fuels.FUELS.getFirst(), null)
        );
    }
}
