package me.qheilmann.vei.Core.Recipe.Bookmark.Repository;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.kyori.adventure.key.Key;

public interface IBookmarkRepository {
    CompletableFuture<Set<Key>> getBookmarksAsync(UUID playerId);
    CompletableFuture<Boolean> addBookmarkAsync(UUID playerId, Key recipe);
    CompletableFuture<Boolean> removeBookmarkAsync(UUID playerId, Key recipe);
    CompletableFuture<Boolean> hasBookmarkAsync(UUID playerId, Key recipe);
}
