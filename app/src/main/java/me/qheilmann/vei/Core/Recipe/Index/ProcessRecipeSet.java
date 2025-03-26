package me.qheilmann.vei.Core.Recipe.Index;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.SequencedSet;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Utils.NotNullSequenceSet;

/**
 * Contains all process recipes for an item. Includes all recipe variants 
 * within a specific process for a particular item.
 */
public class ProcessRecipeSet<R extends Recipe> {
    
    /**
     * The set of recipes.
     * The recipes and recipeArray are kept in sync to ensure consistency.
     * Some methods require the use of a Set for operations like ensuring uniqueness,
     * while others need a List for operations like maintaining order or accessing by index.
     * Therefore, both collections are necessary.
     */
    private final NotNullSequenceSet<R> recipes;
    private final ArrayList<R> recipeArray;

    public ProcessRecipeSet() {
        this(Collections.emptyList());
    }

    public ProcessRecipeSet(Collection<? extends R> ProcessRecipeCollection) {
        this.recipes = new NotNullSequenceSet<>(new LinkedHashSet<>(), ProcessRecipeCollection);
        recipeArray = new ArrayList<>(recipes);
    }

    public ItemStack getItem() {
        return recipeArray.get(0).getResult();
        // TODO check during ctor and add if all process return at any moment the item of this set (result can be a list so change the protoype with the item of this set)
        // then change here to return the item of this set

        // check by a predicate (bc sometime by result or by ingredient or by nothing)
    }

    /**
     * Returns the recipe at the specified index in the collection.
     * 
     * @param index the index of the recipe to return
     * @return the recipe at the specified index in the collection
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @NotNull
    public R getVariant(int index) {
        return recipeArray.get(index);
    }

    /**
     * Returns all the recipes in the set.
     * 
     * @return all the recipes in the set
     */
    @NotNull
    public SequencedSet<R> getAllRecipes() {
        return Collections.unmodifiableSequencedSet(recipes);
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
        if (recipes.add(recipe)) {
            recipeArray.add(recipe);
            return true;
        }
        return false;
    }

    /**
     * Attempts to add a recipe to the set. The recipe is only added if it is an 
     * instance of the specified set type and not already in the set. You can differ
     * between the two cases by using {@link #contains(Object)}.
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
        boolean modified = false;
        for (R recipe : recipes) {
            modified |= add(recipe);
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
        if (recipes.remove(recipe)) {
            recipeArray.remove(recipe);
            return true;
        }
        return false;
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
        boolean modified = recipes.retainAll(recipes);
        recipeArray.clear();
        recipeArray.addAll(recipes);
        
        return modified; // TODO is array in the same order as before (no) ?
    }

    /**
     * Removes all recipes from the set.
     */
    public void clear() {
        recipes.clear();
        recipeArray.clear();
    }

    /**
     * Returns an iterator over the recipes in the set.
     * 
     * @return an iterator over the recipes in the set
     */
    @NotNull
    public Iterator<R> iterator() {
        return recipeArray.iterator();
    }

    /**
     * Returns an array containing all of the recipes in the set.
     * 
     * @return an array containing all of the recipes in the set
     */
    @NotNull
    public R[] toArray() {
        if (recipeArray.isEmpty()) {
            @SuppressWarnings("unchecked")
            R[] emptyArray = (R[]) new Recipe[0];
            return emptyArray;
        }
        @SuppressWarnings("unchecked")
        R[] array = (R[]) Array.newInstance(Recipe.class, recipeArray.size());
        return recipeArray.toArray(array);
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
        return recipeArray.toArray(array);
    }

    /**
     * Returns the index of the first occurrence of the specified element in 
     * this collection, or -1 if this collection does not contain the element
     * 
     * @param index the index of the recipe to return
     * @return the recipe at the specified index in the set
     */
    public int indexOf(R recipe) {
        if (recipe == null || !recipes.contains(recipe)) {
            return -1;
        }
        return recipeArray.indexOf(recipe);
    }

    //#endregion Delegation

    /**
     * Returns a string representation of the set.
     * 
     * @return a string representation of the set
     */
    @NotNull
    public String toString() {
        return recipeArray.toString();
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
