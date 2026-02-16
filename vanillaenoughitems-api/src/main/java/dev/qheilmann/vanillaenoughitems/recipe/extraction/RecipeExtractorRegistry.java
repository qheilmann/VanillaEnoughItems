package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import org.jspecify.annotations.NullMarked;

/**
 * Registry of recipe extractors that delegates extraction to registered strategies.
 * Extends {@link RecipeExtractor} to act as a composite extractor.
 * <p>
 * Register extractors before indexation. Once locked, no new extractors can be registered.
 * </p>
 */
@NullMarked
public interface RecipeExtractorRegistry extends RecipeExtractor {

    /**
     * Register a recipe extractor strategy.
     * Must be called before indexation begins.
     * @param extractor the extractor to register
     * @throws IllegalStateException if the registry is locked (indexation already started)
     */
    void registerExtractor(RecipeExtractorStrategy<?> extractor);
}
