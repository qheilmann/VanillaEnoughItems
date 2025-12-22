package dev.qheilmann.vanillaenoughitems.recipe.index;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeHelper;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.RecipeIndexView;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

/**
 * Index all recipes by different criteria.
 * usage, result, process, id...
 */
@NullMarked
public class RecipeIndex implements RecipeIndexView {
    
    private final ProcessRegistry processRegistry;
    private final RecipeExtractor recipeExtractor;
    
    // Search indexes
    private final ConcurrentSkipListMap<Key, Recipe> recipesById = new ConcurrentSkipListMap<>(Key.comparator());
    private final MultiProcessRecipeMap recipesByProcess = new MultiProcessRecipeMap();
    // ItemStack#hashcode are not really reliable between sessions, at least we regenerate them each time
    private final ConcurrentHashMap<ItemStack, MultiProcessRecipeMap> recipesByResult = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ItemStack, MultiProcessRecipeMap> recipesByUsage = new ConcurrentHashMap<>();

    // Inverse index for fast lookup
    /**
     * Store the first compatible process for a recipe depending {@link Process#COMPARATOR}.
     */
    private final ConcurrentSkipListMap<Recipe, Process> processByRecipe = new ConcurrentSkipListMap<>(RecipeHelper.RECIPE_COMPARATOR);

    /**
     * Create an empty RecipeIndex
     */
    public RecipeIndex(ProcessRegistry processRegistry, RecipeExtractor recipeExtractor) {
        this.processRegistry = processRegistry;
        this.recipeExtractor = recipeExtractor;
    }

    //#region Indexing

