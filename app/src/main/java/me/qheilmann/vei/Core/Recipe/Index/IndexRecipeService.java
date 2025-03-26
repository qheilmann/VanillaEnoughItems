package me.qheilmann.vei.Core.Recipe.Index;

import java.util.*;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import me.qheilmann.vei.Core.Utils.NotNullMap;
import me.qheilmann.vei.Core.Process.Process;

public class IndexRecipeService {
    private final NotNullMap<NamespacedKey, Recipe> recipesById = new NotNullMap<>(new HashMap<>());
    private final NotNullMap<ItemStack, MixedProcessRecipeMap> recipesByResult = new NotNullMap<>(new HashMap<>());
    @SuppressWarnings("unused") // TODO: Implement recipesByIngredient index
    private final NotNullMap<ItemStack, MixedProcessRecipeMap> recipesByIngredient = new NotNullMap<>(new HashMap<>());
    private final NotNullMap<Process<?>, ProcessRecipeSet<?>> recipesByProcess = new NotNullMap<>(new HashMap<>());

    public void addRecipe(Recipe recipe) {
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

    public void removeRecipe(Recipe recipe) {
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

    public Recipe getRecipeById(NamespacedKey recipeId) {
        return recipesById.get(recipeId);
    }

    public SequencedSet<Recipe> getAllByResult(ItemStack item) {
        return Collections.unmodifiableSequencedSet(recipesByResult.getOrDefault(item, new MixedProcessRecipeMap()).getAllRecipes());
    }

    public SequencedSet<Recipe> getAllByIngredient(ItemStack item) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @SuppressWarnings("unchecked")
    public <R extends Recipe> SequencedSet<R> getRecipesByProcess(Process<R> process) {
        return (SequencedSet<R>) recipesByProcess.getOrDefault(process, new ProcessRecipeSet<>()).getAllRecipes();
    }
}
