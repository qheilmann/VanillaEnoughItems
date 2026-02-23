package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.Style;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Registry mapping Process types to their corresponding ProcessPanelFactory implementations.
 * Provides factory methods to create the correct panel for a given recipe and process.
 */
@NullMarked
public interface ProcessPanelRegistry {

    /**
     * Register a ProcessPanelFactory for the given process.
     * <b>IMPORTANT: The provided factory should handle any recipe supported by the associated process,
     * see {@link Process#canHandleRecipe(Recipe)}</b>
     * 
     * @param process the process to register the provider for
     * @param provider the factory to register
     */
    void registerProvider(Process process, ProcessPanelFactory provider);

    /**
     * Create a ProcessPanel for the given recipe under the associated process.
     * If no factory is registered for the process, returns an {@link ProcessPanel.UndefinedProcessPanel}.
     * 
     * @param process the process the recipe belongs to
     * @param recipe the recipe to create the panel for
     * @param style the style configuration
     * @return a new ProcessPanel for the given recipe
     */
    ProcessPanel createPanel(Process process, Recipe recipe, Style style);

    /**
     * Check if a factory is registered for the given process.
     * @param process the process to check
     * @return true if a factory is registered
     */
    boolean hasFactory(Process process);
}
