package me.qheilmann.vei.Core.Recipe.Index;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Process.Process;

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

        this.recipes = new ConcurrentSkipListMap<>(Process.comparator());
        recipes.putAll(recipeCollection);
    }

    /**
     * Add a recipe to the map.
     */
    public void addRecipe(@NotNull Recipe recipe) {
        Objects.requireNonNull(recipe, "Recipe cannot be null");

        Process<?> process = Process.ProcessRegistry.getProcessByRecipe(recipe);
        ProcessRecipeSet<?> processRecipeSet = recipes.computeIfAbsent(process, p -> new ProcessRecipeSet<>());
        processRecipeSet.unsafeAdd(recipe);
    }

    public void addProcessRecipeSet(@NotNull Process<?> process, @NotNull ProcessRecipeSet<?> recipeSet) {
        Objects.requireNonNull(process, "Process cannot be null");
        Objects.requireNonNull(recipeSet, "Recipe set cannot be null");

        if (recipeSet.isEmpty()) {
            return;
        }

        ProcessRecipeSet<?> processRecipeSet = recipes.computeIfAbsent(process, p -> new ProcessRecipeSet<>());
        processRecipeSet.unsafeAddAll(recipeSet.getAllRecipes());
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
        Process<?> process = Process.ProcessRegistry.getProcessByRecipe(recipe);
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
     * Get all the recipes from each process in an unmodifiable NavigaleSet.
     */
    public NavigableSet<Recipe> getAllRecipes() {
        NavigableSet<Recipe> allRecipes = new ConcurrentSkipListSet<>(ProcessRecipeSet.RECIPE_COMPARATOR);
        for (ProcessRecipeSet<?> processRecipeSet : recipes.values()) {
            allRecipes.addAll(processRecipeSet.getAllRecipes());
        }
        return Collections.unmodifiableNavigableSet(allRecipes);
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
     * type.
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
        for (Recipe recipe : recipeSet.getAllRecipes()) {
            Process<?> recipeProcess = Process.ProcessRegistry.getProcessByRecipe(recipe);
            if (recipeProcess == null) {
                if (recipe instanceof Keyed keyedRecipe) {
                    throw new IllegalArgumentException("Recipe %s has no process type".formatted(keyedRecipe.getKey()));
                } else {
                    throw new IllegalArgumentException("Recipe %s has no process type".formatted(recipe.getClass().getSimpleName()));
                }
            }

            // main logic
            if (!recipeProcess.equals(process)) {
                throw new IllegalArgumentException("Recipe %s (also %s) is not compatible with process %s (also %s)"
                    .formatted(recipe.getClass(), recipe.getClass().getGenericSuperclass(), process.getClass(), process.getClass().getGenericSuperclass()));
            }
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
     * Provides an unmodifiable NavigableMap view of the recipes map.
     * The map is ordered using the {@link Process#comparator()}.
     * 
     * @return an unmodifiable NavigableMap view of the recipes map
     */
    @NotNull
    public NavigableMap<Process<?>, ProcessRecipeSet<?>> asMap() {
        return Collections.unmodifiableNavigableMap(recipes);
    }

    /**
     * Returns an unmodifiable NavigableSet of all processes contained in the map.
     * The processes are ordered using the {@link Process#comparator()}.
     * 
     * @return an unmodifiable NavigableSet of all processes in the map
     */
    @NotNull
    public NavigableSet<Process<?>> getAllProcess() {
        return Collections.unmodifiableNavigableSet(recipes.navigableKeySet());
    }

    /**
     * Returns an unmodifiable SequencedSet of ProcessRecipeSets contained in 
     * the collection. The order matches the sequence of their associated 
     * processes, which are ordered using the {@link Process#comparator()}.
     * 
     * @return an unmodifiable SequencedSet of ProcessRecipeSets in the collection
     */
    @NotNull
    public SequencedSet<ProcessRecipeSet<?>> getAllProcessRecipeSet() {
        SequencedSet<ProcessRecipeSet<?>> collections = new LinkedHashSet<ProcessRecipeSet<?>>(recipes.values());
        return Collections.unmodifiableSequencedSet(collections);
    }

    /**
     * Returns a set view of the mappings contained in the map.
     * 
     * @return a set view of the mappings contained in the map
     */
    @NotNull
    public SequencedSet<Map.Entry<Process<?>, ProcessRecipeSet<?>>> entrySet() {
        SequencedSet<Map.Entry<Process<?>, ProcessRecipeSet<?>>> entries = new LinkedHashSet<Map.Entry<Process<?>, ProcessRecipeSet<?>>>(recipes.entrySet());
        return Collections.unmodifiableSequencedSet(entries);
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
}
