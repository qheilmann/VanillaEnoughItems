package me.qheilmann.vei.Core.Recipe.Index.Reader;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Collections;
import java.util.NavigableSet;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Recipe.Index.ProcessRecipeSet;

public class ProcessRecipeReader<R extends Recipe> {
    private final @NotNull ProcessRecipeSet<R> recipeSet;
    private R currentRecipe;

    public ProcessRecipeReader(@NotNull ProcessRecipeSet<R> recipeSet) {
        this(recipeSet, recipeSet.getAllRecipes().first());
    }

    public ProcessRecipeReader(@NotNull ProcessRecipeSet<R> recipeSet, R recipe) {
        Objects.requireNonNull(recipeSet, "RecipeSet cannot be null.");

        this.recipeSet = recipeSet;
        setRecipe(recipe);
    }

    public boolean setRecipe(R recipe) {
        Objects.requireNonNull(recipe, "Recipe cannot be null.");

        if (!recipeSet.getAllRecipes().contains(recipe)) {
            return false; // Recipe not found in the set, cannot set it as current.
        }   
        this.currentRecipe = recipe;
        return true;
    }


    /**
     * Sets the current recipe to the given recipe
     * @param recipe
     * @return true if the recipe was set, false if the recipe was not found in the set.
     * @throws ClassCastException if the types of one or more elements
     * in the specified collection are incompatible with this reader
     */
    @SuppressWarnings("unchecked")
    public boolean unsafeSetRecipe(Recipe recipe) throws ClassCastException {
        return setRecipe((R) recipe);
    }

    public R currentRecipe() {
        return currentRecipe;
    }

    public boolean hasNext() {
        return recipeSet.getAllRecipes().higher(currentRecipe) != null;
    }

    public R next() {
        R nextRecipe = recipeSet.getAllRecipes().higher(currentRecipe);
        if (nextRecipe == null) {
            throw new NoSuchElementException("No next recipe available.");
        }
        return currentRecipe = nextRecipe;
    }

    public boolean hasPrevious() {
        return recipeSet.getAllRecipes().lower(currentRecipe) != null;
    }

    public R previous() {
        R previousRecipe = recipeSet.getAllRecipes().lower(currentRecipe);
        if (previousRecipe == null) {
            throw new NoSuchElementException("No previous recipe available.");
        }
        return currentRecipe = previousRecipe;
    }

    public R first() {
        if (recipeSet.isEmpty()) {
            throw new NoSuchElementException("No recipes available.");
        }
        return currentRecipe = recipeSet.getAllRecipes().first();
    }

    public R last() {
        if (recipeSet.isEmpty()) {
            throw new NoSuchElementException("No recipes available.");
        }
        return currentRecipe = recipeSet.getAllRecipes().last();
    }

    public boolean Contains(R recipe) {
        return recipeSet.getAllRecipes().contains(recipe);
    }

    public NavigableSet<R> getAllRecipes() {
        return Collections.unmodifiableNavigableSet(recipeSet.getAllRecipes());
    }

    // Temporary adapter for smooth transition
    public ProcessRecipeSet<R> temporaryGetRecipeSet() {
        return recipeSet;
    }
}