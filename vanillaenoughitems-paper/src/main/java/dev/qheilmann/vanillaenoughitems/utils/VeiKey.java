package dev.qheilmann.vanillaenoughitems.utils;

import org.bukkit.NamespacedKey;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import net.kyori.adventure.key.Key;

/**
 * Utility class for creating NamespacedKeys and Adventure Keys in the VEI namespace.
 */
public class VeiKey {

    /**
     * Creates a Kyori Adventure Key in the VEI namespace.
     *
     * @param value the key value
     * @return a Key with the VEI namespace
     */
    public static final Key key(String value) {
        return Key.key(VanillaEnoughItems.NAMESPACE, value);
    }

    /**
     * Creates a Bukkit NamespacedKey in the VEI namespace.
     *
     * @param value the key value
     * @return a NamespacedKey with the VEI namespace
     */
    public static final NamespacedKey namespacedKey(String value) {
        return new NamespacedKey(VanillaEnoughItems.NAMESPACE, value);
    }

    /**
     * Converts a Kyori Adventure Key to a Bukkit NamespacedKey.
     * <p> Note: A bukkit NamespacedKey implements Adventure Key </p>
     *
     * @param key the Kyori Adventure Key
     * @return a Bukkit NamespacedKey
     */
    public static final NamespacedKey toNamespacedKey(Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }
}
