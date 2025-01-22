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

import com.google.common.base.Preconditions;

import me.qheilmann.vei.VanillaEnoughItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This class is an Inventory implementation that stores a shadow copy of the items inside the inventory.
 * <p>
 * Craftbukkit (I think) creates a new identical ItemStack instance when you set an item in the inventory, but not a derived class, just the base ItemStack class.
 * <p>
 * This class stores a copy of the item instances inside a local map and returns the items from the map instead of the original inventory.
 * This way, if you retrieve an item from the inventory, you can compare it with instanceof and it will return the correct class.
 * Note: This class does not support alternate contents other than the basic storage contents (e.g., inside player inventory just the inventory, not the armor contents, crafting matrix, etc.)
 */
public class InventoryShadow<T extends Inventory> implements Inventory {
    private final T originalInventory;
    private final Map<Integer, ItemStack> itemMap;

    /**
     * Creates a new InventoryShadow with the original inventory.
     * @param originalInventory the original inventory to fix.
     * <p>
     * Note: The inventory holder of this InventoryShadow will be the same as the original inventory.
     * This will not retrieve the original item instances inputted inside the originalInventory; it will take the ItemStack instances (which are copies) from the originalInventory.
     */
    public InventoryShadow(T originalInventory) {
        this.originalInventory = originalInventory;
        this.itemMap = new HashMap<>(originalInventory.getSize());
        for (int i = 0; i < originalInventory.getSize(); i++) {
            itemMap.put(i, originalInventory.getItem(i));
        }
        throwIfInventoryIsNotSame();
    }

