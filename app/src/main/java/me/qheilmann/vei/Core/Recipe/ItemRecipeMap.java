package me.qheilmann.vei.Core.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Utils.NotNullMap;
import me.qheilmann.vei.Core.Process.Process;

/**
 * Contains all recipes for an item, including all variants and different
 * processes for a specific item.
 */
public class ItemRecipeMap {
    
    private final NotNullMap<Process<?>, ProcessRecipeSet> recipes;

    public ItemRecipeMap() {
        this(Collections.emptyMap());
    }

    public ItemRecipeMap(@NotNull Map<? extends Process<?>, ? extends ProcessRecipeSet> recipeCollection) {
        this.recipes = new NotNullMap<>(new HashMap<>(), recipeCollection);
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
    public ProcessRecipeSet getProcessRecipeSet(@NotNull Object process) {
        return recipes.get(process);
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
    public ProcessRecipeSet putProcessRecipeSet(@NotNull Process<?> process, @NotNull ProcessRecipeSet recipeSet) {
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
    public ProcessRecipeSet removeProcessRecipeSet(@Nullable Object process) {
        return recipes.remove(process);
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
    public Map<Process<?>, ProcessRecipeSet> asMap() {
        return Collections.unmodifiableMap(recipes);
    }

    /**
     * Returns a set view of the processes contained in the map.
     * 
     * @return a set view of the processes contained in the map
     */
    @NotNull
    public Set<Process<?>> ProcessSet() {
        return recipes.keySet();
    }

    /**
     * Returns a collection view of the recipe sets contained in the map.
     * 
     * @return a collection view of the recipe sets contained in the map
     */
    @NotNull
    public Collection<ProcessRecipeSet> recipeSet() {
        return recipes.values();
    }

    /**
     * Returns a set view of the mappings contained in the map.
     * 
     * @return a set view of the mappings contained in the map
     */
    @NotNull
    public Set<Map.Entry<Process<?>, ProcessRecipeSet>> entrySet() {
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
}
