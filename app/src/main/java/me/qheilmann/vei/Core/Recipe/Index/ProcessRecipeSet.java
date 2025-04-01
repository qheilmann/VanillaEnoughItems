package me.qheilmann.vei.Core.Recipe.Index;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

/**
 * Contains a set of recipe for a particular process
 */
public class ProcessRecipeSet<R extends Recipe> {
    
    public static final Comparator<Recipe> RECIPE_COMPARATOR = recipeComparator();

    /**
     * A collection of recipes.
     * Each entry in this set must be non-null, and this requirement must be strictly enforced.
     */
    private final ConcurrentSkipListSet<R> recipes;

    public ProcessRecipeSet() {
        this(Collections.emptyList());
    }

    public ProcessRecipeSet(@NotNull Collection<? extends R> ProcessRecipeCollection) {
        if (ProcessRecipeCollection.contains(null)) {
            throw new IllegalArgumentException("ProcessRecipeCollection cannot contain null values %s".formatted(ProcessRecipeCollection));
        }

        this.recipes = new ConcurrentSkipListSet<>(RECIPE_COMPARATOR);
        this.recipes.addAll(ProcessRecipeCollection);
    }

    /**
     * Returns all the recipes in the set. The recipe are ordered by the
     * {@link #RECIPE_COMPARATOR} comparator.
     * 
     * @return all the recipes in the set
     */
    @NotNull
    public NavigableSet<R> getAllRecipes() {
        return Collections.unmodifiableNavigableSet(recipes);
    }

    // Add methods to delegate to the wrapped NotNullSet
    //#region Delegation

    /**
     * Adds a recipe to the set.
     * 
     * @param recipe the recipe to add
     * @return true if this set did not already contain the specified element
     */
    public boolean add(@NotNull R recipe) {
        Objects.requireNonNull(recipe, "Recipe cannot be null");

        return recipes.add(recipe);
    }

