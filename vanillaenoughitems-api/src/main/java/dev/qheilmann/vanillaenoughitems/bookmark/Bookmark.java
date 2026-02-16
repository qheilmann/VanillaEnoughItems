package dev.qheilmann.vanillaenoughitems.bookmark;

import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;

/**
 * Represents a bookmark containing recipe data and a visual symbol.
 * <p>
 * Equality is based on recipe content - bookmarks with the same recipes
 * are considered duplicates regardless of defaults/symbols.
 * </p>
 */
@NullMarked
public interface Bookmark {

    /**
     * Get a copy of the reader for this bookmark.
     * Returns a new independent reader so the GUI can modify it without affecting the bookmark.
     * @return a copy of the reader
     */
    MultiProcessRecipeReader getReader();

    /**
     * Get the visual symbol for this bookmark.
     * @return the symbol as a CyclicIngredient
     */
    CyclicIngredient getSymbol();
}
