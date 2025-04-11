package me.qheilmann.vei.Core.Recipe.Bookmark.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.qheilmann.vei.VanillaEnoughItems;
import net.kyori.adventure.key.Key;

public class InMemoryBookmarkRepository implements IBookmarkRepository {
    private final Map<UUID, Set<Key>> data = new HashMap<>();

    public InMemoryBookmarkRepository() {
        var defaultUuid = UUID.fromString("81376bb8-5576-47bc-a2d9-89d98746d3ec"); // hard coded dummy default bookmark for quoinquoin UUID
        data.put(defaultUuid, new TreeSet<>(List.of(
            Key.key(VanillaEnoughItems.NAMESPACE, "second_diamond_sword"),
            Key.key("stick"),
            Key.key("iron_ingot_from_smelting_deepslate_iron_ore")
        )));
    }

    @Override
    public CompletableFuture<Set<Key>> getBookmarksAsync(UUID playerId) {
        Set<Key> bookmarks = Collections.unmodifiableSet(data.getOrDefault(playerId, Collections.emptySet()));
        return CompletableFuture.completedFuture(bookmarks);
    }

    @Override
    public CompletableFuture<Boolean> addBookmarkAsync(UUID playerId, Key recipe) {
        boolean added = data.computeIfAbsent(playerId, k -> new TreeSet<>())
            .add(recipe);
        return CompletableFuture.completedFuture(added);
    }

    @Override
    public CompletableFuture<Boolean> removeBookmarkAsync(UUID playerId, Key recipe) {
        boolean removed = data.getOrDefault(playerId, Collections.emptySet())
            .remove(recipe);
        return CompletableFuture.completedFuture(removed);
    }

    @Override
    public CompletableFuture<Boolean> hasBookmarkAsync(UUID playerId, Key recipe) {
        boolean has = data.getOrDefault(playerId, Collections.emptySet())
            .contains(recipe);
        return CompletableFuture.completedFuture(has);
    }
}
