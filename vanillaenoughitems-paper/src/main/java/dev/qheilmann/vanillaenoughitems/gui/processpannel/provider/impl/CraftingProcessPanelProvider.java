package dev.qheilmann.vanillaenoughitems.gui.processpannel.provider.impl;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelProvider;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.CraftingProcessPanel;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.CraftingProcess;
import net.kyori.adventure.key.Key;

@NullMarked
public class CraftingProcessPanelProvider implements ProcessPanelProvider<@NonNull CraftingProcessPanel> {

    @Override
    public Key getAssignedProcessKey() {
        return CraftingProcess.KEY;
    }

    @Override
    public CraftingProcessPanel createPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        return new CraftingProcessPanel(recipe, actions, context);
    }
}
