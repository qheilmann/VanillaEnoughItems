package me.qheilmann.vei.Core.Recipe.Bookmark;

import java.util.UUID;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import me.qheilmann.vei.Core.Recipe.Bookmark.Repository.IBookmarkRepository;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * Manages recipe bookmarks for players identified by UUID.
 */
public class Bookmark {
    private static IBookmarkRepository repository;

    public static void init(IBookmarkRepository repositoryImpl) {
        repository = repositoryImpl;
    }

    /**
     * Gets the bookmarks for the specified player.
     * @param playerId The player's UUID
     * @return A set of the player's bookmarked recipes
     */
    public static CompletableFuture<Set<Key>> getBookmarksAsync(@NotNull UUID playerId) {
        return repository.getBookmarksAsync(playerId);
    }

    /**
     * Adds a recipe to the player's bookmarks.
     * @param playerId The player's UUID
     * @param recipe The recipe to bookmark
     * @return true if the bookmark was newly added, false otherwise
     */
    public static CompletableFuture<Boolean> addBookmarkAsync(@NotNull UUID playerId, @NotNull Keyed recipe) {
        return repository.addBookmarkAsync(playerId, recipe.key());
    }

    /**
     * Removes a recipe from the player's bookmarks.
     * @param playerId The player's UUID
     * @param recipe The recipe to remove
     * @return true if the bookmark was removed, false otherwise
     */
    public static CompletableFuture<Boolean> removeBookmarkAsync(@NotNull UUID playerId, @NotNull Keyed recipe) {
        return repository.removeBookmarkAsync(playerId, recipe.key());
    }

    /**
     * Checks if the specified recipe is in the player's bookmarks.
     * @param playerId The player's UUID
     * @param recipe The recipe to check
     * @return true if the recipe is bookmarked
     */
    public static CompletableFuture<Boolean> hasBookmarkAsync(@NotNull UUID playerId, @NotNull Keyed recipe) {
        return repository.hasBookmarkAsync(playerId, recipe.key());
    }
}
