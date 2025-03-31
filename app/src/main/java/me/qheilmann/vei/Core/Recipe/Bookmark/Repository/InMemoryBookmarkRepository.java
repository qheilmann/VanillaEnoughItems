package me.qheilmann.vei.Core.Recipe.Bookmark.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.qheilmann.vei.VanillaEnoughItems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public class InMemoryBookmarkRepository implements IBookmarkRepository {
    private final Map<UUID, Set<Keyed>> data = new HashMap<>();

    public InMemoryBookmarkRepository() {
        var defaultUuid = UUID.fromString("81376bb8-5576-47bc-a2d9-89d98746d3ec"); // hard coded quoinquoin UUID
        data.put(defaultUuid, new HashSet<>(List.of(
            Key.key(VanillaEnoughItems.NAMESPACE, "second_diamond_sword"),
            Key.key("stick"),
            Key.key("iron_ingot_from_smelting_deepslate_iron_ore")
        )));
    }

    @Override
    public CompletableFuture<Set<Keyed>> getBookmarksAsync(UUID playerId) {
        return CompletableFuture.completedFuture(data.getOrDefault(playerId, Collections.emptySet()));
    }

    @Override
    public CompletableFuture<Boolean> addBookmarkAsync(UUID playerId, Keyed recipe) {
        boolean added = data.computeIfAbsent(playerId, k -> new HashSet<>()).add(recipe);
        return CompletableFuture.completedFuture(added);
    }

    @Override
    public CompletableFuture<Boolean> removeBookmarkAsync(UUID playerId, Keyed recipe) {
        boolean removed = false;
        if (data.containsKey(playerId)) {
            removed = data.get(playerId).remove(recipe);
        }
        return CompletableFuture.completedFuture(removed);
    }

    @Override
    public CompletableFuture<Boolean> hasBookmarkAsync(UUID playerId, Keyed recipe) {
        boolean has = data.getOrDefault(playerId, Collections.emptySet()).contains(recipe);
        return CompletableFuture.completedFuture(has);
    }
}
