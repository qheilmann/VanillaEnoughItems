package dev.qheilmann.vanillaenoughitems.recipe.index.reader;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.index.MultiProcessRecipeMap;
import dev.qheilmann.vanillaenoughitems.recipe.index.ProcessRecipeSet;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;
import net.kyori.adventure.key.Key;

/**
 * The main entry to walk over indexed recipes
 * Return preconfigured reader from the RecipeIndex.
 */
@NullMarked
public class RecipeIndexReader {
    RecipeIndex recipeIndex;

    /**
     * Create a RecipeIndexReader
     * @param recipeIndex the recipe index to read from
     */
    public RecipeIndexReader(RecipeIndex recipeIndex) {
        this.recipeIndex = recipeIndex;
    }

    //#region By Key

    /**
     * Get a MultiProcessRecipeReader by the result of this recipe key
     * @param key the recipe key
     * @return the MultiProcessRecipeReader, or null if not found, or complex recipes
     */
    @Nullable
    public MultiProcessRecipeReader readerByKey(Key key) {
        NavigableMap<Key, Recipe> recipesById = recipeIndex.getAllRecipesById();
        Map<ItemStack, MultiProcessRecipeMap> recipesByResult = recipeIndex.getAllRecipesByResult();
        NavigableMap<Recipe, Process> processByRecipe = recipeIndex.getAllProcessByRecipeMap();
        
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
    public MultiProcessRecipeReader readerByProcess(Process process) {
        NavigableMap<Process, ProcessRecipeSet> recipesByProcess = recipeIndex.getAllRecipesByProcess();
        ProcessRecipeSet processRecipeSet = recipesByProcess.get(process);
        if (processRecipeSet == null) {
            return null;
        }

        MultiProcessRecipeMap singleProcessRecipeMap = new MultiProcessRecipeMap(Collections.singleton(processRecipeSet));
        return new MultiProcessRecipeReader(singleProcessRecipeMap, process);
    }

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
    public MultiProcessRecipeReader readerByProcess(Process process, Recipe startRecipe) {
        MultiProcessRecipeReader reader = readerByProcess(process);
        if (reader != null) {
            reader.getCurrentProcessRecipeReader().setCurrent(startRecipe);
        }
        return reader;
    }

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
    public MultiProcessRecipeReader readerByResult(ItemStack result) {
        Map<ItemStack, MultiProcessRecipeMap> recipesByResult = recipeIndex.getAllRecipesByResult();

        result = result.asOne();
        MultiProcessRecipeMap multiProcessRecipeMap = recipesByResult.get(result);
        if (multiProcessRecipeMap == null) {
            return null;
        }

        return new MultiProcessRecipeReader(multiProcessRecipeMap);
    }

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
    public MultiProcessRecipeReader readerByResult(ItemStack result, Process startProcess) {
        MultiProcessRecipeReader reader = readerByResult(result);
        if (reader != null) {
            reader.setCurrentProcess(startProcess);
        }
        return reader;
    }

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
    public MultiProcessRecipeReader readerByResult(ItemStack result, Process startProcess, Recipe startRecipe) {
        MultiProcessRecipeReader reader = readerByResult(result, startProcess);
        if (reader != null) {
            reader.getCurrentProcessRecipeReader().setCurrent(startRecipe);
        }
        return reader;
    }

    //#endregion By Result

    //#region By Ingredient

    /**
     * Return a MultiProcessRecipeReader for the specified ingredient.
     * All recipes are categorized by their process.
     *
     * @param ingredient the target ingredient
     * @return a MultiProcessRecipeReader for the ingredient, or null if none exist
     */
    @Nullable
    public MultiProcessRecipeReader readerByIngredient(ItemStack ingredient) {
        Map<ItemStack, MultiProcessRecipeMap> recipesByIngredient = recipeIndex.getAllRecipesByIngredient();

        ingredient = ingredient.asOne();
        MultiProcessRecipeMap multiProcessRecipeMap = recipesByIngredient.get(ingredient);
        if (multiProcessRecipeMap == null) {
            return null;
        }

        return new MultiProcessRecipeReader(multiProcessRecipeMap);
    }

    /**
     * Return a MultiProcessRecipeReader for the specified ingredient, starting at the specified process.
     * The reader will contain only recipes for that single process.
     *
     * @param ingredient the target ingredient
     * @param startProcess the target process
     * @return a MultiProcessRecipeReader for the ingredient and process, or null if none exist
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    @Nullable
    public MultiProcessRecipeReader readerByIngredient(ItemStack ingredient, Process startProcess) {
        MultiProcessRecipeReader reader = readerByIngredient(ingredient);
        if (reader != null) {
            reader.setCurrentProcess(startProcess);
        }
        return reader;
    }

    /**
     * Return a MultiProcessRecipeReader for the specified ingredient, starting at the specified process and recipe.
     * The reader will contain only recipes for that single process.
     *
     * @param ingredient the target ingredient
     * @param startProcess the target process
     * @param startRecipe the recipe to start at
     * @return a MultiProcessRecipeReader for the ingredient, process and recipe, or null if none exist
     * @throws IllegalArgumentException if the process or recipe does not exist in the MultiProcessRecipeMap
     */
    @Nullable
    public MultiProcessRecipeReader readerByIngredient(ItemStack ingredient, Process startProcess, Recipe startRecipe) {
        MultiProcessRecipeReader reader = readerByIngredient(ingredient, startProcess);
        if (reader != null) {
            reader.getCurrentProcessRecipeReader().setCurrent(startRecipe);
        }
        return reader;
    }

    //#endregion By Ingredient

    //#region All Recipes

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index starting at the first process.
     * All recipes are categorized by their process.
     *
     * @return a MultiProcessRecipeReader for all recipes
     */
    public MultiProcessRecipeReader readerWithAllRecipes() {
        NavigableMap<Process, ProcessRecipeSet> recipesByProcess = recipeIndex.getAllRecipesByProcess();
        MultiProcessRecipeMap allProcessRecipeMap = new MultiProcessRecipeMap(recipesByProcess.values());
        return new MultiProcessRecipeReader(allProcessRecipeMap);
    }

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index starting at the specified process.
     * All recipes are categorized by their process.
     *
     * @param startProcess the target process
     * @return a MultiProcessRecipeReader for all recipes starting at the specified process
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    public MultiProcessRecipeReader readerWithAllRecipes(Process startProcess) {
        MultiProcessRecipeReader reader = readerWithAllRecipes();
        reader.setCurrentProcess(startProcess);
        return reader;
    }

    /**
     * Return a MultiProcessRecipeReader for all recipes in the index starting at the specified process and recipe.
     * All recipes are categorized by their process.
     *
     * @param process the target process
     * @param startRecipe the recipe to start at
     * @return a MultiProcessRecipeReader for all recipes starting at the specified process and recipe
     * @throws IllegalArgumentException if the process or recipe does not exist in the MultiProcessRecipeMap
     */
    public MultiProcessRecipeReader readerWithAllRecipes(Process startProcess, Recipe startRecipe) {
        MultiProcessRecipeReader reader = readerWithAllRecipes(startProcess);
        reader.getCurrentProcessRecipeReader().setCurrent(startRecipe);
        return reader;
    }

    //#endregion All Recipes

    /**
     * Get a single recipe by its key
     * @param key the recipe key
     * @return the recipe, or null if not found
     */
    @Nullable
    public Recipe getSingleRecipeByKey(Key key) {
        return recipeIndex.getSingleRecipeByKey(key);
    }

    public RecipeExtractor getAssociatedRecipeExtractor() {
        RecipeExtractor extractor = recipeIndex.getAssociatedRecipeExtractor();
        extractor.lock();
        return extractor;
    }

    public ProcessRegistry getAssociatedProcessRegistry() {
        return recipeIndex.getAssociatedProcessRegistry();
    }

    public Set<ItemStack> getAllResultItems() {
        return recipeIndex.getAllRecipesByIngredient().keySet();
    }

    public Set<ItemStack> getAllUsedItems() {
        return recipeIndex.getAllRecipesByIngredient().keySet();
    }
}
