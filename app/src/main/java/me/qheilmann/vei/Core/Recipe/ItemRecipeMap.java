package me.qheilmann.vei.Core.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nullable;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Utils.NotNullMap;
import me.qheilmann.vei.Core.Process.CraftingProcess;
import me.qheilmann.vei.Core.Process.DummyProcess;
import me.qheilmann.vei.Core.Process.Process;

/**
 * Contains all recipes for an item, including all variants and different
 * processes for a specific item.
 */
public class ItemRecipeMap {
    
    private final NotNullMap<Process<?>, ProcessRecipeSet<?>> recipes;

    public ItemRecipeMap() {
        this(Collections.emptyMap());
    }

    public ItemRecipeMap(@NotNull Map<? extends Process<?>, ? extends ProcessRecipeSet<?>> recipeCollection) {
        Comparator<Process<?>> processComparator = getProcessOrderComparator();
        this.recipes = new NotNullMap<>(new TreeMap<>(processComparator), recipeCollection);
    }

    // Add methods to delegate to the wrapped NotNullMap

    /**
     * Returns the number of different processes used to craft the item.
     * 
     * @return the number of recipes in the map
     */
    public int size() {
        return recipes.size();
    }
    
    /**
     * Returns true if the map don't contains recipes.
     * 
     * @return true if the map contains no recipes
     */
    public boolean isEmpty() {
        return recipes.isEmpty();
    }
    
    /**
     * Returns true if the map contains the specified process.
     * 
     * @param process the process to check for
     * @return true if the map contains the specified process
     */
    public boolean containsProcess(@NotNull Object process) {     
        return recipes.containsKey(process);
    }

    /**
     * Returns the recipe set associated with the specified process.
     * 
     * @param process the process to get the recipe set for
     * @return the recipe set associated with the specified process
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends Recipe> ProcessRecipeSet<R> getProcessRecipeSet(@NotNull Process<R> process) {
        return (ProcessRecipeSet<R>) recipes.get(process);
    }

    /**
     * Adds a new recipe set with the specified process to the map.
     * 
     * @param process the process to add
     * @param recipeSet the recipe set to add
     * @return the previous value associated with the process, or null if there
     * was no mapping for the process
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends Recipe> ProcessRecipeSet<R> putProcessRecipeSet(@NotNull Process<R> process, @NotNull ProcessRecipeSet<R> recipeSet) {        
        return (ProcessRecipeSet<R>) recipes.put(process, recipeSet);
    }

    /**
     * Adds a new recipe set with the specified process to the map without type
     * checking. The Process generic type must match the ProcessRecipeSet generic
     * type, otherwise undefined behavior may occur.
     * 
     * @param process the process to add
     * @param recipeSet the recipe set to add
     * @return the previous value associated with the process, or null if there
     * was no mapping for the process
     * @throws IllegalArgumentException if the recipe set is not compatible with
     * the process
     */
    @Nullable
    public ProcessRecipeSet<?> unsafePutProcessRecipeSet(@NotNull Process<?> process, @NotNull ProcessRecipeSet<?> recipeSet) {
        return recipes.put(process, recipeSet);
    }

    /**
     * Removes the recipe set associated with the specified process from the map.
     * 
     * @param process the process to remove
     * @return the recipe set previously associated with the process, or null if
     * there was no mapping for the process
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends Recipe> ProcessRecipeSet<R> removeProcessRecipeSet(@Nullable Process<R> process) {
        return (ProcessRecipeSet<R>) recipes.remove(process);
    }

    /**
     * Removes all processes and their associated recipe sets from the map.
     */
    public void clear() {
        recipes.clear();
    }

    /**
     * Returns an unmodifiable view of the map.
     * 
     * @return an unmodifiable view of the map
     */
    @NotNull
    public Map<Process<?>, ProcessRecipeSet<?>> asMap() {
        return Collections.unmodifiableMap(recipes);
    }

    /**
     * Returns a set view of the processes contained in the map.
     * 
     * @return a set view of the processes contained in the map
     */
    @NotNull
    public Set<Process<?>> getAllProcess() {
        return recipes.keySet();
    }

    /**
     * Returns a collection view of the ProcessRecipe contained in the collection.
     * 
     * @return a collection view of the recipe sets contained in the collection
     */
    @NotNull
    public Collection<ProcessRecipeSet<?>> getAllProcessRecipeSet() {
        return recipes.values();
    }

    /**
     * Returns a set view of the mappings contained in the map.
     * 
     * @return a set view of the mappings contained in the map
     */
    @NotNull
    public Set<Map.Entry<Process<?>, ProcessRecipeSet<?>>> entrySet() {
        return recipes.entrySet();
    }

    /**
     * Get the hash code of the map.
     */
    public int hashCode() {
        return recipes.hashCode();
    }

    /**
     * Check if the map is equal to another object.
     */
    public String toString() {
        return recipes.toString();
    }

    /**
     * Provides a custom comparator for ordering processes.
     * The CraftingProcess is prioritized first, followed by all other processes in lexicographical order, and finally the DummyProcess.
     * 
     * @return a comparator for ordering processes
     */
    public static Comparator<Process<?>> getProcessOrderComparator() {
        return new Comparator<Process<?>>() {
            @Override
            public int compare(Process<?> p1, Process<?> p2) {
                if (p1.getProcessName().equals(CraftingProcess.PROCESS_NAME) && !p2.getProcessName().equals(CraftingProcess.PROCESS_NAME)) {
                    return -1;
                } else if (!p1.getProcessName().equals(CraftingProcess.PROCESS_NAME) && p2.getProcessName().equals(CraftingProcess.PROCESS_NAME)) {
                    return 1;
                } else if (p1.getProcessName().equals(DummyProcess.PROCESS_NAME) && !p2.getProcessName().equals(DummyProcess.PROCESS_NAME)) {
                    return 1;
                } else if (!p1.getProcessName().equals(DummyProcess.PROCESS_NAME) && p2.getProcessName().equals(DummyProcess.PROCESS_NAME)) {
                    return -1;
                } else {
                    return p1.getProcessName().compareTo(p2.getProcessName());
                }
            }
        };
    }
}
