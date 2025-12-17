package dev.qheilmann.vanillaenoughitems.gui.player;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;

/**
 * Holds all GUI-related data for a single player.
 * Includes bookmarks, navigation history, and GUI style preferences.
 */
@NullMarked
public class PlayerGuiData {
    private final UUID playerUuid;
    private final BookmarkCollection bookmarkCollection;
    private final RecipeNavigationHistory navigationHistory;
    private final Style style;

    public PlayerGuiData(UUID playerUuid, RecipeExtractor recipeExtractor) {
        this.playerUuid = playerUuid;
        this.bookmarkCollection = new BookmarkCollection(playerUuid, recipeExtractor);
        this.navigationHistory = new RecipeNavigationHistory(playerUuid);
        this.style = new Style();
    }

    /**
     * Get the player's UUID
     * @return the player UUID
     */
    public UUID playerUuid() {
        return playerUuid;
    }

    /**
     * Get the bookmark collection
     * @return the bookmark collection
     */
    public BookmarkCollection bookmarkCollection() {
        return bookmarkCollection;
    }

    /**
     * Get the navigation history
     * @return the navigation history
     */
    public RecipeNavigationHistory navigationHistory() {
        return navigationHistory;
    }

    /**
     * Get the GUI style preferences
     * @return the GUI style
     */
    public Style style() {
        return style;
    }

    /**
     * Clear all player data
     */
    public void clear() {
        bookmarkCollection.clear();
        navigationHistory.clear();
    }
}
