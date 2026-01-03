package dev.qheilmann.vanillaenoughitems.recipe.index;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeHelper;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Store multiple ProcessRecipeSet. Typically all the underway recipe are linked in some way, (e.g. same result)
 * This is immutable
 */
@NullMarked
public class MultiProcessRecipeMap {

    private final ConcurrentNavigableMap<Process, ProcessRecipeSet> processRecipeSets = new ConcurrentSkipListMap<>(Process.COMPARATOR);
    private final Grouping grouping;

    /**
     * Create an empty MultiProcessRecipeMap
     * @param grouping the grouping of all the recipes in this map
     */
    public MultiProcessRecipeMap(Grouping grouping) {
        this.grouping = grouping;
    }

    /**
     * Create a MultiProcessRecipeMap with initial ProcessRecipeSets
     * @param grouping the grouping of all the recipes in this map
     * @param processRecipeSets the initial ProcessRecipeSets
     */
    public MultiProcessRecipeMap(Grouping grouping, Iterable<ProcessRecipeSet> processRecipeSets) {
        this(grouping);
        for (ProcessRecipeSet processRecipeSet : processRecipeSets) {
            putProcessRecipeSet(processRecipeSet);
        }
    }

    /**
     * Add a recipe to a ProcessRecipeSet. If the ProcessRecipeSet does not exist yet, it will be created
     * @param process the process associated to the ProcessRecipeSet
     * @param recipe the recipe to add to the inner ProcessRecipeSet
     * @return true if the recipe was added, false if the process cannot handle it
     */
    public boolean addRecipe(Process process, Recipe recipe) {
        ProcessRecipeSet processRecipeSet = processRecipeSets.computeIfAbsent(process, p -> new ProcessRecipeSet(process));
        return processRecipeSet.add(recipe);
    }

    /**
     * Remove a recipe from a ProcessRecipeSet, removing the ProcessRecipeSet if it becomes empty
     * @param process the process associated to the ProcessRecipeSet
     * @param recipe the recipe to remove from the inner ProcessRecipeSet
     * @return true if the recipe was removed, false if the process or recipe does not exist
     */
    public boolean removeRecipe(Process process, Recipe recipe) {
        ProcessRecipeSet processRecipeSet = processRecipeSets.get(process);
        if (processRecipeSet != null) {
            boolean removed = processRecipeSet.remove(recipe);
            if (processRecipeSet.isEmpty()) {
                processRecipeSets.remove(process);
            }
            return removed;
        }
        return false;
    }

    /**
     * Get the ProcessRecipeSet for a given process
     * @param process the process
     * @return the ProcessRecipeSet, or null if it does not exist
     */
    @Nullable
    public ProcessRecipeSet getProcessRecipeSet(Process process) {
        return processRecipeSets.get(process);
    }

    /**
     * Put a ProcessRecipeSet into the map if absent, else merge recipes
     * @param processRecipeSet the ProcessRecipeSet to put
     */
    public void putProcessRecipeSet(ProcessRecipeSet processRecipeSet) {
        Process process = processRecipeSet.getProcess();
        processRecipeSets.merge(process, processRecipeSet, (set1, set2) -> {
            set1.addAll(set2.getRecipes());
            return set1;
        });
    }

    /**
     * Remove a ProcessRecipeSet from the map
     * @param process the process associated to the ProcessRecipeSet
     * @return the removed ProcessRecipeSet, or null if it did not exist
     */
    @Nullable
    public ProcessRecipeSet removeProcessRecipeSet(Process process) {
        return processRecipeSets.remove(process);
    }

    /**
     * Get an unmodifiable view of the process recipe sets
     * @return unmodifiable view of the process recipe sets
     */
    @UnmodifiableView
    public NavigableMap<Process, ProcessRecipeSet> getAllProcessRecipeSets() {
        return Collections.unmodifiableNavigableMap(processRecipeSets);
    }

    /**
     * Get all processes in this map
     * @return unmodifiable view of all processes
     */
    @UnmodifiableView
    public NavigableSet<Process> getAllProcesses() {
        return Collections.unmodifiableNavigableSet(processRecipeSets.navigableKeySet());
    }

    /**
     * Get all recipes from all ProcessRecipeSets
     * @return a new NavigableSet containing all recipes
     */
    public NavigableSet<Recipe> getAllRecipes() {
        ConcurrentSkipListSet<Recipe> allRecipes = new ConcurrentSkipListSet<>(RecipeHelper.RECIPE_COMPARATOR);
        for (ProcessRecipeSet processRecipeSet : processRecipeSets.values()) {
            allRecipes.addAll(processRecipeSet.getRecipes());
        }
        return allRecipes;
    }

    /**
     * Clear all ProcessRecipeSets from the map
     */
    public void clear() {
        processRecipeSets.clear();
    }

    /**
     * Get the grouping criteria for this map
     * @return the grouping
     */
    public Grouping getGrouping() {
        return grouping;
    }
}
