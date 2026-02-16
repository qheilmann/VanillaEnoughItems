package dev.qheilmann.vanillaenoughitems.bookmark;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.NullMarked;

/**
 * Core bookmark collection providing common CRUD operations.
 * Thread-safe using ConcurrentHashMap backing.
 */
@NullMarked
public class BookmarkCollection {
    private final Set<Bookmark> bookmarks;

    /**
     * Create a new BookmarkCollection
     */
    public BookmarkCollection() {
        this.bookmarks = ConcurrentHashMap.newKeySet();
    }

    /**
     * Add a bookmark to the collection.
     * Duplicates are automatically prevented via Bookmark.equals().
     * @param bookmark the bookmark to add
     * @return true if added, false if already existed
     */
    public boolean addBookmark(Bookmark bookmark) {
        return bookmarks.add(bookmark);
    }

    /**
     * Remove a bookmark from the collection.
     * @param bookmark the bookmark to remove
     * @return true if removed, false if didn't exist
     */
    public boolean removeBookmark(Bookmark bookmark) {
        return bookmarks.remove(bookmark);
    }

    /**
     * Check if the collection contains a specific bookmark
     * @param bookmark the bookmark
     * @return true if contained
     */
    public boolean contains(Bookmark bookmark) {
        return bookmarks.contains(bookmark);
    }

    /**
     * Toggle a bookmark in the collection.
     * If it exists, it will be removed. If it doesn't exist, it will be added.
     * @param bookmark the bookmark to toggle
     * @return true if added, false if removed
     */
    public boolean toggleBookmark(Bookmark bookmark) {
        if (bookmarks.contains(bookmark)) {
            bookmarks.remove(bookmark);
            return false;
        } else {
            bookmarks.add(bookmark);
            return true;
        }
    }

    /**
     * Get all bookmarks.
     * @return unmodifiable set of bookmarks
     */
    public Set<Bookmark> getBookmarks() {
        return Collections.unmodifiableSet(bookmarks);
    }

    /**
     * Clear all bookmarks
     */
    public void clear() {
        bookmarks.clear();
    }

    /**
     * Get the number of bookmarked recipes
     * @return bookmark count
     */
    public int getBookmarkCount() {
        return bookmarks.size();
    }
}
