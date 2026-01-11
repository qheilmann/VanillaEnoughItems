package dev.qheilmann.vanillaenoughitems;

import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.bookmark.ServerBookmarkRegistry;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import dev.qheilmann.vanillaenoughitems.recipe.index.TagIndex;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;

/**
 * Immutable container for recipe system services.
 * This is a simple data holder that bundles commonly-used services together
 * for convenience when passing to complex components like GUIs and commands.
 * 
 * <p>Use this when you need to pass multiple services together.
 * For simpler use cases, prefer dependency injection of individual services.
 */
@NullMarked
public record RecipeServices(
    RecipeExtractorRegistry recipeExtractor,
    ProcessRegistry processRegistry,
    ProcessPanelRegistry processPanelRegistry,
    RecipeIndex recipeIndex,
    TagIndex tagIndex,
    ServerBookmarkRegistry serverBookmarkRegistry
) {
    // Pure data holder - no methods needed
}
