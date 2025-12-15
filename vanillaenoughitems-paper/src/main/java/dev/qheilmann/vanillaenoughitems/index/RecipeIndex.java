package dev.qheilmann.vanillaenoughitems.index;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.index.processrecipe.MultiProcessRecipeMap;
import dev.qheilmann.vanillaenoughitems.index.processrecipe.ProcessRecipeSet;
import dev.qheilmann.vanillaenoughitems.index.processrecipe.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.index.process.Process;
import net.kyori.adventure.key.Key;

/**
 * Index for all recipes
 * The main entry to walk over recipes.
 * Contain set of recipes depending of your search (ingredient, output, process, id, etc)
 */
@NullMarked
public class RecipeIndex {
    
    // Search indexes
    private final ConcurrentSkipListMap<Key, Recipe> recipesById = new ConcurrentSkipListMap<>(Key.comparator());
    // private final ConcurrentSkipListMap<Process, ProcessRecipeSet> recipesByProcess = new ConcurrentSkipListMap<>();
    private final MultiProcessRecipeMap recipesByProcess = new MultiProcessRecipeMap();
    // ItemStack#hashcode are not really reliable between sessions, at least we regenerate them each time
    private final ConcurrentHashMap<ItemStack, MultiProcessRecipeMap> recipesByResult = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ItemStack, MultiProcessRecipeMap> recipesByIngredient = new ConcurrentHashMap<>();

    // Inverse index for fast lookup
    /**
     * Store the first compatible process for a recipe depending {@link Process#COMPARATOR}.
     */
    private final ConcurrentHashMap<Recipe, Process> processByRecipe = new ConcurrentHashMap<>();

    /**
     * Create an empty RecipeIndex
     */
    public RecipeIndex() {
    }

    //#region Indexing