    public T getOriginalInventory() {
        return originalInventory;
    }

    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        internalSetItem(index, item);
        throwIfInventoryIsNotSame();
    }

    /*
    * This method is used to set an item inside the inventory, 
    * the original inventory use CraftItemStack.asNMSCopy(item) and duplicate the item instance
    * This method will not check if the two inventory are the same
    */
    private void internalSetItem(int index, @Nullable ItemStack item) {
        originalInventory.setItem(index, item);

        if (item == null || item.isEmpty())
        {
            itemMap.put(index, null);
            return;
        }

        itemMap.put(index, item);
    }

    @Override
    @Nullable
    public ItemStack getItem(int index) {
        ItemStack item = itemMap.get(index);
        if (item == null || item.isEmpty()) {
            return null;
        }
        return item;
    }

    @Override
    public int getSize() {
        return originalInventory.getSize();
    }

    /**
     * Unimplemented method, not supported by InventoryShadow
     * Use getStorageContents instead (Warning: this methode only retrieve the items from the storage contents)
     * 
     * @deprecated in favour of {@link #getStorageContents()}
     * @return null
     */
    @Override
    @Deprecated
    public @Nullable ItemStack @NotNull [] getContents() {
        throw new UnsupportedOperationException("getContents is not implemented, is not supported by InventoryShadow. Use getStorageContents instead.");
    }

    @Override
    public @Nullable ItemStack @NotNull [] getStorageContents() {
        if(itemMap.size() == 0) {
            return null;
        }

        return itemMap.entrySet().stream()
            .filter(entry -> entry.getValue() != null)
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .toArray(ItemStack[]::new);
    }

    /**
     * Unimplemented method, not supported by InventoryShadow
     * Use setStorageContents instead (Warning: this methode only set the items in the storage contents)
     * 
     * @deprecated in favour of {@link #setStorageContents(ItemStack[])}
     */
    @Override
    @Deprecated
    public void setContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException{
        throw new UnsupportedOperationException("setContents is not implemented, is not supported by InventoryShadow. Use setStorageContents instead.");
    }

    @Override
    public void setStorageContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException {
        Preconditions.checkArgument(items.length <= this.getSize(), "Invalid inventory size (%s); expected %s or less", items.length, this.getSize());

        for (int i = 0; i < this.getSize(); i++) {
            if (i >= items.length) {
                this.internalSetItem(i, null);
            } else {
                this.internalSetItem(i, items[i]);
            }
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
        internalClear(index);
        throwIfInventoryIsNotSame();
    }

    /*
     * Clear the item at the specified index.
     * This method will not check if the two inventory are the same
     */
    private void internalClear(int index) {
        internalSetItem(index, null);
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.getSize(); i++) {
            internalClear(i);
        }
        throwIfInventoryIsNotSame();
    }

    @Override
    @NotNull
    public List<HumanEntity> getViewers() {
        return originalInventory.getViewers();
    }

    @Override
    @NotNull
    public InventoryType getType() {
        return originalInventory.getType();
    }

    @Override
    @Nullable
    public InventoryHolder getHolder() {
        return originalInventory.getHolder();
    }

    @Override
    @Nullable
    public Location getLocation() {
        return originalInventory.getLocation();
    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        Preconditions.checkArgument(material != null, "Material cannot be null");

        return itemMap.values().stream().filter(Objects::nonNull)
            .anyMatch(item -> item.getType() == material);
    }

    @Override
    public boolean contains(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        return itemMap.values().contains(item);
    }

    @Override
    public boolean contains(@NotNull Material material, int amount) throws IllegalArgumentException {
        Preconditions.checkArgument(material != null, "Material cannot be null");

        if (amount <= 0) {
            return true;
        }

        return itemMap.values().stream().filter(Objects::nonNull)
            .filter(item -> item.getType() == material)
            .mapToInt(ItemStack::getAmount)
            .sum() >= amount;
    }

    @Override
    public boolean contains(@Nullable ItemStack item, int amount) {
        if (item == null) {
            return false;
        }

        if (amount <= 0) {
            return true;
        }

        // idk if I need to use equals or isSimilar the docs are not clear,
        // but the docs say that the stack size is verified so equals should be the correct method
        return itemMap.values().stream().filter(Objects::nonNull)
            .filter(item::equals)
            .mapToInt(ItemStack::getAmount)
            .sum() >= amount;
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        if (item == null) {
            return false;
        }

        if (amount <= 0) {
            return true;
        }

        return itemMap.values().stream().filter(Objects::nonNull)
            .filter(item::isSimilar)
            .mapToInt(ItemStack::getAmount)
            .sum() >= amount;
    }

    @Override
    @NotNull
    public HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        Preconditions.checkArgument(material != null, "Material cannot be null");
        HashMap<Integer, ItemStack> slots = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.getType() == material) {
                slots.put(entry.getKey(), item);
            }
        }
        return slots;
    }

    @Override
    @NotNull
    public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack value = entry.getValue();
            if ((value != null && value.equals(item)) || (value == null && item == null)) { // all specific item or all empty slots
                slots.put(entry.getKey(), value);
            }
        }
        return slots;
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        Preconditions.checkArgument(material != null, "Material cannot be null");
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.getType() == material) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public int first(@NotNull ItemStack item) {
        Preconditions.checkArgument(item != null, "Item cannot be null");
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
    public void remove(@NotNull Material material) throws IllegalArgumentException {
        Preconditions.checkArgument(material != null, "Material cannot be null");

        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.getType() == material) {
                internalClear(entry.getKey());
            }
        }

        throwIfInventoryIsNotSame();
    }

    /*
     * Returns the first slot with a partial item stack that is similar to the specified item.
     */
    private int firstPartial(ItemStack item) {
        if (item == null) {
            return -1;
        }

        return itemMap.entrySet().stream()
            .filter(entry -> entry.getValue() != null)
            .filter(entry -> entry.getValue().isSimilar(item))
            .filter(entry -> entry.getValue().getAmount() < entry.getValue().getMaxStackSize())
            .sorted(Map.Entry.comparingByKey())
            .findFirst()
            .map(Entry::getKey)
            .orElse(-1);
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        Preconditions.checkArgument(items != null, "Items cannot be null");

        for (ItemStack item : items) {
            Preconditions.checkArgument(item != null, "Item cannot be null");
        }

        // Attempt to add items to the original inventory
        HashMap<Integer, ItemStack> leftover = originalInventory.addItem(items);
        
        // Attempt to add items to the itemMap
        for (ItemStack item : items) {
            int toAdd = item.getAmount();

            int maxIteration = 0;
            while(true)
            {
                // PERFORMANCE: This is a temporary test to prevent infinite loops in case of a mal implementation, can be removed later
                if(maxIteration++ >= 56)
                {
                    VanillaEnoughItems.LOGGER.warning("Infinite loop detected in addItem method");
                    VanillaEnoughItems.LOGGER.warning("Item: " + item + " toAdd: " + toAdd);
                    throwIfInventoryIsNotSame();
                    throw new IllegalStateException("Inventores are the same but infinite loop detected in addItem method");
                }

                int firstPartialIndex = firstPartial(item);

                // We don't have any more partial items to add to, so add to an empty slot
                if(firstPartialIndex == -1)
                {
                    int firstEmptyIndex = firstEmpty();
                    
                    // We don't have any empty slots to add to, so we're done
                    if(firstEmptyIndex == -1)
                    {
                        // leftover
                        break;
                    }
                    
                    // Add to the empty slot
                    int amountToAdd = Math.min(item.getMaxStackSize(), toAdd);
                    ItemStack newItemStack = new ItemStack(item);
                    newItemStack.setAmount(amountToAdd);
                    itemMap.put(firstEmptyIndex, newItemStack);
                    toAdd -= amountToAdd;
                }
                else {
                    // Partial item found, add to it
                    ItemStack invItem = getItem(firstPartialIndex);
                    int currentAmount = invItem.getAmount();
                    int maxStackSize = invItem.getMaxStackSize();
                    int amountToAdd = Math.min(maxStackSize - currentAmount, toAdd);
                    invItem.setAmount(currentAmount + amountToAdd);
                    toAdd -= amountToAdd;
                }

                if(toAdd <= 0)
                {
                    break;
                }
            }
        }
        
        throwIfInventoryIsNotSame();
        return leftover;
    }

    @Override
    public void remove(@NotNull ItemStack item) {
        Preconditions.checkArgument(item != null, "Item cannot be null");

        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack value = entry.getValue();
            if (value != null && value.equals(item)) {
                internalClear(entry.getKey());
            }
        }

        throwIfInventoryIsNotSame();
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        Preconditions.checkArgument(items != null, "Items cannot be null");
        for (ItemStack item : items) {
            Preconditions.checkArgument(item != null, "Item cannot be null");
        }

        // Attempt to remove items from the original inventory
        HashMap<Integer, ItemStack> leftover = originalInventory.removeItem(items);
        
        // Attempt to remove items from the itemMap
        for (ItemStack item : items)
        {
            int toDelete = item.getAmount();

            while(true)
            {
                int index = first(item);

                // We don't have any more items to delete
                if(index == -1)
                {
                    item.setAmount(toDelete);
                    break;
                }

                ItemStack invItem = getItem(index);
                int currentAmount = invItem.getAmount();
                if(currentAmount <= toDelete) // clear the slot, all used up and continue
                {
                    toDelete -= currentAmount;
                    internalClear(index);
                }
                else // remove the amount from the stack and break
                {
                    invItem.setAmount(currentAmount - toDelete);
                    break;
                }
            }
        }

        throwIfInventoryIsNotSame();
        return leftover;
    }

    /**
     * Unimplemented method, not supported by InventoryShadow
     * Use removeItem instead (Warning: this methode remove the items from the storage contents)
     * 
     * @deprecated in favour of {@link #removeItem(ItemStack...)}
     */
    @Override
    @Deprecated
    public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... items) throws IllegalArgumentException {
        throw new UnsupportedOperationException("removeItemAnySlot it's not implemented, is not supported by InventoryShadow");
    }

    @Override
    public int close() {
        return originalInventory.close();
    }

    @Override
    @Nullable
    public InventoryHolder getHolder(boolean useSnapshot) {
        return originalInventory.getHolder(useSnapshot);
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator() {
        return new ArrayList<>(itemMap.values()).listIterator();
    }

    @Override
    public @NotNull ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            index += this.getSize() + 1; // ie, with -1, previous() will return the last element
        }

        return new ArrayList<>(itemMap.values()).listIterator(index);
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
                VanillaEnoughItems.LOGGER.warning("The inventoryFix was not correctly implemented, the original inventory is not the same as the inventoryFix, please check the implementation");
                
                VanillaEnoughItems.LOGGER.warning("Different Item at slot " + i);
                VanillaEnoughItems.LOGGER.warning("Slot " + i + ": " + originalItem + " != " + mapItem);

                VanillaEnoughItems.LOGGER.warning("Full Inventory: ");
                VanillaEnoughItems.LOGGER.warning("Original Inventory: ");
                for (int j = 0; j < originalInventory.getSize(); j++) {
                    VanillaEnoughItems.LOGGER.warning("Original Slot " + j + ": " + originalInventory.getItem(j) + "\n");
                }

                VanillaEnoughItems.LOGGER.warning("Item Map: ");
                for (int j = 0; j < itemMap.size(); j++) {
                    VanillaEnoughItems.LOGGER.warning("ItemMap Slot " + j + ": " + itemMap.get(j) + "\n");
                }

                throw new IllegalStateException("The inventoryFix was not correctly implemented, the original inventory is not the same as the inventoryFix, please check the implementation");
            }
        }
    }
}