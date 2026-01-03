package dev.qheilmann.vanillaenoughitems.recipe.index.reader;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

import net.kyori.adventure.key.Key;

@NullMarked
public interface RecipeIndexView {

    //#region By Key

    /**
     * Get a MultiProcessRecipeReader by the result of this recipe key
     * @param key the recipe key
     * @return the MultiProcessRecipeReader, or null if not found
     */
    @Nullable
    public MultiProcessRecipeReader readerByKey(Key key);

    /**
     * Get a single recipe by its key
     * @param key the recipe key
     * @return the recipe, or null if not found
     */
    @Nullable
    public Recipe getSingleRecipeByKey(Key key);

    //#endregion By Key

    //#region By Process

    /**
     * Return a MultiProcessRecipeReader with only one entry for the specified process.
     * The reader will contain only recipes for that single process.
     *
     * @param process the target process
     * @return a MultiProcessRecipeReader for the process, or null if none exist
     */
    @Nullable
    public MultiProcessRecipeReader readerByProcess(Process process);

    /**
     * Return a MultiProcessRecipeReader with only one entry for the specified process,
     * starting at the specified recipe.
     * The reader will contain only recipes for that single process.
     *
     * @param process the target process
     * @param startRecipe the recipe to start at
     * @return a MultiProcessRecipeReader for the process, or null if none exist
     * @throws IllegalArgumentException if the recipe does not exist in the ProcessRecipeSet
     */
    @Nullable
    public MultiProcessRecipeReader readerByProcess(Process process, Recipe startRecipe);

    //#endregion By Process

    //#region By Result

    /**
     * Return a MultiProcessRecipeReader for the specified result.
     * All recipes are categorized by their process.
     *
     * @param result the target result
     * @return a MultiProcessRecipeReader for the result, or null if none exist
     */
    @Nullable
    public MultiProcessRecipeReader readerByResult(ItemStack result);

    /**
     * Return a MultiProcessRecipeReader for the specified result, starting at the specified process.
     * The reader will contain only recipes for that single process.
     *
     * @param result the target result
     * @param startProcess the target process
     * @return a MultiProcessRecipeReader for the result and process, or null if none exist
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    @Nullable
    public MultiProcessRecipeReader readerByResult(ItemStack result, Process startProcess);

    /**
     * Return a MultiProcessRecipeReader for the specified result, starting at the specified process and recipe.
     * The reader will contain only recipes for that single process.
     *
     * @param result the target result
     * @param startProcess the target process
     * @param startRecipe the recipe to start at
     * @return a MultiProcessRecipeReader for the result, process and recipe, or null if none exist
     * @throws IllegalArgumentException if the process or recipe does not exist in the MultiProcessRecipeMap
     */    
    @Nullable
    public MultiProcessRecipeReader readerByResult(ItemStack result, Process startProcess, Recipe startRecipe);

    //#endregion By Result

    //#region By Ingredient

    /**
     * Return a MultiProcessRecipeReader for the specified ingredient.
     * All recipes are categorized by their process.
     *
     * @param item the target ingredient
     * @return a MultiProcessRecipeReader for the ingredient, or null if none exist
     */
    @Nullable
    public MultiProcessRecipeReader readerByIngredient(ItemStack item);

    /**
     * Return a MultiProcessRecipeReader for the specified ingredient, starting at the specified process.
     * The reader will contain only recipes for that single process.
     *
     * @param item the target ingredient
     * @param startProcess the target process
     * @return a MultiProcessRecipeReader for the ingredient and process, or null if none exist
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    @Nullable
    public MultiProcessRecipeReader readerByIngredient(ItemStack item, Process startProcess);

    /**
     * Return a MultiProcessRecipeReader for the specified ingredient, starting at the specified process and recipe.
     * The reader will contain only recipes for that single process.
     *
     * @param item the target ingredient
     * @param startProcess the target process
     * @param startRecipe the recipe to start at
     * @return a MultiProcessRecipeReader for the ingredient, process and recipe, or null if none exist
     * @throws IllegalArgumentException if the process or recipe does not exist in the MultiProcessRecipeMap
     */
    @Nullable
    public MultiProcessRecipeReader readerByIngredient(ItemStack item, Process startProcess, Recipe startRecipe);

    //#endregion By Ingredient

    //#region All Recipes

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index starting at the first process.
     * All recipes are categorized by their process.
     *
     * @return a MultiProcessRecipeReader for all recipes
     */
    public MultiProcessRecipeReader readerWithAllRecipes();

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index starting at the specified process.
     * All recipes are categorized by their process.
     *
     * @param startProcess the target process
     * @return a MultiProcessRecipeReader for all recipes starting at the specified process
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    public MultiProcessRecipeReader readerWithAllRecipes(Process startProcess);

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index starting at the specified process and recipe.
     * All recipes are categorized by their process.
     *
     * @param process the target process
     * @param startRecipe the recipe to start at
     * @return a MultiProcessRecipeReader for all recipes starting at the specified process and recipe
     * @throws IllegalArgumentException if the process or recipe does not exist in the MultiProcessRecipeMap
     */
    public MultiProcessRecipeReader readerWithAllRecipes(Process startProcess, Recipe startRecipe);

    //#endregion All Recipes

    /**
     * Get all result items in the recipe index
     * @return a set of all result items
     */
    public Set<ItemStack> getAllResultItems();




    /**
     * Get all ingredient items in the recipe index
     * @return a set of all ingredient items
     */
    public Set<ItemStack> getAllIngredientItems();
}
