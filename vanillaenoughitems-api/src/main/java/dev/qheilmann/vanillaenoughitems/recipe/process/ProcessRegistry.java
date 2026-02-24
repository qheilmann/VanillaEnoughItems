package dev.qheilmann.vanillaenoughitems.recipe.process;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.kyori.adventure.key.Key;

/**
 * Registry for recipe processes.
 * Allows registering new process types and looking up processes by key or recipe.
 */
@NullMarked
public interface ProcessRegistry {

    /**
     * Register a process
     * @param process the process to register
     */
    void registerProcess(Process process);

    /**
     * Get a process by its key
     * @param key the key of the process
     * @return the process, or null if not found
     */
    @Nullable
    Process getProcess(Key key);

    /**
     * Get the process that can handle the given recipe
     * @param recipe the recipe to check
     * @return the process that can handle the recipe, or {@link Process#UNDEFINED_PROCESS} if none found
     */
    Process getRecipeProcess(Recipe recipe);
}
