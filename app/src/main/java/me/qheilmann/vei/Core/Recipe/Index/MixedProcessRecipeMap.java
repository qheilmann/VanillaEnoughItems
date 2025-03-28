package me.qheilmann.vei.Core.Recipe.Index;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.annotation.Nullable;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Process.VanillaProcesses;

/**
 * Contains a collection of recipes witch are grouped by Process.
 */
public class MixedProcessRecipeMap {
    
    /**
     * A map that organizes recipes by their associated processes.
     * Keys and values in this map must be non-null and this constraint must be strictly enforced.
     */
    private final ConcurrentSkipListMap<Process<?>, ProcessRecipeSet<?>> recipes;

    public MixedProcessRecipeMap() {
        this(Collections.emptyMap());
    }

    public MixedProcessRecipeMap(@NotNull Map<? extends Process<?>, ProcessRecipeSet<? extends Recipe>> recipeCollection) {
        if (recipeCollection.containsKey(null) || recipeCollection.containsValue(null)) {
            throw new IllegalArgumentException("The map cannot contain null keys or values %s".formatted(recipeCollection));
        }

        Comparator<Process<?>> processComparator = processComparator();
        this.recipes = new ConcurrentSkipListMap<>(processComparator);
        recipes.putAll(recipeCollection);
    }

    /**
     * Add a recipe to the map.
     */
    public void addRecipe(@NotNull Recipe recipe) {
        Objects.requireNonNull(recipe, "Recipe cannot be null");

        Process<?> process = Process.ProcessRegistry.getProcesseByRecipe(recipe);
        ProcessRecipeSet<?> processRecipeSet = recipes.computeIfAbsent(process, p -> new ProcessRecipeSet<>());
        processRecipeSet.unsafeAdd(recipe);
    }

    /**
     * Removes a recipe from the map.
     * <p>
     * If the process's recipe set associated with the recipe no longer contains any recipes,
     * the process's recipe set is also removed from the map.
     * <p>
     * If the recipe is not present in its associated process's recipe set or if
     * the process itself is not in the map, no action is taken.
     */
    public void removeRecipe(@NotNull Recipe recipe) {
        Process<?> process = Process.ProcessRegistry.getProcesseByRecipe(recipe);
        ProcessRecipeSet<?> processRecipeSet = recipes.get(process);
        if (processRecipeSet != null) {
            processRecipeSet.unsafeRemove(recipe);

            // Also remove the process if it has no recipes anymore
            if (processRecipeSet.isEmpty()) {
                recipes.remove(process);
            }
        }
    }

    /**
     * Get all the recipes from each process in an unique unmodifiable set.
     */
    public SequencedSet<Recipe> getAllRecipes() {
        SequencedSet<Recipe> allRecipes = new LinkedHashSet<>();
        for (ProcessRecipeSet<?> processRecipeSet : recipes.values()) {
            allRecipes.addAll(processRecipeSet.getAllRecipes());
        }
        return Collections.unmodifiableSequencedSet(allRecipes);
    }

    // Add methods to delegate to the wrapped NotNullMap
    //#region Delegation

    /**
     * Returns the number of different processes used to craft the item.
     * 
     * @return the number of recipes in the map
     */
    public int size() {
        return recipes.size();
    }
    
    /**
     * Returns true if the map don't contains recipes.
     * 
     * @return true if the map contains no recipes
     */
    public boolean isEmpty() {
        return recipes.isEmpty();
    }
    
    /**
     * Returns true if the map contains the specified process.
     * 
     * @param process the process to check for
     * @return true if the map contains the specified process
     */
    public boolean containsProcess(@NotNull Process<?> process) {     
        return recipes.containsKey(process);
    }

