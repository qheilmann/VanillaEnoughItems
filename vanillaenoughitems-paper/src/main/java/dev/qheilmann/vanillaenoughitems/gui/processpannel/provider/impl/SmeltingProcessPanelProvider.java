package dev.qheilmann.vanillaenoughitems.gui.processpannel.provider.impl;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelProvider;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.SmeltingProcessPanel;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmeltingProcess;
import net.kyori.adventure.key.Key;

@NullMarked
public class SmeltingProcessPanelProvider implements ProcessPanelProvider<@NonNull SmeltingProcessPanel> {

    @Override
    public Key getAssignedProcessKey() {
        return SmeltingProcess.KEY;
    }

    @Override
    public SmeltingProcessPanel createPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        return new SmeltingProcessPanel(recipe, actions, context);
    }
}
