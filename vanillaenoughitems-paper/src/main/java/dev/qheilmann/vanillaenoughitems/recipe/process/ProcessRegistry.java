package dev.qheilmann.vanillaenoughitems.recipe.process;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.kyori.adventure.key.Key;

@NullMarked
public class ProcessRegistry {

    private final Map<Key, Process> processes = new HashMap<>();

    /**
     * Register a process
     * @param process the process to register
     */
    public void registerProcess(Process process) {
        this.processes.put(process.key(), process);
    }

    /**
     * Get a process by its key
     * @param key the key of the process
     * @return the process, or null if not found
     */
    @Nullable
    public Process getProcess(Key key) {
        return this.processes.get(key);
    }

    /**
     * Get the process that can handle the given recipe
     * @param recipe the recipe to check
     * @return the process that can handle the recipe, or UNDEFINED_PROCESS if none found
     */
    public Process getRecipeProcess(Recipe recipe) {
        for (Process process : processes.values()) {
            if (process.canHandleRecipe(recipe)) {
                return process;
            }
        }
        return Process.UNDEFINED_PROCESS;
    }
}