    /**
     * Returns the recipe set associated with the specified process.
     * 
     * @param process the process to get the recipe set for
     * @return the recipe set associated with the specified process or null if
     * there is no mapping for the process
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends Recipe> ProcessRecipeSet<R> getProcessRecipeSet(@NotNull Process<R> process) {
        return (ProcessRecipeSet<R>) recipes.get(process);
    }

    /**
     * Adds a new recipe set with the specified process to the map.
     * 
     * @param process the process to add
     * @param recipeSet the recipe set to add
     * @return the previous value associated with the process, or null if there
     * was no mapping for the process
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends Recipe> ProcessRecipeSet<R> putProcessRecipeSet(@NotNull Process<R> process, @NotNull ProcessRecipeSet<R> recipeSet) {        
        return (ProcessRecipeSet<R>) recipes.put(process, recipeSet);
    }

    /**
     * Adds a new recipe set with the specified process to the map without type
     * checking. The Process generic type must match the ProcessRecipeSet generic
     * type, otherwise undefined behavior may occur.
     * 
     * @param process the process to add
     * @param recipeSet the recipe set to add
     * @return the previous value associated with the process, or null if there
     * was no mapping for the process
     * @throws IllegalArgumentException if the recipe set is not compatible with
     * the process
     */
    @Nullable
    @SuppressWarnings("unused")
    private ProcessRecipeSet<?> unsafePutProcessRecipeSet(@NotNull Process<?> process, @NotNull ProcessRecipeSet<?> recipeSet) {
        // Check if the recipe set is compatible with the process
        var classes = process.getRecipeClasses();
        for (var recipeClass : classes) {
            if (recipeSet.iterator().next().getClass().isAssignableFrom(recipeClass)) {
                break;
            }
            throw new IllegalArgumentException("Recipe set (%s also %s) is not compatible with process (%s also %s)"
                    .formatted(recipeSet.getClass(), recipeSet.getClass().getGenericSuperclass(), process.getClass(), process.getClass().getGenericSuperclass()));
        }

        return recipes.put(process, recipeSet);
    }

    /**
     * Removes the recipe set associated with the specified process from the map.
     * 
     * @param process the process to remove
     * @return the recipe set previously associated with the process, or null if
     * there was no mapping for the process
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends Recipe> ProcessRecipeSet<R> removeProcessRecipeSet(@Nullable Process<R> process) {
        return (ProcessRecipeSet<R>) recipes.remove(process);
    }

    /**
     * Removes all processes and their associated recipe sets from the map.
     */
    public void clear() {
        recipes.clear();
    }

    /**
     * Returns an unmodifiable view of the map.
     * 
     * @return an unmodifiable view of the map
     */
    @NotNull
    public Map<Process<?>, ProcessRecipeSet<?>> asMap() {
        return Collections.unmodifiableMap(recipes);
    }

    /**
     * Returns a set view of the processes contained in the map.
     * 
     * @return a set view of the processes contained in the map
     */
    @NotNull
    public Set<Process<?>> getAllProcess() {
        return recipes.keySet();
    }

    /**
     * Returns a collection view of the ProcessRecipe contained in the collection.
     * 
     * @return a collection view of the recipe sets contained in the collection
     */
    @NotNull
    public Collection<ProcessRecipeSet<?>> getAllProcessRecipeSet() {
        return recipes.values();
    }

    /**
     * Returns a set view of the mappings contained in the map.
     * 
     * @return a set view of the mappings contained in the map
     */
    @NotNull
    public Set<Map.Entry<Process<?>, ProcessRecipeSet<?>>> entrySet() {
        return recipes.entrySet();
    }

    //#endregion

    /**
     * Get the hash code of the map.
     */
    public int hashCode() {
        return recipes.hashCode();
    }

    /**
     * Check if the map is equal to another object.
     */
    public String toString() {
        return recipes.toString();
    }

    /**
     * Provides a custom comparator for ordering processes.
     * The CraftingProcess is prioritized first, followed by all other processes in lexicographical order, and finally the DummyProcess.
     * 
     * @return a comparator for ordering processes
     */
    public static Comparator<Process<?>> processComparator() {
        return new Comparator<Process<?>>() {
            @Override
            public int compare(Process<?> p1, Process<?> p2) {
                if (p1.getProcessName().equals(VanillaProcesses.CRAFTING_PROCESS_NAME) && !p2.getProcessName().equals(VanillaProcesses.CRAFTING_PROCESS_NAME)) {
                    return -1;
                } else if (!p1.getProcessName().equals(VanillaProcesses.CRAFTING_PROCESS_NAME) && p2.getProcessName().equals(VanillaProcesses.CRAFTING_PROCESS_NAME)) {
                    return 1;
                } else if (p1.getProcessName().equals(VanillaProcesses.DUMMY_PROCESS_NAME) && !p2.getProcessName().equals(VanillaProcesses.DUMMY_PROCESS_NAME)) {
                    return 1;
                } else if (!p1.getProcessName().equals(VanillaProcesses.DUMMY_PROCESS_NAME) && p2.getProcessName().equals(VanillaProcesses.DUMMY_PROCESS_NAME)) {
                    return -1;
                } else {
                    return p1.getProcessName().compareTo(p2.getProcessName());
                }
            }
        };
    }
}
