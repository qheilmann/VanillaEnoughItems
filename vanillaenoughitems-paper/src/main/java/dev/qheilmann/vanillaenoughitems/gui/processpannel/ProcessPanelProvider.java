package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import net.kyori.adventure.key.Key;

/**
 * Functional interface for providing ProcessPanel instances for a specific process.
 * @apiNote The provider should handle any recipe supported by that process, {@link Process#canHandleRecipe(Recipe)}
 */
@NullMarked
public interface ProcessPanelProvider<P extends @NonNull AbstractProcessPanel> {
    
    /**
     * Get the process this provider is assigned to
     * @return the assigned process
     */
    Key getAssignedProcessKey();
    
    /**
     * Create a ProcessPanel for the given recipe
     * 
     * @param recipe the recipe to render
     * @param actions the action interface for navigation
     * @param context the global GUI context
     * @return a new ProcessPanel instance
     */
    P createPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context); // TODO should we realy have the context here ? maybe jsut the Style ?, becasue the context is too permissive
}
