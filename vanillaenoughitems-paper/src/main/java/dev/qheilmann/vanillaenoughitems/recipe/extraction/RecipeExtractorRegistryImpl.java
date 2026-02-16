package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import net.kyori.adventure.key.Key;

/**
 * Extract any recipe with registered IRecipeExtractor
 */
@NullMarked
public class RecipeExtractorRegistryImpl implements RecipeExtractorRegistry {
    
    // Map of registered extractors ordered by insertion order
    LinkedHashSet<RecipeExtractorStrategy<?>> extractors = new LinkedHashSet<>();

    boolean locked = false;

    /**
     * Create a new RecipeExtractorRegistryImpl
     */
    public RecipeExtractorRegistryImpl() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerExtractor(RecipeExtractorStrategy<?> extractor) {
        if (locked) {
            throw new IllegalStateException("RecipeExtractor is locked, cannot register new extractors");
        }
        this.extractors.add(extractor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Recipe recipe) {
        for (RecipeExtractorStrategy<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
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
        for (RecipeExtractorStrategy<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                RecipeExtractorStrategy<@NonNull Recipe> typedExtractor = (RecipeExtractorStrategy<@NonNull Recipe>) extractor;
                return typedExtractor.extractKey(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        for (RecipeExtractorStrategy<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                RecipeExtractorStrategy<@NonNull Recipe> typedExtractor = (RecipeExtractorStrategy<@NonNull Recipe>) extractor;
                return typedExtractor.extractIngredients(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ItemStack> extractResults(Recipe recipe) {
        for (RecipeExtractorStrategy<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                RecipeExtractorStrategy<@NonNull Recipe> typedExtractor = (RecipeExtractorStrategy<@NonNull Recipe>) extractor;
                return typedExtractor.extractResults(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        for (RecipeExtractorStrategy<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                RecipeExtractorStrategy<@NonNull Recipe> typedExtractor = (RecipeExtractorStrategy<@NonNull Recipe>) extractor;
                return typedExtractor.extractOthers(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }
}
