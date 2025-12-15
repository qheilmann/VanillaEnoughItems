package dev.qheilmann.vanillaenoughitems.index.processrecipe;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.index.process.Process;
import dev.qheilmann.vanillaenoughitems.index.recipe.RecipeHelper;

/**
 * Store multiple recipes. All recipes undergo the same unique process.
 * The recipes set are immutable
 */
@NullMarked
public class ProcessRecipeSet {

    Process process;
    ConcurrentSkipListSet<Recipe> recipes = new ConcurrentSkipListSet<>(RecipeHelper.RECIPE_COMPARATOR); // Concurrent Navigable set 

    /**
     * Create a ProcessRecipeSet without initial recipes
     * @param process the process
     */
    public ProcessRecipeSet(Process process) {
        this.process = process;
    }

    /**
     * Create a ProcessRecipeSet with initial recipes
     * @param process the process
     * @param recipes the initial recipes
     */
    public ProcessRecipeSet(Process process, Set<Recipe> recipes) {
        this.process = process;

        for (Recipe recipe : recipes) {
            add(recipe);
        }
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
}
