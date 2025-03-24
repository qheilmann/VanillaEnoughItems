package me.qheilmann.vei.Core.Recipe.Bookmark.Repository;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.key.Keyed;

public interface IBookmarkRepository {
    CompletableFuture<Set<Keyed>> getBookmarksAsync(UUID playerId);
    CompletableFuture<Boolean> addBookmarkAsync(UUID playerId, Keyed recipe);
    CompletableFuture<Boolean> removeBookmarkAsync(UUID playerId, Keyed recipe);
    CompletableFuture<Boolean> hasBookmarkAsync(UUID playerId, Keyed recipe);
}
