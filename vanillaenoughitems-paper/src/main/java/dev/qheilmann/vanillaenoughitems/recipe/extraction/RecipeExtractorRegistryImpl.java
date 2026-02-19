package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import java.util.LinkedHashMap;
import java.util.Set;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.kyori.adventure.key.Key;

/**
 * Registry implementation that delegates extraction to registered extractors
 */
@NullMarked
public class RecipeExtractorRegistryImpl implements RecipeExtractorRegistry {
    
    // Map of registered extractors by key, ordered by insertion order
    LinkedHashMap<Key, RecipeExtractor> extractors = new LinkedHashMap<>();

    boolean locked = false;
    
    // Cache for last-used extractor (optimization for repeated calls on same recipe)
    private @Nullable Recipe lastRecipe;
    private @Nullable RecipeExtractor lastExtractor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerExtractor(RecipeExtractor extractor) {
        if (locked) {
            throw new IllegalStateException("RecipeExtractor is locked, cannot register new extractors");
        }
        this.extractors.put(extractor.key(), extractor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipeExtractor unregisterExtractor(Key key) {
        if (locked) {
            throw new IllegalStateException("RecipeExtractor is locked, cannot unregister extractors");
        }
        return this.extractors.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public RecipeExtractor getExtractor(Key key) {
        return extractors.get(key);
    }

    /**
     * Find the extractor that can handle the given recipe.
     * Uses a simple cache for performance when processing the same recipe repeatedly.
     * @param recipe the recipe to find an extractor for
     * @return the extractor that can handle the recipe
     * @throws IllegalArgumentException if no extractor can handle the recipe
     */
    private RecipeExtractor findExtractor(Recipe recipe) {
        // Fast path: check if last extractor still works (same recipe reference)
        RecipeExtractor cached = lastExtractor;
        if (cached != null && lastRecipe == recipe && cached.canHandle(recipe)) {
            return cached;
        }
        
        // Slow path: search for extractor
        for (RecipeExtractor extractor : extractors.values()) {
            if (extractor.canHandle(recipe)) {
                // Cache for next call
                lastRecipe = recipe;
                lastExtractor = extractor;
                return extractor;
            }
        }
        
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Recipe recipe) {
        // Fast path check
        RecipeExtractor cached = lastExtractor;
        if (cached != null && lastRecipe == recipe && cached.canHandle(recipe)) {
            return true;
        }
        
        for (RecipeExtractor extractor : extractors.values()) {
            if (extractor.canHandle(recipe)) {
                lastRecipe = recipe;
                lastExtractor = extractor;
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Key extractKey(Recipe recipe) {
        return findExtractor(recipe).extractKey(recipe);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        return findExtractor(recipe).extractIngredients(recipe);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ItemStack> extractResults(Recipe recipe) {
        return findExtractor(recipe).extractResults(recipe);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return findExtractor(recipe).extractOthers(recipe);
    }
}
