package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

/**
 * Extract ingredients, results or other items from a recipe
 */
@NullMarked
public interface IRecipeExtractor<R extends @NonNull Recipe> extends Keyed{

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
    default Key extractKey(R recipe){
        if (recipe instanceof Keyed keyed) {
            return keyed.key();
        }
        throw new IllegalArgumentException("Recipe is not Keyed: " + recipe.getClass().getSimpleName());
    }

    /**
     * Extract the ingredients from the given recipe
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the ingredients
     */
    Set<ItemStack> extractIngredients(R recipe);
    
    /**
     * Extract the results from the given recipe
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the results
     */
    default Set<ItemStack> extractResults(R recipe) {
        return Set.of(recipe.getResult());
    }
    
    /**
     * Extract other relevant items from the given recipe (e.g. combustion fuel in a furnace recipe)
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing other relevant items
     */
    Set<ItemStack> extractOthers(R recipe);
}
