package me.qheilmann.vei.Core.Recipe.Index;

import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.NavigableSet;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class ProcessRecipeReader {
    private final @NotNull ProcessRecipeSet<Recipe> recipeSet;
    private Recipe currentRecipe;

    public ProcessRecipeReader(@NotNull ProcessRecipeSet<Recipe> recipeSet) {
        this.recipeSet = recipeSet;
        this.currentRecipe = recipeSet.getAllRecipes().first();
    }

    public ProcessRecipeReader(@NotNull ProcessRecipeSet<Recipe> recipeSet, Recipe recipe) {
        this(recipeSet);
        setRecipe(recipe);
    }

    public void setRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null.");
        }

        if (!recipeSet.getAllRecipes().contains(recipe)) {
            throw new IllegalArgumentException("Recipe not found in the recipe set: " + recipe.getResult());
        }
        this.currentRecipe = recipe;
    }

    public Recipe currentRecipe() {
        return currentRecipe;
    }

    public boolean hasNext() {
        return recipeSet.getAllRecipes().higher(currentRecipe) != null;
    }

    public Recipe next() {
        Recipe nextRecipe = recipeSet.getAllRecipes().higher(currentRecipe);
        if (nextRecipe == null) {
            throw new NoSuchElementException("No next recipe available.");
        }
        return currentRecipe = nextRecipe;
    }

    public boolean hasPrevious() {
        return recipeSet.getAllRecipes().lower(currentRecipe) != null;
    }

    public Recipe previous() {
        Recipe previousRecipe = recipeSet.getAllRecipes().lower(currentRecipe);
        if (previousRecipe == null) {
            throw new NoSuchElementException("No previous recipe available.");
        }
        return currentRecipe = previousRecipe;
    }

    public Recipe first() {
        if (recipeSet.isEmpty()) {
            throw new NoSuchElementException("No recipes available.");
        }
        return currentRecipe = recipeSet.getAllRecipes().first();
    }

    public Recipe last() {
        if (recipeSet.isEmpty()) {
            throw new NoSuchElementException("No recipes available.");
        }
        return currentRecipe = recipeSet.getAllRecipes().last();
    }

    public boolean Contains(Recipe recipe) {
        return recipeSet.getAllRecipes().contains(recipe);
    }

    public NavigableSet<Recipe> getAllRecipes() {
        return Collections.unmodifiableNavigableSet(recipeSet.getAllRecipes());
    }
}