package me.qheilmann.vei.Menu;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This class is a fix for the Inventory implementation.
 * This class store a copy of the items in the inventory with the original class.
 * This way if you retrieve an item from the inventory you can compare it with intanceof and it will return the correct class.
 * Note: This class is not don't support contents outer the storage contents
*/
public class InventoryFix<T extends Inventory> implements Inventory {
    private final T originalInventory;
    private final Map<Integer, ItemStack> itemMap;

    public InventoryFix(T originalInventory) {
        this.originalInventory = originalInventory;
        this.itemMap = new HashMap<>(originalInventory.getSize());
        for (int i = 0; i < originalInventory.getSize(); i++) {
            itemMap.put(i, originalInventory.getItem(i));
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        originalInventory.setItem(index, item);
        itemMap.put(index, item);

        throwIfInventoryIsNotSame();
    }

    @Override
    public ItemStack getItem(int index) {
        return itemMap.getOrDefault(index, originalInventory.getItem(index));
    }

    @Override
    public int getSize() {
        return originalInventory.getSize();
    }

    @Override
    public ItemStack[] getContents() {
        return itemMap.values().toArray(new ItemStack[0]);
    }

    @Override
    public void setContents(ItemStack[] items) throws IllegalArgumentException {
        originalInventory.setContents(items);
        for (int i = 0; i < items.length; i++) {
            itemMap.put(i, items[i]);
        }

        throwIfInventoryIsNotSame();
    }

    @Override
    public int getMaxStackSize() {
        return originalInventory.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        originalInventory.setMaxStackSize(size);
        throwIfInventoryIsNotSame();
    }

    @Override
    public void clear(int index) {
        originalInventory.clear(index);
        itemMap.remove(index);
        throwIfInventoryIsNotSame();
    }

    @Override
    public void clear() {
        originalInventory.clear();
        itemMap.clear();
        throwIfInventoryIsNotSame();
    }

    @Override
    public List<HumanEntity> getViewers() {
        return originalInventory.getViewers();
    }

    @Override
    public InventoryType getType() {
        return originalInventory.getType();
    }

    @Override
    public InventoryHolder getHolder() {
        return originalInventory.getHolder();
    }

    @Override
    public Location getLocation() {
        return originalInventory.getLocation();
    }

    @Override
    public boolean contains(Material material) {
        return itemMap.values().stream().filter(Objects::nonNull)
            .anyMatch(item -> item.getType() == material);
    }

    @Override
    public boolean contains(ItemStack item) {
        return itemMap.values().contains(item);
    }

    @Override
    public boolean contains(Material material, int amount) {
        return itemMap.values().stream().filter(Objects::nonNull)
            .filter(item -> item.getType() == material)
            .mapToInt(ItemStack::getAmount)
            .sum() >= amount;
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        // idk if I need to use equals or isSimilar the docs are not clear, but the docs say that the stack size is verified
        return itemMap.values().stream().filter(Objects::nonNull)
            .filter(item::equals)
            .mapToInt(ItemStack::getAmount)
            .sum() >= amount;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        HashMap<Integer, ItemStack> map = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.getType() == material) {
                map.put(entry.getKey(), item);
            }
        }
        return map;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> map = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack value = entry.getValue();
            if (value != null && value.equals(item)) {
                map.put(entry.getKey(), value);
            }
        }
        return map;
    }

    @Override
    public int first(Material material) {
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.getType() == material) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public int first(ItemStack item) {
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack value = entry.getValue();
            if (value != null && value.equals(item)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < originalInventory.getSize(); i++) {
            if (itemMap.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return itemMap.values().stream().allMatch(item -> item == null);
    }

    @Override
    public void remove(Material material) {
        itemMap.entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue().getType() == material);
        throwIfInventoryIsNotSame();
    }

    @Override
    public void remove(ItemStack item) {
        itemMap.entrySet().removeIf(entry -> entry.getValue() != null && entry.getValue().equals(item));
        throwIfInventoryIsNotSame();
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        
        // Track items to be removed from itemMap (original inventory will modify the input items array)
        HashMap<ItemStack, Integer> itemsToRemove = new HashMap<>();
        for (ItemStack item : items) {
            if (itemsToRemove.entrySet().stream().anyMatch(e -> e.getKey().isSimilar(item))) {
                itemsToRemove.put(item, itemsToRemove.get(item) + item.getAmount());
            } else {
            itemsToRemove.put(item, item.getAmount());
            }
        }

        // Attempt to remove items from the original inventory
        HashMap<Integer, ItemStack> leftover = originalInventory.removeItem(items);
        
        // Attempt to remove items from the itemMap
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack invItem = entry.getValue();
            if (invItem != null && itemsToRemove.entrySet().stream().anyMatch(e -> e.getKey().isSimilar(invItem))) {
                Entry<ItemStack, Integer> itemToRemove = itemsToRemove.entrySet().stream().filter(e -> e.getKey().isSimilar(invItem)).findFirst().get();
                int amountToRemove = itemToRemove.getValue();
                int currentAmount = invItem.getAmount();
                if (currentAmount <= amountToRemove) { // item inside inv is completly removed and some amount left inside amountToRemove
                    itemMap.remove(entry.getKey());
                    itemToRemove.setValue(amountToRemove - currentAmount);
                } else { // some item inside inv left, the itemToRemove can be delete
                    invItem.setAmount(currentAmount - amountToRemove);
                    itemsToRemove.remove(itemToRemove.getKey());
                }
            }
        }

        throwIfInventoryIsNotSame();
        return leftover;
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {

        // Track items to be removed from itemMap (original inventory will modify the input items array)
        HashSet<ItemStack> itemsToRemove = new HashSet<>();
        for (ItemStack item : items) {
            itemsToRemove.add(item);
        }

        // Attempt to add items to the original inventory
        HashMap<Integer, ItemStack> leftover = originalInventory.addItem(items);
        
        // AAttempt to add items to the itemMap
        for (ItemStack item : itemsToRemove) {
            int amountToStore = item.getAmount();
            
            // Try to add to existing stacks
            for (int j = 0; j < getSize(); j++) {
                ItemStack current = getItem(j);
                if (current != null && current.isSimilar(item)) {
                    int maxStackSize = Math.min(current.getMaxStackSize(), getMaxStackSize());
                    int currentAmount = current.getAmount();
                    
                    if (currentAmount < maxStackSize) {
                        int amountToAdd = Math.min(amountToStore, maxStackSize - currentAmount);
                        current.setAmount(currentAmount + amountToAdd);
                        amountToStore -= amountToAdd;
                        
                        if (amountToStore <= 0) {
                            break;
                        }
                    }
                }
            }
            
            // Try to add to empty slots
            for (int j = 0; j < getSize(); j++) {
                if (amountToStore <= 0) {
                    break;
                }
                
                ItemStack current = getItem(j);
                if (current == null) {
                    int maxStackSize = Math.min(item.getMaxStackSize(), getMaxStackSize());
                    int amountToAdd = Math.min(amountToStore, maxStackSize);
                    
                    ItemStack newItem = item.clone();
                    newItem.setAmount(amountToAdd);
                    setItem(j, newItem);
                    amountToStore -= amountToAdd;
                }
            }
        }
        
        throwIfInventoryIsNotSame();
        return leftover;
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        return itemMap.values().stream().filter(Objects::nonNull)
            .filter(item::isSimilar)
            .mapToInt(ItemStack::getAmount)
            .sum() >= amount;
    }



    @Override
    public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... items) throws IllegalArgumentException {
        
        // Track items to be removed from itemMap (original inventory will modify the input items array)
        HashMap<ItemStack, Integer> itemsToRemove = new HashMap<>();
        for (ItemStack item : items) {
            if (itemsToRemove.entrySet().stream().anyMatch(e -> e.getKey().isSimilar(item))) {
                itemsToRemove.put(item, itemsToRemove.get(item) + item.getAmount());
            } else {
            itemsToRemove.put(item, item.getAmount());
            }
        }

        // Attempt to remove items from the original inventory
        HashMap<Integer, ItemStack> leftover = originalInventory.removeItemAnySlot(items);
        
        // Attempt to remove items from the itemMap
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack invItem = entry.getValue();
            if (invItem != null && itemsToRemove.entrySet().stream().anyMatch(e -> e.getKey().isSimilar(invItem))) {
                Entry<ItemStack, Integer> itemToRemove = itemsToRemove.entrySet().stream().filter(e -> e.getKey().isSimilar(invItem)).findFirst().get();
                int amountToRemove = itemToRemove.getValue();
                int currentAmount = invItem.getAmount();
                if (currentAmount <= amountToRemove) { // item inside inv is completly removed and some amount left inside amountToRemove
                    itemMap.remove(entry.getKey());
                    itemToRemove.setValue(amountToRemove - currentAmount);
                } else { // some item inside inv left, the itemToRemove can be delete
                    invItem.setAmount(currentAmount - amountToRemove);
                    itemsToRemove.remove(itemToRemove.getKey());
                }
            }
        }

        throwIfInventoryIsNotSame();
        return leftover;
    }

    @Override
    public @Nullable ItemStack @NotNull [] getStorageContents() {
        return itemMap.values().toArray(new ItemStack[0]);
    }

    @Override
    public void setStorageContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException {
        originalInventory.setStorageContents(items);
        for (int i = 0; i < originalInventory.getSize(); i++) {
            if(i < items.length)
                itemMap.put(i, items[i]);
            else
                itemMap.remove(i);
        }

        throwIfInventoryIsNotSame();
    }

    @Override
    public int close() {
        return originalInventory.close();
    }

    @Override
    public @Nullable InventoryHolder getHolder(boolean useSnapshot) {
        return originalInventory.getHolder(useSnapshot);
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator() {
        return List.copyOf(itemMap.values()).listIterator();
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator(int index) {
        return List.copyOf(itemMap.values()).listIterator(index);
    }

    /**
     * Throw an IllegalStateException if the original inventory is not the same as the inventoryFix
     * This method is used to check if the inventoryFix was correctly implemented and it's used for debugging temporary
     */
    @Deprecated(forRemoval = true)
    protected void throwIfInventoryIsNotSame() {
        for (int i = 0; i < originalInventory.getSize(); i++) {
            ItemStack originalItem = originalInventory.getItem(i);
            ItemStack mapItem = itemMap.get(i);
            if (!Objects.equals(originalItem, mapItem)) {
            throw new IllegalStateException("The inventoryFix was not correctly implemented, the original inventory is not the same as the inventoryFix, please check the implementation");
            }
        }
    }
}

// TODO link correcly itemMap and originalInventory
// encasp try catch around the originalInventory and retrieve the snapshot if an exception is thrown / inv not same
// add throwIfInventoryIsNotSame where I change the original inventory
// and precondition like interface