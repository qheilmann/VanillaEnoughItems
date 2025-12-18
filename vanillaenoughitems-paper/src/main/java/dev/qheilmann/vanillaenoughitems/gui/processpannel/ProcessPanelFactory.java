package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Functional interface for providing ProcessPanel instances for a recipe under a specific process.
 */
@FunctionalInterface
@NullMarked
public interface ProcessPanelFactory {
   
    /**
     * Create a ProcessPanel to show the given recipe under the associated process
     * <b> IMPORTANT: The provided factory should handle any recipe supported by the associated process, {@link Process#canHandleRecipe(Recipe)}</b>
     * 
     * @param recipe the recipe to render
     * @param actions the action interface for navigation
     * @param context the global GUI context
     * @return a new ProcessPanel
     */
    AbstractProcessPanel create(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context);
}