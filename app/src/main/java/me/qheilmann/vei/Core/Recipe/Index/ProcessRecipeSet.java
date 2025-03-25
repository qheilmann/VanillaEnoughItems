package me.qheilmann.vei.Core.Recipe.Index;

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
public class ProcessRecipeSet {
    
    /**
     * The set of recipes.
     * The recipes and recipeArray are kept in sync to ensure consistency.
     * Some methods require the use of a Set for operations like ensuring uniqueness,
     * while others need a List for operations like maintaining order or accessing by index.
     * Therefore, both collections are necessary.
     */
    private final NotNullSequenceSet<Recipe> recipes;
    private final ArrayList<Recipe> recipeArray;

    public ProcessRecipeSet() {
        this(Collections.emptyList());
    }

    public ProcessRecipeSet(Collection<Recipe> ProcessRecipeCollection) {
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
    public Recipe getVariant(int index) {
        return recipeArray.get(index);
    }

    /**
     * Returns all the recipes in the set.
     * 
     * @return all the recipes in the set
     */
    public SequencedSet<Recipe> getAllRecipes() {
        return Collections.unmodifiableSequencedSet(recipes);
    }

    // Add methods to delegate to the wrapped NotNullSet
    //#region Delegation

    /**
     * Adds a recipe to the set.
     * 
     * @param recipe the recipe to add
     * @return true if the recipe was added
     */
    public boolean add(@NotNull Recipe recipe) {
        if (recipes.add(recipe)) {
            recipeArray.add(recipe);
            return true;
        }
        return false;
    }

    /**
     * Adds all recipes in the specified collection to the set.
     * 
     * @param collection the collection of recipes to add
     * @return true if the set was modified
     */
    public boolean addAll(@NotNull Collection<Recipe> collection) {
        boolean modified = false;
        for (Recipe recipe : collection) {
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
    public boolean contains(@NotNull Recipe recipe) {
        return recipes.contains(recipe);
    }

    /**
     * Returns true if the set contains all recipes in the specified collection.
     * 
     * @param collection the collection of recipes to check for
     * @return true if the set contains all recipes in the specified collection
     */
    public boolean containsAll(@NotNull Collection<Recipe> collection) {
        return recipes.containsAll(collection);
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
    public boolean remove(@NotNull Recipe recipe) {
        if (recipes.remove(recipe)) {
            recipeArray.remove(recipe);
            return true;
        }
        return false;
    }

    /**
     * Removes all recipes in the specified collection from the set.
     * 
     * @param collection the collection of recipes to remove
     * @return true if the set was modified
     */
    public boolean removeAll(@NotNull Collection<Recipe> collection) {
        boolean modified = false;
        for (Recipe recipe : collection) {
            modified |= remove(recipe);
        }
        return modified;
    }

    /**
     * Retains only the recipes in the set that are contained in the specified 
     * collection.
     * 
     * @param collection the collection of recipes to retain
     * @return true if the set was modified
     */
    public boolean retainAll(@NotNull Collection<Recipe> collection) {
        boolean modified = recipes.retainAll(collection);
        recipeArray.clear();
        recipeArray.addAll(recipes);
        
        return modified; // TODO fast same order as before ?
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
    public Iterator<Recipe> iterator() {
        return recipeArray.iterator();
    }

    /**
     * Returns an array containing all of the recipes in the set.
     * 
     * @return an array containing all of the recipes in the set
     */
    @NotNull
    public Recipe[] toArray() {
        return recipeArray.toArray(new Recipe[recipeArray.size()]);
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
    public Recipe[] toArray(@NotNull Recipe[] array) {
        return recipeArray.toArray(array);
    }

    /**
     * Returns the index of the first occurrence of the specified element in 
     * this collection, or -1 if this collection does not contain the element
     * 
     * @param index the index of the recipe to return
     * @return the recipe at the specified index in the set
     */
    public int indexOf(Recipe recipe) {
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
