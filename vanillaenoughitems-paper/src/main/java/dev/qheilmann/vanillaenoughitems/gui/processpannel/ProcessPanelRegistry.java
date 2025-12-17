package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;
import net.kyori.adventure.key.Key;

/**
 * Registry that maps Process types to their ProcessPanelProvider implementations.
 * Provides a factory method to create appropriate panels for recipes.
 */
@NullMarked
public class ProcessPanelRegistry {
    
    private final Map<Process, ProcessPanelProvider<?>> providers = new HashMap<>();
    private final ProcessRegistry processRegistry;

    public ProcessPanelRegistry(ProcessRegistry processRegistry) {
        this.processRegistry = processRegistry;
    }

    /**
     * Register a ProcessPanelProvider for a specific process
     * 
     * @param provider the provider to register
     */
    public void registerProvider(ProcessPanelProvider<?> provider) {
        Key processKey = provider.getAssignedProcessKey();
        Process process = processRegistry.getProcess(processKey);
        if (process == null) {
            throw new IllegalArgumentException("No process registered with key: " + processKey);
        }

        providers.put(process, provider);
    }

    /**
     * Create a ProcessPanel for the given recipe and process
     * 
     * @param process the process for this recipe
     * @param recipe the recipe to render
     * @param actions the action interface for navigation
     * @param context the global GUI context
     * @return a new ProcessPanel, or null if no provider is registered
     */
    public AbstractProcessPanel createPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        NavigableMap<Recipe, Process> allProcessByRecipeMap = context.getRecipeIndex().getAllProcessByRecipeMap();
        Process process = allProcessByRecipeMap.get(recipe);
        if (process == null) {
            throw new IllegalArgumentException("No process found for recipe: " + recipe.getClass().getSimpleName());
        }

        if (!hasProvider(process)) {
            throw new IllegalStateException("No ProcessPanelProvider registered for process: " + process);
        }
        
        return providers.get(process).createPanel(recipe, actions, context);
    }

    /**
     * Check if a provider is registered for the given process
     * 
     * @param process the process to check
     * @return true if a provider is registered
     */
    public boolean hasProvider(Process process) {
        return providers.containsKey(process);
    }
}
