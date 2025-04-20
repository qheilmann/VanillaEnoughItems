package me.qheilmann.vei.Service;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * CustomItemRegistry is a service class that manages the registration and retrieval of custom items.
 * It supports both an internal registry and external registries for flexibility.
 */
public class CustomItemRegistry {

    private final Map<NamespacedKey, ItemStack> internalRegistry = new HashMap<>();
    private final Map<ItemStack, NamespacedKey> reverseRegistry = new HashMap<>();

    private final LinkedHashMap<String, Function<NamespacedKey, ItemStack>> externalRegistry = new LinkedHashMap<>();
    private final LinkedHashMap<String, Function<ItemStack, NamespacedKey>> reverseExternalRegistry = new LinkedHashMap<>();

    private boolean isInitialized = false;

    /**
     * Marks the registry as initialized, preventing further modifications.
     */
    public void completeInitialization() {
        isInitialized = true;
    }

    /**
     * Registers a custom ItemStack with a NamespacedKey.
     *
     * @param key       The NamespacedKey for the custom item.
     * @param itemStack The ItemStack to register.
     */
    public void registerItem(NamespacedKey key, ItemStack itemStack) {
        if (isInitialized) {
            throw new IllegalStateException("Cannot register items after the registry has been initialized.");
        }
        internalRegistry.put(key, itemStack);
        reverseRegistry.put(itemStack, key); // Add reverse mapping
    }

    /**
     * Retrieves a custom ItemStack by its NamespacedKey.
     *
     * @param key The NamespacedKey of the custom item.
     * @return The corresponding ItemStack, or null if not found.
     */
    @Nullable
    public ItemStack getItem(NamespacedKey key) {
        // Check the internal registry first
        ItemStack item = internalRegistry.get(key);
        if (item != null) {
            return item;
        }

        // Check each external registry in the order they were added
        for (Function<NamespacedKey, ItemStack> provider : externalRegistry.values()) {
            item = provider.apply(key);
            if (item != null) {
                return item;
            }
        }

        // Return null if not found
        return null;
    }

    /**
     * Retrieves the NamespacedKey for a given ItemStack.
     *
     * @param itemStack The ItemStack to look up.
     * @return The corresponding NamespacedKey, or null if not found.
     */
    @Nullable
    public NamespacedKey getKeyByItem(ItemStack itemStack) {
        // Check the internal reverse registry first
        NamespacedKey key = reverseRegistry.get(itemStack);
        if (key != null) {
            return key;
        }

        // Check each external reverse registry in the order they were added
        for (Function<ItemStack, NamespacedKey> provider : reverseExternalRegistry.values()) {
            key = provider.apply(itemStack);
            if (key != null) {
            return key;
            }
        }

        // Return null if not found
        return null;
    }

    /**
     * Removes a custom ItemStack from the internal registry.
     *
     * @param key The NamespacedKey of the custom item to remove.
     * @return The removed ItemStack, or null if it was not found.
     */
    @Nullable
    public ItemStack removeItem(NamespacedKey key) {
        if (isInitialized) {
            throw new IllegalStateException("Cannot remove items after the registry has been initialized.");
        }
        ItemStack removedItem = internalRegistry.remove(key);
        if (removedItem != null) {
            reverseRegistry.remove(removedItem); // Remove reverse mapping
        }
        return removedItem;
    }

    /**
     * Registers an external registry with a library name and a provider function.
     *
     * @param libraryName       The name of the external library.
     * @param itemStackProvider The function to provide ItemStacks by NamespacedKey.
     * @param reverseProvider   The function to provide NamespacedKeys by ItemStack for reverse mapping.
     */
    public void registerExternalRegistry(String libraryName, Function<NamespacedKey, ItemStack> itemStackProvider, Function<ItemStack, NamespacedKey> reverseProvider) {
        if (isInitialized) {
            throw new IllegalStateException("Cannot register external registries after the registry has been initialized.");
        }

        externalRegistry.put(libraryName, itemStackProvider);
        reverseExternalRegistry.put(libraryName, reverseProvider);
    }

    /**
     * Unregisters an external registry by its library name.
     *
     * @param libraryName The name of the external library to unregister.
     */
    public void unregisterExternalRegistry(String libraryName) {
        if (isInitialized) {
            throw new IllegalStateException("Cannot unregister external registries after the registry has been initialized.");
        }
        externalRegistry.remove(libraryName);
    }
}
