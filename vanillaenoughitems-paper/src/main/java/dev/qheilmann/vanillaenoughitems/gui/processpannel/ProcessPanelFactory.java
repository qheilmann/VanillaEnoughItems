package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

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
     * @return a new ProcessPanel
     */
    ProcessPanel create(Recipe recipe);
}