    /**
     * Index multiple recipes with an iterable.
     * You can use a lambda {@code () -> iterator} to pass an iterator instance
     * @param recipes the recipes to index
     */
    public void indexRecipe(Iterable<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            indexRecipe(recipe);
        }
    }

    /**
     * Index a single recipe
     * @param recipe the recipe to index
     */
    public void indexRecipe(Recipe recipe) {
        // TODO implement indexing
        // need extractor things from recipe (result, ingredients, other)
    }

    /**
     * Unindex multiple recipes with an iterable.
     * You can use a lambda {@code () -> iterator} to pass an iterator instance
     * @param recipes the recipes to unindex
     */
    public void unindexRecipe(Iterable<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            unindexRecipe(recipe);
        }
    }

    /**
     * Unindex a single recipe
     * @param recipe the recipe to unindex
     */
    public void unindexRecipe(Recipe recipe) {
        // TODO implement unindexing
    }

    /**
     * Clear the entire index
     */
    public void clearIndex() {
        recipesById.clear();
        recipesByProcess.clear();
        recipesByResult.clear();
        recipesByIngredient.clear();
    }

    //#endregion Indexing

    //#region Searching

    /**
     * Get a MultiProcessRecipeReader by the result of this recipe key
     * @param key the recipe key
     * @return the MultiProcessRecipeReader, or null if not found, or complex recipes
     */
    @Nullable
    public MultiProcessRecipeReader readerByKey(Key key) {
        // Get the recipe by its key
        Recipe recipe = recipesById.get(key);
        if (recipe == null) {
            return null;
        }

        // Get the result of the recipe
        ItemStack recipeResult = recipe.getResult();
        if (recipeResult == null || recipeResult.isEmpty()) {
            return null;
        }

        // Get the MultiProcessRecipeMap by the result
        MultiProcessRecipeMap multiProcessRecipeMap = recipesByResult.get(recipeResult);
        if (multiProcessRecipeMap == null) {
            // No recipes found for this result
            // Should not happen if the index is consistent
            return null; 
        }

        // Preset the reader to the right process and recipe
        MultiProcessRecipeReader reader = new MultiProcessRecipeReader(multiProcessRecipeMap, processByRecipe.get(recipe));
        reader.getCurrentProcessRecipeReader().setCurrent(recipe);
        return reader;
    }

    /**
     * Return a MultiProcessRecipeReader with only one entry for the specified process.
     * The reader will contain only recipes for that single process.
     *
     * @param process the target process
     * @return a MultiProcessRecipeReader for the process, or null if none exist
     */
    @Nullable
    public MultiProcessRecipeReader readerByProcess(Process process) {
        ProcessRecipeSet processRecipeSet = recipesByProcess.getProcessRecipeSet(process);
        if (processRecipeSet == null) {
            return null;
        }

        MultiProcessRecipeMap singleProcessRecipeMap = new MultiProcessRecipeMap(Collections.singleton(processRecipeSet));
        return new MultiProcessRecipeReader(singleProcessRecipeMap, process);
    }

    /**
     * Return a MultiProcessRecipeReader for the specified result.
     * All recipes are categorized by their process.
     *
     * @param result the target result
     * @return a MultiProcessRecipeReader for the result, or null if none exist
     */
    @Nullable
    public MultiProcessRecipeReader readerByResult(ItemStack result) {
        MultiProcessRecipeMap multiProcessRecipeMap = recipesByResult.get(result);
        if (multiProcessRecipeMap == null) {
            return null;
        }

        return new MultiProcessRecipeReader(multiProcessRecipeMap);
    }

    // TODO add byResult + starting process (+ throw if not found)
    // same for byIngredient, etc

    /**
     * Return a MultiProcessRecipeReader for the specified ingredient.
     * All recipes are categorized by their process.
     *
     * @param ingredient the target ingredient
     * @return a MultiProcessRecipeReader for the ingredient, or null if none exist
     */
    @Nullable
    public MultiProcessRecipeReader readerByIngredient(ItemStack ingredient) {
        MultiProcessRecipeMap multiProcessRecipeMap = recipesByIngredient.get(ingredient);
        if (multiProcessRecipeMap == null) {
            return null;
        }

        return new MultiProcessRecipeReader(multiProcessRecipeMap);
    }

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index.
     * All recipes are categorized by their process.
     *
     * @return a MultiProcessRecipeReader for all recipes
     */
    public MultiProcessRecipeReader readerWithAllRecipes() {
        return new MultiProcessRecipeReader(recipesByProcess);
    }

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index,
     * starting at the specified default process.
     * All recipes are categorized by their process.
     *
     * @param defaultProcess the process to start at
     * @return a MultiProcessRecipeReader for all recipes
     */
    public MultiProcessRecipeReader readerWithAllRecipes(Process defaultProcess) {
        return new MultiProcessRecipeReader(recipesByProcess, defaultProcess);
    }

    //#endregion Searching

    //#region Exporting

    /**
     * Get an unmodifiable view of all recipes by their id
     * @return unmodifiable view of all recipes by their id
     */
    public NavigableMap<Key, Recipe> getAllRecipesById() {
        return Collections.unmodifiableNavigableMap(recipesById); 
    }

    /**
     * Get an unmodifiable view of all recipes by their process
     * <p>
     * <b>Warning:</b> The returned map is unmodifiable, but the {@link ProcessRecipeSet} values
     * within are <b>NOT</b> immutable. Do not modify the inner sets or recipes as this will break
     * the index consistency. Use {@link #readerByProcess(Process)} for safe read-only access.
     * <p>
     * This method is intended for internal use or advanced use cases where you need direct access
     * to the underlying structure.
     * 
     * @return unmodifiable view of all recipes by their process
     */
    public NavigableMap<Process, ProcessRecipeSet> getAllRecipesByProcess() {
        return Collections.unmodifiableNavigableMap(recipesByProcess.getAllProcessRecipeSets());
    }

    /**
     * Get an unmodifiable view of all recipes by their result.
     * <p>
     * <b>Warning:</b> The returned map is unmodifiable, but the {@link MultiProcessRecipeMap} values
     * within are <b>NOT</b> immutable. Do not modify the inner maps or recipes as this will break
     * the index consistency. Use {@link #readerByResult(ItemStack)} for safe read-only access.
     * <p>
     * This method is intended for internal use or advanced use cases where you need direct access
     * to the underlying structure.
     *
     * @return unmodifiable view of all recipes by their result
     */
    public Map<ItemStack, MultiProcessRecipeMap> getAllRecipesByResult() {
        return Collections.unmodifiableMap(recipesByResult);
    }

    /**
     * Get an unmodifiable view of all recipes by their ingredient.
     * <p>
     * <b>Warning:</b> The returned map is unmodifiable, but the {@link MultiProcessRecipeMap} values
     * within are <b>NOT</b> immutable. Do not modify the inner maps or recipes as this will break
     * the index consistency. Use {@link #readerByIngredient(ItemStack)} for safe read-only access.
     * <p>
     * This method is intended for internal use or advanced use cases where you need direct access
     * to the underlying structure.
     *
     * @return unmodifiable view of all recipes by their ingredient
     */
    public Map<ItemStack, MultiProcessRecipeMap> getAllRecipesByIngredient() {
        return Collections.unmodifiableMap(recipesByIngredient);
    }

    /**
     * Get a single recipe by its key
     * @param key the recipe key
     * @return the recipe, or null if not found
     */
    @Nullable
    public Recipe getSingleRecipeByKey(Key key) {
        return recipesById.get(key);
    }

    //#endregion Exporting
}
