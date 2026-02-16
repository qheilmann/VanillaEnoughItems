package dev.qheilmann.vanillaenoughitems.recipe.index;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeHelper;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Store multiple recipes. All recipes undergo the same unique process.
 * The recipes set are immutable
 */
@NullMarked
public class ProcessRecipeSet {

    private final Process process;
    private final ConcurrentSkipListSet<Recipe> recipes;

    /**
     * Create a ProcessRecipeSet without initial recipes
     * @param process the process
     */
    public ProcessRecipeSet(Process process) {
        this.process = process;
        recipes = new ConcurrentSkipListSet<>(RecipeHelper.RECIPE_COMPARATOR); // Concurrent NavigableSet
    }

    /**
     * Create a ProcessRecipeSet with initial recipes
     * @param process the process
     * @param recipes the initial recipes
     */
    public ProcessRecipeSet(Process process, Set<Recipe> recipes) {
        this(process);

        for (Recipe recipe : recipes) {
            add(recipe);
        }
    }

    /**
     * Create a copy of an existing ProcessRecipeSet.
     * The copy clone the recipes and process.
     * @param other the ProcessRecipeSet to copy
     */
    public ProcessRecipeSet(ProcessRecipeSet other) {
        this(other.process, other.recipes);
    }

    /**
     * Get an unmodifiable view of the recipes
     * @return unmodifiable view of the recipes
     */
    public NavigableSet<Recipe> getRecipes() {
        return Collections.unmodifiableNavigableSet(recipes);
    }

    /**
     * Get the process associated with this set
     * @return the process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Add a recipe to the set if the process can handle it
     * @param recipe the recipe to add
     * @return true if the recipe was added, false if the process cannot handle it
     */
    public boolean add(Recipe recipe) {

        if (!process.canHandleRecipe(recipe)) {
            return false;
        }
        
        recipes.add(recipe);
        return true;
    }

    /**
     * Add multiple recipes to the set if the process can handle them
     * @param recipesToAdd the recipes to add
     */
    public void addAll(Set<Recipe> recipesToAdd) {
        for (Recipe recipe : recipesToAdd) {
            add(recipe);
        }
    }

    /**
     * Remove a recipe from the set
     * @param recipe the recipe to remove
     * @return true if this set contained the specified recipe
     */
    public boolean remove(Recipe recipe) {
        return recipes.remove(recipe);
    }

    /**
     * Remove multiple recipes from the set
     * @param recipesToRemove the recipes to remove
     */
    public void removeAll(Set<Recipe> recipesToRemove) {
        for (Recipe recipe : recipesToRemove) {
            remove(recipe);
        }
    }

    /**
     * Get the number of recipes in the set
     * @return the number of recipes
     */
    public int size() {
        return recipes.size();
    }

    /**
     * Check if the set is empty
     * @return true if the set is empty, false otherwise
     */
    public boolean isEmpty() {
        return recipes.isEmpty();
    }

    /**
     * Clear all recipes from the set
     */
    public void clear() {
        recipes.clear();
    }

    @Override
    public int hashCode() {
        return Objects.hash(process, recipes);
    }
    
    /**
     * Check equality between this ProcessRecipeSet and another object
     * Considers two ProcessRecipeSets equal if they have the same process and identical sets of recipes
     * @param obj the object to compare with
     */
    @SuppressWarnings("null")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ProcessRecipeSet other)) return false;
        if (!process.equals(other.process)) return false;
        return recipes.equals(other.recipes);
    }
}
