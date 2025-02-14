package me.qheilmann.vei.Core.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Utils.NotNullSet;

/**
 * Contains all process recipes for an item. Includes all recipe variants 
 * within a specific process for a particular item.
 */
public class ProcessRecipeSet {
    
    private final NotNullSet<Recipe> recipes;

    public ProcessRecipeSet() {
        this(Collections.emptyList());
    }

    public ProcessRecipeSet(Collection<? extends Recipe> ProcessRecipeCollection) {
        this.recipes = new NotNullSet<>(new HashSet<>(), ProcessRecipeCollection);
    }

    // Add methods to delegate to the wrapped NotNullSet

    /**
     * Adds a recipe to the set.
     * 
     * @param recipe the recipe to add
     * @return true if the recipe was added
     */
    public boolean add(@NotNull Recipe recipe) {
        return recipes.add(recipe);
    }

    /**
     * Adds all recipes in the specified collection to the set.
     * 
     * @param c the collection of recipes to add
     * @return true if the set was modified
     */
    public boolean addAll(@NotNull Collection<? extends Recipe> c) {
        return recipes.addAll(c);
    }

    /**
     * Returns true if the set contains the specified recipe.
     * 
     * @param o the recipe to check for
     * @return true if the set contains the specified recipe
     */
    public boolean contains(@NotNull Object o) {
        return recipes.contains(o);
    }

    /**
     * Returns true if the set contains all recipes in the specified collection.
     * 
     * @param c the collection of recipes to check for
     * @return true if the set contains all recipes in the specified collection
     */
    public boolean containsAll(@NotNull Collection<?> c) {
        return recipes.containsAll(c);
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
     * @param o the recipe to remove
     * @return true if the recipe was removed
     */
    public boolean remove(@NotNull Object o) {
        return recipes.remove(o);
    }

    /**
     * Removes all recipes in the specified collection from the set.
     * 
     * @param c the collection of recipes to remove
     * @return true if the set was modified
     */
    public boolean removeAll(@NotNull Collection<?> c) {
        return recipes.removeAll(c);
    }

    /**
     * Retains only the recipes in the set that are contained in the specified 
     * collection.
     * 
     * @param c the collection of recipes to retain
     * @return true if the set was modified
     */
    public boolean retainAll(@NotNull Collection<?> c) {
        return recipes.retainAll(c);
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
    public Iterator<Recipe> iterator() {
        return recipes.iterator();
    }

    /**
     * Returns an array containing all of the recipes in the set.
     * 
     * @return an array containing all of the recipes in the set
     */
    @NotNull
    public Recipe[] toArray() {
        return recipes.toArray(new Recipe[0]);
    }

    /**
     * Returns an array containing all of the recipes in the set.
     * 
     * @param a the array into which the recipes are to be stored, if it is big 
     * enough; otherwise, a new array of the same runtime type is allocated for 
     * this purpose
     * @return an array containing all of the recipes in the set
     */
    @NotNull
    public Recipe[] toArray(@NotNull Recipe[] a) {
        return recipes.toArray(a);
    }

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
}