    /**
     * Attempts to add a recipe to the set. The recipe is only added if it is an 
     * instance of the specified set type and not already in the set. You can differ
     * between the two cases by using {@link #contains(Recipe)}.
     * 
     * @param recipe the recipe to add
     * @return true if the recipe was added
     */
    @SuppressWarnings("unchecked")
    public boolean unsafeAdd(@NotNull Recipe recipe) {
        try {
            return add((R) recipe);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Recipe (%s) is not an instance of the specified set type (%s also)"
            .formatted(recipe, this.getClass(), this.getClass().getGenericSuperclass()), e);
        }
    }

    /**
     * Adds all recipes in the specified collection to the set.
     * 
     * @param recipes the collection of recipes to add
     * @return true if the set was modified
     */
    public boolean addAll(@NotNull Collection<R> recipes) {
        if (recipes.contains(null)) {
            throw new IllegalArgumentException("recipes collection cannot contain null values %s".formatted(recipes));
        }

        boolean modified = false;
        for (R recipe : recipes) {
            modified |= add(recipe);
        }
        return modified;
    }

    public boolean unsafeAddAll(@NotNull Collection<? extends Recipe> recipes) {
        boolean modified = false;
        for (Recipe recipe : recipes) {
            modified |= unsafeAdd(recipe);
        }
        return modified;
    }

    /**
     * Returns true if the set contains the specified recipe.
     * 
     * @param recipe the recipe to check for
     * @return true if the set contains the specified recipe
     */
    public boolean contains(@NotNull R recipe) {
        return recipes.contains(recipe);
    }

    /**
     * Returns true if the set contains all recipes in the specified collection.
     * 
     * @param recipes the collection of recipes to check for
     * @return true if the set contains all recipes in the specified collection
     */
    public boolean containsAll(@NotNull Collection<R> recipes) {
        return recipes.containsAll(recipes);
    }

    /**
     * Returns true if the set contains no recipes.
     * 
     * @return true if the set contains no recipes
     */
    public boolean isEmpty() {
        return recipes.isEmpty();
    }

    /**
     * Returns the number of recipes in the set.
     * 
     * @return the number of recipes in the set
     */
    public int size() {
        return recipes.size();
    }

    /**
     * Removes a recipe from the set.
     * 
     * @param recipe the recipe to remove
     * @return true if the recipe was removed
     */
    public boolean remove(@NotNull R recipe) {
        return recipes.remove(recipe);
    }

    /**
     * Attempts to remove a recipe from the set. The recipe is only removed if it is 
     * an instance of the specified set type and in the set.
     * 
     * @param recipe the recipe to remove
     * @return true if the recipe was removed
     */
    @SuppressWarnings("unchecked")
    public boolean unsafeRemove(@NotNull Recipe recipe) {
        try {
            return remove((R) recipe);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Recipe (%s) is not an instance of the specified set type (%s also)"
            .formatted(recipe, this.getClass(), this.getClass().getGenericSuperclass()), e);
        }
    }

    /**
     * Removes all recipes in the specified collection from the set.
     * 
     * @param recipes the collection of recipes to remove
     * @return true if the set was modified
     */
    public boolean removeAll(@NotNull Collection<R> recipes) {
        boolean modified = false;
        for (R recipe : recipes) {
            modified |= remove(recipe);
        }
        return modified;
    }

    /**
     * Retains only the recipes in the set that are contained in the specified 
     * collection.
     * 
     * @param recipes the collection of recipes to retain
     * @return true if the set was modified
     */
    public boolean retainAll(@NotNull Collection<R> recipes) {
        return recipes.retainAll(recipes);
    }

    /**
     * Removes all recipes from the set.
     */
    public void clear() {
        recipes.clear();
    }

    /**
     * Returns an iterator over the recipes in the set.
     * 
     * @return an iterator over the recipes in the set
     */
    @NotNull
    public Iterator<R> iterator() {
        return recipes.iterator();
    }

    /**
     * Returns an array containing all of the recipes in the set.
     * 
     * @return an array containing all of the recipes in the set
     */
    @NotNull
    public R[] toArray() {
        if (recipes.isEmpty()) {
            @SuppressWarnings("unchecked")
            R[] emptyArray = (R[]) new Recipe[0];
            return emptyArray;
        }
        @SuppressWarnings("unchecked")
        R[] array = (R[]) Array.newInstance(Recipe.class, recipes.size());
        return recipes.toArray(array);
    }

    /**
     * Returns an array containing all of the recipes in the set.
     * 
     * @param array the array into which the recipes are to be stored, if it is big 
     * enough; otherwise, a new array of the same runtime type is allocated for 
     * this purpose
     * @return an array containing all of the recipes in the set
     */
    @NotNull
    public R[] toArray(@NotNull R[] array) {
        return recipes.toArray(array);
    }

    //#endregion Delegation

    /**
     * Returns a string representation of the set.
     * 
     * @return a string representation of the set
     */
    @NotNull
    public String toString() {
        return recipes.toString();
    }

    /**
     * Returns true if the specified object is a set of recipes that contains the 
     * same recipes as this set.
     * 
     * @param o the object to compare
     * @return true if the specified object is a set of recipes that contains the 
     * same recipes as this set
     */
    public boolean equals(@NotNull Object o) {
        return recipes.equals(o);
    }

    /**
     * Returns a hash code value for the set.
     * 
     * @return a hash code value for the set
     */
    public int hashCode() {
        return recipes.hashCode();
    }

    /**
     * Provides a custom comparator for ordering processes.
     * The CraftingProcess is prioritized first, followed by all other processes in lexicographical order, and finally the DummyProcess.
     * 
     * @return a comparator for ordering processes
     */
    private static Comparator<Recipe> recipeComparator() {
        return new Comparator<Recipe>() {
            @Override
            public int compare(Recipe r1, Recipe r2) {

                // Check if both recipes refer to the same object
                if (r1 == r2) {
                    return 0;
                } 
                
                // Check normal cases, keyed recipes
                if (r1 instanceof org.bukkit.Keyed keyed1 && r2 instanceof org.bukkit.Keyed keyed2) {
                    // Compare the keys of the recipes
                    int keyComparison = keyed1.getKey().compareTo(keyed2.getKey());
                    if (keyComparison != 0) {
                        return keyComparison;
                    }
                } else if (r1 instanceof org.bukkit.Keyed) {
                    return -1; // r1 is a Keyed recipe, r2 is not
                } else if (r2 instanceof org.bukkit.Keyed) {
                    return 1; // r2 is a Keyed recipe, r1 is not
                }

                // If neither is a Keyed recipe, or not differentiable by key, we can compare them by their hash codes
                int deltaHashCode = r1.hashCode() - r2.hashCode();
                if (deltaHashCode != 0) {
                    return deltaHashCode;
                } 

                // If hash codes are equal, compare the class names for a consistent order (rare case)
                return r1.getClass().getName().compareTo(r2.getClass().getName());
            }
        };
    }
}
