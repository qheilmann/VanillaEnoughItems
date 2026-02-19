package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

/**
 * Strategy for extracting ingredients, results, and other items from a recipe.
 * <p>
 * Implementations handle specific recipe types (e.g., ShapelessRecipe, FurnaceRecipe).
 * The registry uses the composite pattern to delegate to registered extractors.
 * </p>
 * <p>
 * Implementations should check the recipe type with {@link #canHandle(Recipe)} before extraction.
 * Type casting is expected after the canHandle check ensures type safety.
 * </p>
 */
@NullMarked
public interface RecipeExtractor extends Keyed {

    /**
     * Check if this extractor can handle the given recipe
     * @param recipe the recipe to check
     * @return true if this extractor can handle the recipe, false otherwise
     */
    boolean canHandle(Recipe recipe);
    
    /**
     * Get the key of this recipe
     * @param recipe the recipe to get the key from
     * @return the key of this recipe
     * @throws IllegalArgumentException if the recipe is not Keyed
     */
    default Key extractKey(Recipe recipe){
        if (recipe instanceof Keyed keyed) {
            return keyed.key();
        }
        throw new UnsupportedOperationException("This extractor does not implement custom key extraction for non-Keyed recipes:" + recipe.getClass().getName());
    }

    /**
     * Extract the ingredients from the given recipe
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the ingredients
     */
    Set<ItemStack> extractIngredients(Recipe recipe);
    
    /**
     * Extract the results from the given recipe
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the results
     */
    default Set<ItemStack> extractResults(Recipe recipe) {
        return Set.of(recipe.getResult());
    }
    
    /**
     * Extract other relevant items from the given recipe (e.g. combustion fuel in a furnace recipe)
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing other relevant items
     */
    Set<ItemStack> extractOthers(Recipe recipe);
}
