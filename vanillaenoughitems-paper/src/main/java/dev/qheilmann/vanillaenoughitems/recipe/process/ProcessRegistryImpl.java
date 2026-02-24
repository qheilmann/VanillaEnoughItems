package dev.qheilmann.vanillaenoughitems.recipe.process;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.kyori.adventure.key.Key;

@NullMarked
public class ProcessRegistryImpl implements ProcessRegistry {

    private final Map<Key, Process> processes = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerProcess(Process process) {
        this.processes.put(process.key(), process);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Process getProcess(Key key) {
        return this.processes.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Process getRecipeProcess(Recipe recipe) {
        for (Process process : processes.values()) {
            if (process.canHandleRecipe(recipe)) {
                return process;
            }
        }
        return Process.UNDEFINED_PROCESS;
    }
}
