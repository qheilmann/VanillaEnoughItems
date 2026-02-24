package dev.qheilmann.vanillaenoughitems.bookmark;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

/**
 * Registry for server-wide bookmarks.
 * These bookmarks are globally shared across all players and controlled by server administrators.
 * Duplicates are prevented via {@link Bookmark} equality comparison.
 */
@NullMarked
public interface ServerBookmarkRegistry {

    /**
     * Add a bookmark to the registry.
     * Duplicates (based on Bookmark.equals()) are automatically prevented.
     * @param bookmark the bookmark to add
     * @return true if the bookmark was added, false if it already existed
     */
    boolean addBookmark(Bookmark bookmark);

    /**
     * Remove a bookmark from the registry.
     * @param bookmark the bookmark to remove
     * @return true if the bookmark was removed, false if it didn't exist
     */
    boolean removeBookmark(Bookmark bookmark);

    /**
     * Clear all bookmarks from the registry.
     */
    void clear();

    /**
     * Get an unmodifiable view of all bookmarks.
     * @return unmodifiable set of all bookmarks
     */
    Set<Bookmark> getBookmarks();

    /**
     * Check if the registry is empty.
     * @return true if no bookmarks exist
     */
    boolean isEmpty();
}
