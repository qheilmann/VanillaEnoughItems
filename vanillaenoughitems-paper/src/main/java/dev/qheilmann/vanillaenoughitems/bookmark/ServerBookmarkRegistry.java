package dev.qheilmann.vanillaenoughitems.bookmark;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

/**
 * Registry for server-wide bookmarks.
 * These bookmarks are globally shared across all players and controlled by server administrators.
 * Duplicates are prevented via Bookmark.equals() comparison.
 */
@NullMarked
public class ServerBookmarkRegistry {

    private final BookmarkCollection bookmarks = new BookmarkCollection();

    /**
     * Add a bookmark to the registry.
     * Duplicates (based on Bookmark.equals()) are automatically prevented.
     * @param bookmark the bookmark to add
     * @return true if the bookmark was added, false if it already existed
     */
    public boolean addBookmark(Bookmark bookmark) {
        return bookmarks.addBookmark(bookmark);
    }

    /**
     * Remove a bookmark from the registry.
     * @param bookmark the bookmark to remove
     * @return true if the bookmark was removed, false if it didn't exist
     */
    public boolean removeBookmark(Bookmark bookmark) {
        return bookmarks.removeBookmark(bookmark);
    }

    /**
     * Clear all bookmarks from the registry.
     */
    public void clear() {
        bookmarks.clear();
    }

    /**
     * Get an unmodifiable view of all bookmarks.
     * @return unmodifiable set of all bookmarks
     */
    public Set<Bookmark> getBookmarks() {
        return bookmarks.getBookmarks();
    }

    /**
     * Check if the registry is empty.
     * @return true if no bookmarks exist
     */
    public boolean isEmpty() {
        return bookmarks.getBookmarkCount() == 0;
    }
}
