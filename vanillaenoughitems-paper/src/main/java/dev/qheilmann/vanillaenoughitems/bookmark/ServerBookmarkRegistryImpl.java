package dev.qheilmann.vanillaenoughitems.bookmark;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

/**
 * Implementation of {@link ServerBookmarkRegistry}.
 * Registry for server-wide bookmarks using a {@link BookmarkCollection} backing.
 */
@NullMarked
public class ServerBookmarkRegistryImpl implements ServerBookmarkRegistry {

    private final BookmarkCollection bookmarks = new BookmarkCollection();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addBookmark(Bookmark bookmark) {
        return bookmarks.addBookmark(bookmark);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeBookmark(Bookmark bookmark) {
        return bookmarks.removeBookmark(bookmark);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        bookmarks.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Bookmark> getBookmarks() {
        return bookmarks.getBookmarks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return bookmarks.getBookmarkCount() == 0;
    }
}
