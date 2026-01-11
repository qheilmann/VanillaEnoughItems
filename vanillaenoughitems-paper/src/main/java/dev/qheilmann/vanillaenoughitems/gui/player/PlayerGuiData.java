package dev.qheilmann.vanillaenoughitems.gui.player;

import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.bookmark.Bookmark;
import dev.qheilmann.vanillaenoughitems.bookmark.BookmarkCollection;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;

/**
 * Holds all GUI-related data for a single player.
 * Includes bookmarks, navigation history, and GUI style preferences.
 */
@NullMarked
public class PlayerGuiData {
    private final UUID playerUuid;
    private final BookmarkCollection bookmarks;
    private final RecipeNavigationHistory navigationHistory;

    public PlayerGuiData(UUID playerUuid, RecipeIndex recipeIndex) {
        this.playerUuid = playerUuid;
        this.bookmarks = new BookmarkCollection();
        this.navigationHistory = new RecipeNavigationHistory(playerUuid);
    }

    /**
     * Get the player's UUID
     * @return the player UUID
     */
    public UUID playerUuid() {
        return playerUuid;
    }

    // Bookmark operations - delegate to internal collection

    /**
     * Add a bookmark to the player's collection.
     * @param bookmark the bookmark to add
     * @return true if added, false if already existed
     */
    public boolean addBookmark(Bookmark bookmark) {
        return bookmarks.addBookmark(bookmark);
    }

    /**
     * Remove a bookmark from the player's collection.
     * @param bookmark the bookmark to remove
     * @return true if removed, false if didn't exist
     */
    public boolean removeBookmark(Bookmark bookmark) {
        return bookmarks.removeBookmark(bookmark);
    }

    /**
     * Check if the player has bookmarked a specific bookmark.
     * @param bookmark the bookmark to check
     * @return true if bookmarked
     */
    public boolean containsBookmark(Bookmark bookmark) {
        return bookmarks.contains(bookmark);
    }

    /**
     * Toggle a bookmark in the player's collection.
     * @param bookmark the bookmark to toggle
     * @return true if added, false if removed
     */
    public boolean toggleBookmark(Bookmark bookmark) {
        return bookmarks.toggleBookmark(bookmark);
    }

    /**
     * Get all player bookmarks.
     * @return unmodifiable set of bookmarks
     */
    public Set<Bookmark> getBookmarks() {
        return bookmarks.getBookmarks();
    }

    /**
     * Get the navigation history
     * @return the navigation history
     */
    public RecipeNavigationHistory navigationHistory() {
        return navigationHistory;
    }

    /**
     * Clear all player data
     */
    public void clear() {
        bookmarks.clear();
        navigationHistory.clear();
    }
}
