package dev.qheilmann.vanillaenoughitems.recipe.index;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeHelper;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import net.kyori.adventure.key.Key;

/**
 * Index all recipes by different criteria.
 * ingredient, result, process, id...
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
    private final ConcurrentSkipListMap<Recipe, Process> processByRecipe = new ConcurrentSkipListMap<>(RecipeHelper.RECIPE_COMPARATOR);

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

    public NavigableMap<Recipe, Process> getAllProcessByRecipeMap() {
        return Collections.unmodifiableNavigableMap(processByRecipe);
    }

    //#endregion Exporting
}
