package dev.qheilmann.vanillaenoughitems.recipe.index.reader;

import java.util.NavigableSet;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.index.ProcessRecipeSet;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Walk over an ProcessRecipeSet and save current recipe position.
 * The inner processRecipeSet can't be accessed to avoid modification.
 */
@NullMarked
public class ProcessRecipeReader {
    private final ProcessRecipeSet processRecipeSet;
    private Recipe currentRecipe;

    /**
     * Create a ProcessRecipeReader starting at the first recipe
     * @param processRecipeSet the process recipe set
     */
    public ProcessRecipeReader(ProcessRecipeSet processRecipeSet) {
        this(processRecipeSet, processRecipeSet.getRecipes().first());
    }

    /**
     * Create a ProcessRecipeReader starting at a specific recipe
     * @param processRecipeSet the process recipe set
     * @param startRecipe the recipe to start at
     */
    @SuppressWarnings("null")
    public ProcessRecipeReader(ProcessRecipeSet processRecipeSet, Recipe startRecipe) {
        this.processRecipeSet = processRecipeSet;
        setCurrent(startRecipe);
    }

    /**
     * Set the current recipe
     * @param recipe the recipe to set as current.<br>
     * Use {@link #contains(Recipe)} to check if the recipe exists in the ProcessRecipeSet
     * @throws IllegalArgumentException if the recipe does not exist in the ProcessRecipeSet
     */
    public void setCurrent(Recipe recipe) {
        if (!processRecipeSet.getRecipes().contains(recipe)) {
            throw new IllegalArgumentException("Recipe does not exist in the ProcessRecipeSet");
        }
        this.currentRecipe = recipe;
    }

    /**
     * Get the current recipe
     * @return the current recipe
     */
    public Recipe getCurrent() {
        return currentRecipe;
    }

    /**
     * Check if the ProcessRecipeSet contains a specific recipe
     * @param recipe the recipe to check
     * @return true if the recipe exists in the ProcessRecipeSet, false otherwise
     */
    public boolean contains(Recipe recipe) {
        return processRecipeSet.getRecipes().contains(recipe);
    }

    /**
     * Check if the current recipe is the first recipe in the ProcessRecipeSet
     * @return true if the current recipe is the first recipe, false otherwise
     */
    public boolean isFirst() {
        return processRecipeSet.getRecipes().lower(currentRecipe) == null;
    }

    /**
     * Move to the previous recipe in the ProcessRecipeSet
     * @return the previous recipe, or null if there is no previous recipe ({@link #isFirst()} is true)
     */
    public @Nullable Recipe previous() {
        Recipe previousRecipe = processRecipeSet.getRecipes().lower(currentRecipe);
        if (previousRecipe == null) {
            return null;
        }
        return currentRecipe = previousRecipe;
    }

    /**
     * Check if the current recipe is the last recipe in the ProcessRecipeSet
     * @return true if the current recipe is the last recipe, false otherwise
     */
    public boolean isLast() {
        return processRecipeSet.getRecipes().higher(currentRecipe) == null;
    }

    /**
     * Move to the next recipe in the ProcessRecipeSet
     * @return the next recipe, or null if there is no next recipe ({@link #isLast()} is true)
     */
    public @Nullable Recipe next() {
        Recipe nextRecipe = processRecipeSet.getRecipes().higher(currentRecipe);
        if (nextRecipe == null) {
            return null;
        }
        return currentRecipe = nextRecipe;
    }

    /**
     * Get the process associated with the ProcessRecipeSet being read
     * @return the associated process
     */
    public Process getProcess() {
        return processRecipeSet.getProcess();
    }

    /**
     * Get all recipes in the ProcessRecipeSet being read
     * @return all recipes
     */
    public NavigableSet<Recipe> getAllRecipes() {
        return processRecipeSet.getRecipes();
    }
}
