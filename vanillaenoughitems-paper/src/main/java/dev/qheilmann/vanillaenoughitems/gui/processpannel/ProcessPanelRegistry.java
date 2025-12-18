package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Maintains a mapping between Process types and their corresponding ProcessPanelFactory implementations.
 * Includes a factory method to create the correct panel for a given recipe.
 */
@NullMarked
public class ProcessPanelRegistry {
    
    private final Map<Process, ProcessPanelFactory> factories = new HashMap<>();

    /**
     * Register a ProcessPanelProvider for the given process
     * <b> IMPORTANT: The provided factory should handle any recipe supported by the associated process, {@link Process#canHandleRecipe(Recipe)}</b>
     * 
     * @param process the process to register the provider for
     * @param provider the provider to register
     */
    public void registerProvider(Process process, ProcessPanelFactory provider) {
        factories.put(process, provider);
    }

    /**
     * Create a ProcessPanel for the given recipe under the associated process
     * 
     * @param process the process the recipe belongs to
     * @param recipe the recipe to create the panel for
     * @param actions the action interface for navigation
     * @param context the global GUI context
     * @return a new ProcessPanel for the given recipe
     * @throws IllegalArgumentException if no factory is registered for the given process
     */
    public AbstractProcessPanel createPanel(Process process, Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {       
        // Non registered factory
        if (!hasFactory(process)) {
            throw new IllegalArgumentException("No ProcessPanelFactory registered for process: " + process.key());
        }

        ProcessPanelFactory factory = factories.get(process);
        return factory.create(recipe, actions, context);
    }

    public boolean hasFactory(Process process) {
        return factories.containsKey(process);
    }
}
