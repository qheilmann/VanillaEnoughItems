package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.kyori.adventure.key.Key;

/**
 * Registry of recipe extractors that delegates extraction to registered strategies.
 * <p>
 * Register extractors before indexation. Once locked, no new extractors can be registered.
 * Keys are unique across all registered extractors.
 * </p>
 */
@NullMarked
public interface RecipeExtractorRegistry {

    /**
     * Register a recipe extractor.
     * Must be called before indexation begins.
     * If an extractor with the same key already exists, it will be replaced.
     * @param extractor the extractor to register
     * @throws IllegalStateException if the registry is locked (indexation already started)
     */
    void registerExtractor(RecipeExtractor extractor);

    /**
     * Unregister a recipe extractor by its key.
     * @param key the key of the extractor to unregister
     * @return the removed extractor, or null if no extractor was registered with the given key
     * @throws IllegalStateException if the registry is locked (indexation already started)
     */
    RecipeExtractor unregisterExtractor(Key key);
    
    /**
     * Get a registered extractor by its key.
     * @param key the key of the extractor
     * @return the extractor, or null if not found
     */
    @Nullable
    RecipeExtractor getExtractor(Key key);

    /**
     * Check if any registered extractor can handle the given recipe.
     * @param recipe the recipe to check
     * @return true if an extractor can handle the recipe, false otherwise
     */
    boolean canHandle(Recipe recipe);

    /**
     * Extract the key from the given recipe using the first compatible extractor.
     * @param recipe the recipe to extract from
     * @return the key of the recipe
     * @throws IllegalArgumentException if no extractor can handle the recipe
     */
    Key extractKey(Recipe recipe);

    /**
     * Extract the ingredients from the given recipe using the first compatible extractor.
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the ingredients
     * @throws IllegalArgumentException if no extractor can handle the recipe
     */
    Set<ItemStack> extractIngredients(Recipe recipe);

    /**
     * Extract the results from the given recipe using the first compatible extractor.
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the results
     * @throws IllegalArgumentException if no extractor can handle the recipe
     */
    Set<ItemStack> extractResults(Recipe recipe);

    /**
     * Extract other relevant items from the given recipe using the first compatible extractor.
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing other relevant items
     * @throws IllegalArgumentException if no extractor can handle the recipe
     */
    Set<ItemStack> extractOthers(Recipe recipe);
}