    /**
     * Index multiple recipes with an iterable.
     * You can use a lambda {@code () -> iterator} to consume an iterator instance
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

        if (!recipeExtractor.canHandle(recipe)) {
            // TODO log an optional warning about unhandled recipe
            String key = (recipe instanceof Keyed) ? ((Keyed) recipe).key().asString() : "no key available";
            VanillaEnoughItems.LOGGER.warn("No extractor found for recipe: " + recipe.getClass().getSimpleName() + " (" + key + ")");
            return;
        }

        // Index by id
        Key recipeKey = recipeExtractor.extractKey(recipe);
        recipesById.put(recipeKey, recipe);

        // Index by process
        Process process = processRegistry.getRecipeProcess(recipe);
        processByRecipe.put(recipe, process);
        recipesByProcess.addRecipe(process, recipe);

        // Index by result
        Set<ItemStack> results = recipeExtractor.extractResults(recipe);
        for (ItemStack result : results) {
            result = result.asOne(); // Normalize to amount 1 for indexing
            MultiProcessRecipeMap multiProcessRecipeMap = recipesByResult.computeIfAbsent(result, r -> new MultiProcessRecipeMap());
            multiProcessRecipeMap.addRecipe(process, recipe);
        }

        // Index by usage (ingredient and others)
        Set<ItemStack> ingredients = recipeExtractor.extractIngredients(recipe);
        for (ItemStack ingredient : ingredients) {
            ingredient = ingredient.asOne(); // Normalize to amount 1 for indexing
            MultiProcessRecipeMap multiProcessRecipeMap = recipesByUsage.computeIfAbsent(ingredient, i -> new MultiProcessRecipeMap());
            multiProcessRecipeMap.addRecipe(process, recipe);
        }
        Set<ItemStack> others = recipeExtractor.extractOthers(recipe);
        for (ItemStack other : others) {
            other = other.asOne(); // Normalize to amount 1 for indexing
            MultiProcessRecipeMap multiProcessRecipeMap = recipesByUsage.computeIfAbsent(other, o -> new MultiProcessRecipeMap());
            multiProcessRecipeMap.addRecipe(process, recipe);
        }
    }

    /**
     * Unindex multiple recipes with an iterable.
     * You can use a lambda {@code () -> iterator} to consume an iterator instance
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
        // Unindex by id
        Key recipeKey = recipeExtractor.extractKey(recipe);
        recipesById.remove(recipeKey);

        // Unindex by process
        Process process = processByRecipe.remove(recipe);
        if (process != null) {
            recipesByProcess.removeRecipe(process,  recipe);
        }

        // Unindex by result
        Set<ItemStack> results = recipeExtractor.extractResults(recipe);
        for (ItemStack result : results) {
            result = result.asOne();
            MultiProcessRecipeMap multiProcessRecipeMap = recipesByResult.get(result);
            if (multiProcessRecipeMap != null && process != null) {
                multiProcessRecipeMap.removeRecipe(process, recipe);
            }
        }

        // Unindex by usage (ingredient and others)
        Set<ItemStack> ingredients = recipeExtractor.extractIngredients(recipe);
        for (ItemStack ingredient : ingredients) {
            ingredient = ingredient.asOne();
            MultiProcessRecipeMap multiProcessRecipeMap = recipesByUsage.get(ingredient);
            if (multiProcessRecipeMap != null && process != null) {
                multiProcessRecipeMap.removeRecipe(process, recipe);
            }
        }
        Set<ItemStack> others = recipeExtractor.extractOthers(recipe);
        for (ItemStack other : others) {
            other = other.asOne();
            MultiProcessRecipeMap multiProcessRecipeMap = recipesByUsage.get(other);
            if (multiProcessRecipeMap != null && process != null) {
                multiProcessRecipeMap.removeRecipe(process, recipe);
            }
        }
    }

    /**
     * Clear the entire index
     */
    public void clearIndex() {
        recipesById.clear();
        recipesByProcess.clear();
        recipesByResult.clear();
        recipesByUsage.clear();
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
     * Get an unmodifiable view of all recipes by their usage.
     * <p>
     * <b>Warning:</b> The returned map is unmodifiable, but the {@link MultiProcessRecipeMap} values
     * within are <b>NOT</b> immutable. Do not modify the inner maps or recipes as this will break
     * the index consistency. Use {@link #readerByUsage(ItemStack)} for safe read-only access.
     * <p>
     * This method is intended for internal use or advanced use cases where you need direct access
     * to the underlying structure.
     *
     * @return unmodifiable view of all recipes by their usage
     */
    public Map<ItemStack, MultiProcessRecipeMap> getAllRecipesByUsage() {
        return Collections.unmodifiableMap(recipesByUsage);
    }

    //#endregion Exporting

    public NavigableMap<Recipe, Process> getAllProcessByRecipeMap() {
        return Collections.unmodifiableNavigableMap(processByRecipe);
    }

    public Set<ItemStack> getAllResultItems() {
        return Collections.unmodifiableSet(recipesByResult.keySet());
    }

    public Set<ItemStack> getAllUsedItems() {
        return Collections.unmodifiableSet(recipesByUsage.keySet());
    }

    /**
     * Get the associated RecipeExtractor
     * @return the recipe extractor
     */
    public RecipeExtractor getAssociatedRecipeExtractor() {
        return recipeExtractor;
    }

    /**
     * Get the associated ProcessRegistry
     * @return the process registry
     */
    public ProcessRegistry getAssociatedProcessRegistry() {
        return processRegistry;
    }

    //#region RecipeIndexView
    //#region By Key

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Recipe getSingleRecipeByKey(Key key) {
        return recipesById.get(key);
    }

    //#endregion By Key

    //#region By Process

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public MultiProcessRecipeReader readerByResult(ItemStack result) {
        result = result.asOne();
        MultiProcessRecipeMap multiProcessRecipeMap = recipesByResult.get(result);
        if (multiProcessRecipeMap == null) {
            return null;
        }

        return new MultiProcessRecipeReader(multiProcessRecipeMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public MultiProcessRecipeReader readerByResult(ItemStack result, Process startProcess) {
        MultiProcessRecipeReader reader = readerByResult(result);
        if (reader != null) {
            reader.setCurrentProcess(startProcess);
        }
        return reader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public MultiProcessRecipeReader readerByResult(ItemStack result, Process startProcess, Recipe startRecipe) {
        MultiProcessRecipeReader reader = readerByResult(result, startProcess);
        if (reader != null) {
            reader.getCurrentProcessRecipeReader().setCurrent(startRecipe);
        }
        return reader;
    }

    //#endregion By Result

    //#region By Usage

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public MultiProcessRecipeReader readerByUsage(ItemStack item) {
        item = item.asOne();
        MultiProcessRecipeMap multiProcessRecipeMap = recipesByUsage.get(item);
        if (multiProcessRecipeMap == null) {
            return null;
        }

        return new MultiProcessRecipeReader(multiProcessRecipeMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public MultiProcessRecipeReader readerByUsage(ItemStack item, Process startProcess) {
        MultiProcessRecipeReader reader = readerByUsage(item);
        if (reader != null) {
            reader.setCurrentProcess(startProcess);
        }
        return reader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public MultiProcessRecipeReader readerByUsage(ItemStack item, Process startProcess, Recipe startRecipe) {
        MultiProcessRecipeReader reader = readerByUsage(item, startProcess);
        if (reader != null) {
            reader.getCurrentProcessRecipeReader().setCurrent(startRecipe);
        }
        return reader;
    }

    //#endregion By Usage

    //#region All Recipes

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiProcessRecipeReader readerWithAllRecipes() {
        MultiProcessRecipeMap allProcessRecipeMap = new MultiProcessRecipeMap(recipesByProcess.getAllProcessRecipeSets().values());
        return new MultiProcessRecipeReader(allProcessRecipeMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiProcessRecipeReader readerWithAllRecipes(Process startProcess) {
        MultiProcessRecipeReader reader = readerWithAllRecipes();
        reader.setCurrentProcess(startProcess);
        return reader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiProcessRecipeReader readerWithAllRecipes(Process startProcess, Recipe startRecipe) {
        MultiProcessRecipeReader reader = readerWithAllRecipes(startProcess);
        reader.getCurrentProcessRecipeReader().setCurrent(startRecipe);
        return reader;
    }
    
    //#endregion All Recipes
    //#endregion RecipeIndexView

    //#region Summary Logging

    public void logSummary() {
        VanillaEnoughItems.LOGGER.info("==================== Recipe Index Summary ====================");
        
        // Overall totals
        int totalRecipes = recipesById.size();
        int totalProcesses = recipesByProcess.getAllProcesses().size();
        int totalResultTypes = recipesByResult.size();
        int totalUsedTypes = recipesByUsage.size();
        
        VanillaEnoughItems.LOGGER.info("Total Recipes Indexed: " + totalRecipes);
        VanillaEnoughItems.LOGGER.info("Total Processes: " + totalProcesses);
        VanillaEnoughItems.LOGGER.info("Total Unique Result Types: " + totalResultTypes);
        VanillaEnoughItems.LOGGER.info("Total Unique Used Types: " + totalUsedTypes);
        
        // Per-process breakdown
        VanillaEnoughItems.LOGGER.info("---------- Recipes by Process ----------");
        NavigableMap<Process, ProcessRecipeSet> processMap = recipesByProcess.getAllProcessRecipeSets();
        for (Map.Entry<Process, ProcessRecipeSet> entry : processMap.entrySet()) {
            Process process = entry.getKey();
            ProcessRecipeSet recipeSet = entry.getValue();
            int recipeCount = recipeSet.size();
            
            String processKey = process.key().asString();
            
            VanillaEnoughItems.LOGGER.info("  " + processKey + ": " + recipeCount + " recipes");
            
            // Show first 5 recipes for this process
            int count = 0;
            for (Recipe recipe : recipeSet.getRecipes()) {
                if (count >= 5) break;
                
                Key recipeKey = recipeExtractor.extractKey(recipe);
                ItemStack result = recipe.getResult();
                String itemType = result.getType().name();
                
                VanillaEnoughItems.LOGGER.info("    - " + recipeKey.asString() + " -> " + itemType + " x" + result.getAmount());
                count++;
            }
            
            if (recipeCount > 5) {
                VanillaEnoughItems.LOGGER.info("    ... and " + (recipeCount - 5) + " more");
            }
        }
        
        // Results index summary
        VanillaEnoughItems.LOGGER.info("---------- Recipes by Result (Top 5) ----------");
        recipesByResult.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(
                e2.getValue().getAllRecipes().size(), 
                e1.getValue().getAllRecipes().size()
            ))
            .limit(5)
            .forEach(entry -> {
                ItemStack result = entry.getKey();
                MultiProcessRecipeMap recipes = entry.getValue();
                int recipeCount = recipes.getAllRecipes().size();
                int processCount = recipes.getAllProcesses().size();
                
                String resultName = result.getType().name();
                VanillaEnoughItems.LOGGER.info("  " + resultName + ": " + recipeCount + " recipes across " + processCount + " processes");
                
                // Show process breakdown for this result
                for (Process process : recipes.getAllProcesses()) {
                    ProcessRecipeSet processRecipes = recipes.getProcessRecipeSet(process);
                    if (processRecipes != null) {
                        VanillaEnoughItems.LOGGER.info("    - " + process.key() + " -> " + processRecipes.size() + " recipes");
                    }
                }
            });
        
        // Usage index summary
        VanillaEnoughItems.LOGGER.info("---------- Recipes by Used Item (Top 5) ----------");
        recipesByUsage.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(
                e2.getValue().getAllRecipes().size(), 
                e1.getValue().getAllRecipes().size()
            ))
            .limit(5)
            .forEach(entry -> {
                ItemStack ingredient = entry.getKey();
                MultiProcessRecipeMap recipes = entry.getValue();
                int recipeCount = recipes.getAllRecipes().size();
                int processCount = recipes.getAllProcesses().size();
                
                String ingredientType = ingredient.getType().name();
                VanillaEnoughItems.LOGGER.info("  " + ingredientType + ": " + recipeCount + " recipes across " + processCount + " processes");
                
                // Show process breakdown for this used item
                for (Process process : recipes.getAllProcesses()) {
                    ProcessRecipeSet processRecipes = recipes.getProcessRecipeSet(process);
                    if (processRecipes != null) {
                        VanillaEnoughItems.LOGGER.info("    - " + process.key() + " -> " + processRecipes.size() + " recipes");
                    }
                }
            });
        
        VanillaEnoughItems.LOGGER.info("==============================================================");
    }

    //#endregion Summary Logging
}
