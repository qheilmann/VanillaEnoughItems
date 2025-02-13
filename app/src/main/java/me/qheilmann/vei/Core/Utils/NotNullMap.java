package me.qheilmann.vei.Core.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

/**
 * A specialized Map implementation for managing key-value pairs.
 * This class ensures no null keys or values are added.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class NotNullMap<K, V> implements Map<K, V> {
    private final @NotNull Map<K, V> wrappedMap;

    /**
     * Constructs a new map that cannot store null keys or values.
     * Use the provided map implementation to store the key-value pairs.
     * <p>
     * If the provided map contains elements, they are removed.
     * 
     * @param mapImplementation the map implementation to use (remove any
     * existing elements)
     */
    public NotNullMap(@NotNull Map<K, V> mapImplementation) {
        this(mapImplementation, Collections.emptyMap());
    }

    /**
     * Constructs a new map containing the key-value pairs in the specified
     * map. The provided map implementation is used to store the key-value pairs.
     * <p>
     * If the provided map contains elements, they are removed.
     *
     * @param mapImplementation the map implementation to use (remove any 
     * existing elements)
     * @param map the map whose key-value pairs are to be placed into this map
     * @throws NullPointerException if the specified map is null
     */
    public NotNullMap(@NotNull Map<K, V> mapImplementation, @NotNull Map<? extends K, ? extends V> map) {
        Preconditions.checkNotNull(mapImplementation, "The provided map cannot be null");
        Preconditions.checkNotNull(map, "The provided map cannot be null");
        
        this.wrappedMap = mapImplementation;
        mapImplementation.clear();

        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            Objects.requireNonNull(entry.getKey(), "Key cannot be null");
            Objects.requireNonNull(entry.getValue(), "Value cannot be null");
            wrappedMap.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public int size() {
        return wrappedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    @Override
    public boolean containsKey(@NotNull Object key) {
        return wrappedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(@NotNull Object value) {
        return wrappedMap.containsValue(value);
    }

    @Override
    @Nullable
    public V get(@NotNull Object key) {
        return wrappedMap.get(key);
    }

    @Override
    @Nullable
    public V put(@NotNull K key, @NotNull V value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        return wrappedMap.put(key, value);
    }

    @Override
    @Nullable
    public V remove(@Nullable Object key) {
        return wrappedMap.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        Objects.requireNonNull(map, "Map cannot be null");
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        wrappedMap.clear();
    }

    @Override
    @NotNull
    public Set<K> keySet() {
        return wrappedMap.keySet();
    }

    @Override
    @NotNull
    public Collection<V> values() {
        return wrappedMap.values();
    }

    @Override
    @NotNull
    public Set<Entry<K, V>> entrySet() {
        return wrappedMap.entrySet();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return wrappedMap.equals(obj);
    }

    @Override
    public int hashCode() {
        return wrappedMap.hashCode();
    }

    @Override
    public String toString() {
        return wrappedMap.toString();
    }
}
