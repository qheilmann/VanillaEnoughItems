package me.qheilmann.vei.Core.Recipe.Bookmark.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.qheilmann.vei.VanillaEnoughItems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public class InMemoryBookmarkRepository implements IBookmarkRepository {
    private final Map<UUID, Set<Keyed>> data = new HashMap<>();

    public InMemoryBookmarkRepository() {
        var defaultUuid = UUID.fromString("81376bb8-5576-47bc-a2d9-89d98746d3ec"); // hard coded quoinquoin UUID
        data.put(defaultUuid, generateTreeSet(List.of(
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
        boolean added = data.computeIfAbsent(playerId, k -> generateTreeSet()).add(recipe);

        return CompletableFuture.completedFuture(added);
    }

    @Override
    public CompletableFuture<Boolean> removeBookmarkAsync(UUID playerId, Keyed recipe) {
        boolean removed = data.getOrDefault(playerId, Collections.emptySet())
                          .removeIf(keyed -> keyed.key().asString().equals(recipe.key().asString()));
            
        return CompletableFuture.completedFuture(removed);
    }

    @Override
    public CompletableFuture<Boolean> hasBookmarkAsync(UUID playerId, Keyed recipe) {
        boolean has = data.getOrDefault(playerId, Collections.emptySet()) // TODO check if this useful or convert to key directly
                          .stream()
                          .map(keyed -> keyed.key().asString())
                          .anyMatch(string -> string.equals(recipe.key().asString()));

        return CompletableFuture.completedFuture(has);
    }



    private TreeSet<Keyed> generateTreeSet() {
        return generateTreeSet(Collections.emptyList());
    }

    private TreeSet<Keyed> generateTreeSet(Collection<? extends Key> c) {
        TreeSet<Keyed> set = new TreeSet<>(Comparator.comparing(keyed -> keyed.key().asString()));
        set.addAll(c);
        return set;
    }
}
