package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import net.kyori.adventure.key.Key;

@NullMarked
public interface RecipeExtractor {

    /**
     * Check if any registered extractor can handle the given recipe
     * @param recipe the recipe to check
     * @return true if any extractor can handle the recipe, false otherwise
     */
    public boolean canHandle(Recipe recipe);

    /**
     * Get the key of this recipe with the first extractor (ordered by extractor key {@link Key#comparator()}
     * <p> Note: not all recipes are Keyed, the extractor can still provide a custom implementation of key for those recipes </p>
     * @param recipe the recipe to get the key from
     * @return the key of this recipe
     * @throws IllegalArgumentException if no extractor could handle the recipe, first check with {@link #canHandle(Recipe)} if needed
     */
    public Key extractKey(Recipe recipe);

    /**
     * Extract the ingredients from the given recipe with the first extractor (ordered by extractor key {@link Key#comparator()}
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the ingredients, or an empty set if no extractor could handle the recipe
     * @throws IllegalArgumentException if no extractor could handle the recipe, first check with {@link #canHandle(Recipe)} if needed
     */
    public Set<ItemStack> extractIngredients(Recipe recipe);

    /**
     * Extract the results from the given recipe with the first extractor (ordered by extractor key {@link Key#comparator()}
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the results
     * @throws IllegalArgumentException if no extractor could handle the recipe, first check with {@link #canHandle(Recipe)} if needed
     */
    public Set<ItemStack> extractResults(Recipe recipe); 

    /**
     * Extract other relevant items (e.g. combustion fuel in a furnace recipe) from the given recipe with the first extractor (ordered by extractor key {@link Key#comparator()}
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing other relevant items
     * @throws IllegalArgumentException if no extractor could handle the recipe, first check with {@link #canHandle(Recipe)} if needed
     */
    public Set<ItemStack> extractOthers(Recipe recipe);
}
