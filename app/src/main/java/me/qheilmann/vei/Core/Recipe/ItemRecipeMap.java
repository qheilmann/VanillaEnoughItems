package me.qheilmann.vei.Core.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Utils.NotNullMap;

/**
 * Contains all recipes for an item, including all variants and different
 * workbenches for a specific item.
 */
public class ItemRecipeMap {
    
    private final NotNullMap<Material, WorkbenchRecipeSet> recipes;

    public ItemRecipeMap() {
        this(Collections.emptyMap());
    }

    public ItemRecipeMap(@NotNull Map<? extends Material, ? extends WorkbenchRecipeSet> recipeCollection) {
        this.recipes = new NotNullMap<>(new HashMap<>(), recipeCollection);
    }

    // Add methods to delegate to the wrapped NotNullMap

    /**
     * Returns the number of different workbenches used to craft the item.
     * 
     * @return the number of recipes in the map
     */
    public int size() {
        return recipes.size();
    }
    
    /**
     * Returns true if the map contains workbench recipes.
     * 
     * @return true if the map contains no recipes
     */
    public boolean isEmpty() {
        return recipes.isEmpty();
    }
    
    /**
     * Returns true if the map contains the specified workbench.
     * 
     * @param workbench the workbench to check for
     * @return true if the map contains the specified workbench
     */
    public boolean containsWorkbench(@NotNull Object workbench) {
        return recipes.containsKey(workbench);
    }

    /**
     * Returns the recipe set associated with the specified workbench.
     * 
     * @param workbench the workbench to get the recipe set for
     * @return the recipe set associated with the specified workbench
     */
    @Nullable
    public WorkbenchRecipeSet getWorkbenchRecipeSet(@NotNull Object workbench) {
        return recipes.get(workbench);
    }

    /**
     * Adds a new recipe set with the specified workbench to the map.
     * 
     * @param workbench the workbench to add
     * @param recipeSet the recipe set to add
     * @return the previous value associated with the workbench, or null if there
     * was no mapping for the workbench
     */
    @Nullable
    public WorkbenchRecipeSet putWorkbenchRecipeSet(@NotNull Material workbench, @NotNull WorkbenchRecipeSet recipeSet) {
        return recipes.put(workbench, recipeSet);
    }

    /**
     * Removes the recipe set associated with the specified workbench from the map.
     * 
     * @param workbench the workbench to remove
     * @return the recipe set previously associated with the workbench, or null if
     * there was no mapping for the workbench
     */
    @Nullable
    public WorkbenchRecipeSet removeWorkbenchRecipeSet(@Nullable Object workbench) {
        return recipes.remove(workbench);
    }

    /**
     * Removes all workbenches and their associated recipe sets from the map.
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
    public Map<Material, WorkbenchRecipeSet> asMap() {
        return Collections.unmodifiableMap(recipes);
    }

    /**
     * Returns a set view of the workbenches contained in the map.
     * 
     * @return a set view of the workbenches contained in the map
     */
    @NotNull
    public Set<Material> workbenchSet() {
        return recipes.keySet();
    }

    /**
     * Returns a collection view of the recipe sets contained in the map.
     * 
     * @return a collection view of the recipe sets contained in the map
     */
    @NotNull
    public Collection<WorkbenchRecipeSet> recipeSet() {
        return recipes.values();
    }

    /**
     * Returns a set view of the mappings contained in the map.
     * 
     * @return a set view of the mappings contained in the map
     */
    @NotNull
    public Set<Map.Entry<Material, WorkbenchRecipeSet>> entrySet() {
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
