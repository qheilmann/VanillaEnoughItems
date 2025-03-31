package me.qheilmann.vei.Core.Recipe.Index;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import net.kyori.adventure.key.Key;

public class RecipeIndexService {
    private final ConcurrentSkipListMap<NamespacedKey, Recipe> recipesById;
    private final ConcurrentHashMap<ItemStack, MixedProcessRecipeMap> recipesByResult;
    private final ConcurrentHashMap<ItemStack, MixedProcessRecipeMap> recipesByIngredient;
    private final ConcurrentSkipListMap<Process<?>, ProcessRecipeSet<?>> recipesByProcess;

    public RecipeIndexService() {
        // Initialize the maps
        recipesById = new ConcurrentSkipListMap<>(Key.comparator());
        recipesByResult = new ConcurrentHashMap<>(); // ItemStack#hashcode are not really reliable between sessions, at least we regenerate them each time
        recipesByIngredient = new ConcurrentHashMap<>();
        recipesByProcess = new ConcurrentSkipListMap<>(MixedProcessRecipeMap.PROCESS_COMPARATOR);
    }

    //#region Indexing / Unindexing

    /**
     * Indexes a collection of recipes.
     * <p>
     * @see {@link #IndexRecipes(JavaPlugin)}: Preferred method to index all recipes in the server.
     * @param recipes The collection of recipes to index.
     */
    public void IndexRecipes(Collection<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            IndexRecipe(recipe);
        }
    }

    /**
     * Indexes all recipes save in the server associated with the plugin.
     * <p>
     * This is the preferred default method to index recipes
     * @param plugin The plugin instance
     */
    public void IndexRecipes(JavaPlugin plugin) {
        Iterator<Recipe> recipeIterator = plugin.getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            IndexRecipe(recipe);
        }        
    }

    /**
     * Indexes a recipe.
     * @param recipe The recipe to index.
     */
    public void IndexRecipe(Recipe recipe) {
        // Index by ID
        if (recipe instanceof org.bukkit.Keyed keyedRecipe) {
            recipesById.put(keyedRecipe.getKey(), recipe);
        }

        // Index by result
        recipesByResult.computeIfAbsent(recipe.getResult(), p -> new MixedProcessRecipeMap()).addRecipe(recipe);

        // Index by needed ingredients
        // for (ItemStack ingredient : recipe.<A way to get needed items from a recipe>) {
        //     recipesByIngredient.computeIfAbsent(needed, k -> new ArrayList<>()).add(recipe);
        // }

        // Index by process
        Process<?> process = Process.ProcessRegistry.getProcesseByRecipe(recipe);
        recipesByProcess.computeIfAbsent(process, p -> new ProcessRecipeSet<>()).unsafeAdd(recipe);
    }

    public void UnindexRecipes(Collection<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            UnindexRecipe(recipe);
        }
    }

    public void UnindexRecipes(JavaPlugin plugin) {
        Iterator<Recipe> recipeIterator = plugin.getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            UnindexRecipe(recipe);
        }
    }

    public void UnindexRecipe(Recipe recipe) {
        // Remove from ID index
        if (recipe instanceof org.bukkit.Keyed keyedRecipe) {
            recipesById.remove(keyedRecipe.getKey());
        }

        // Remove from result index
        ItemStack result = recipe.getResult();
        MixedProcessRecipeMap map = recipesByResult.get(result);
        if (map != null) {
            map.removeRecipe(recipe);

            // Also remove the map if no recipes anymore
            if (map.isEmpty()) {
                recipesByResult.remove(result);
            }
        }

        // Remove from needed ingredients index
        // for (ItemStack needed : recipe.<A way to get needed items from a recipe>) {
        //     recipesByIngredient.getOrDefault(needed, Collections.emptyList()).remove(recipe);
        // }

        // Remove from process index
        Process<?> process = Process.ProcessRegistry.getProcesseByRecipe(recipe);
        ProcessRecipeSet<?> processRecipeSet = recipesByProcess.get(process);
        if (processRecipeSet != null) {
            processRecipeSet.unsafeRemove(recipe);

            // Also remove the process if it has no recipes anymore
            if (processRecipeSet.isEmpty()) {
                recipesByProcess.remove(process);
            }
        }
    }

    public void UnindexAll() {
        recipesById.clear();
        recipesByResult.clear();
        recipesByIngredient.clear();
        recipesByProcess.clear();
    }

    //#endregion Indexing / Unindexing

    //#region Searching

    public SequencedSet<NamespacedKey> getAllRecipeIds() {
        return Collections.unmodifiableSequencedSet(recipesById.keySet());
    }

    public Recipe getById(NamespacedKey recipeId) {
        return recipesById.get(recipeId);
    }

    public MixedProcessRecipeReader getByResult(ItemStack item) {
        return new MixedProcessRecipeReader(recipesByResult.get(item));
    }

    public MixedProcessRecipeReader getByIngredient(ItemStack item) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @SuppressWarnings("unchecked")
    public <R extends Recipe> ProcessRecipeReader<R> getByProcess(Process<R> process) {
        return (ProcessRecipeReader<R>) new ProcessRecipeReader<>(recipesByProcess.getOrDefault(process, new ProcessRecipeSet<>()));
    }

    //#endregion Searching
}
