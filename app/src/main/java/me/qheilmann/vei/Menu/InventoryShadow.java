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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

/**
 * This class is an Inventory implementation that stores a shadow copy of the
 * items inside the inventory.
 * <p>
 * Craftbukkit, for example, creates a new identical ItemStack instance when
 * you set an item in the inventory, but if you add a derived class of
 * ItemStack, it will convert it to a copy ItemStack and then store it. The
 * derived type is lost.
 * <p>
 * This class stores the item instances inside a local map and returns the
 * items from the map instead of the original inventory. This way, if you
 * retrieve an item from the inventory, you can compare it with instanceof or
 * use polymorphism to run the correct implementation of your derived type.
 * <p>
 * - This class does not support alternate contents other than the basic
 * storage contents (e.g., inside player inventory the alternate contents is
 * the armor or the crafting matrix, etc.)
 * <p>
 * - Make sure the derived class of ItemStack overrides the clone method to
 * return the correct type.
 * <p>
 * - This class is not thread-safe.
 * <p>
 * - This class does not support inventory modified by other sources than
 * this class. (e.g., Player inventory click, same original inventory used
 * elsewhere)
 */
public class InventoryShadow<T extends Inventory> implements Inventory {
    private final T originalInventory;
    private final Map<Integer, ItemStack> itemMap;

    /**
     * Creates a new InventoryShadow with the original inventory.
     * @param originalInventory the original inventory to add items.
     * <p>
     * Note: The inventory holder of this InventoryShadow will be the same as 
     * the original inventory.
     * <p>
     * This constructor will transfer the items from the original inventory to 
     * the shadow inventory. However, it will not retrieve the original item 
     * instances inputted inside the originalInventory; it will take the same 
     * ItemStack instances (ItemStack base class) as stored in the 
     * originalInventory.
     */
    public InventoryShadow(T originalInventory) {
        this.originalInventory = originalInventory;
        int originalSize = originalInventory.getSize();
        this.itemMap = new HashMap<>(originalSize);
        for (int i = 0; i < originalSize; i++) {
            itemMap.put(i, originalInventory.getItem(i));
        }
        throwIfInventoryIsNotSame();
    }

    /**
     * Returns the original inventory.
     * @return the original inventory.
     */
    public T getOriginalInventory() {
        return originalInventory;
    }

    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        internalSetItem(index, item);
        throwIfInventoryIsNotSame();
    }

    /*
    * Set an item inside the inventory.
    * The original inventory uses CraftItemStack.asNMSCopy(item) and duplicates
    * the item instance. 
    * <p>
    * This method will not check if the two inventories are the same.
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
     * Unimplemented method, not supported by {@link InventoryShadow}.
     * Use {@link #getStorageContents()} instead (Warning: this method only 
     * retrieves the items from the storage contents).
     * 
     * @deprecated in favor of {@link #getStorageContents()}
     * @return throw {@link UnsupportedOperationException}
     */
    @Override
    @Deprecated
    public @Nullable ItemStack @NotNull [] getContents() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getContents is not implemented, is not supported by InventoryShadow. Use getStorageContents instead.");
    }

    /**
     * Gets a hard copy of the storage contents of the inventory.
     * Each ItemStack in the array is a clone of the item stored in the 
     * inventory.
     * @return an array of items inside the inventory, containing null if the 
     * slot is empty.
     */
    @Override
    public @Nullable ItemStack @NotNull [] getStorageContents() {
        int size = itemMap.size();
        ItemStack[] contents = new ItemStack[size];

        for (int i = 0; i < size; i++) {
            ItemStack item = itemMap.get(i);
            contents[i] = (item == null || item.isEmpty()) ? null : item.clone();
        }

        return contents;
    }

    /**
     * Unimplemented method, not supported by {@link InventoryShadow}.
     * Use {@link #setStorageContents(ItemStack[])} instead (Warning: this 
     * method only sets the items in the storage contents).
     * 
     * @deprecated in favor of {@link #setStorageContents(ItemStack[])}
     * @return throw {@link UnsupportedOperationException}
     */
    @Override
    @Deprecated
    public void setContents(@Nullable ItemStack @NotNull [] items) throws UnsupportedOperationException{
        throw new UnsupportedOperationException("setContents is not implemented, is not supported by InventoryShadow. Use setStorageContents instead.");
    }

    /**
     * Sets the storage contents of the inventory.
     * <p>
     * Pads with empty slots if the size of the items is less than the size
     * of the inventory.
     * @param items the items to set in the inventory.
     * @throws IllegalArgumentException if the items are null or the size of
     * the items is greater than the size of the inventory.
     */
    @Override
    public void setStorageContents(@Nullable ItemStack @NotNull [] items) throws IllegalArgumentException {
        Preconditions.checkArgument(items != null, "Items cannot be null");
        Preconditions.checkArgument(items.length <= this.getSize(), "Invalid inventory size (%s); expected %s or less", items.length, this.getSize());

        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            if (i < items.length) {
                this.internalSetItem(i, items[i]);
            } else {
                this.internalSetItem(i, null);
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
     * Clears the item at the specified index.
     * This method will not check if the two inventories are the same.
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

        for (ItemStack i : itemMap.values()) {
            if (item != null && item.equals(i) && --amount <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        if (item == null) {
            return false;
        }

        if (amount <= 0) {
            return true;
        }

        return itemMap.values().stream()
            .filter(Objects::nonNull)
            .filter(item::isSimilar)
            .mapToInt(ItemStack::getAmount)
            .sum() >= amount;
    }

    /**
     * Returns a HashMap of slots and ItemStacks that contain the specified
     * material.
     * <p>
     * The HashMap contains entries where the key is the slot index, and the 
     * value is an ItemStack clone in that slot. If no matching ItemStack with 
     * the given Material is found, an empty map is returned.
     * @param material the material to search for
     * @return a HashMap of slots and ItemStacks clone that contain the 
     * specified material
     * @throws IllegalArgumentException if the material is null
     * @see #all(ItemStack)
     */
    @Override
    @NotNull
    public HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        Preconditions.checkArgument(material != null, "Material cannot be null");

        HashMap<Integer, ItemStack> slots = new HashMap<>();

        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.getType() == material) {
                slots.put(entry.getKey(), item.clone());
            }
        }
        return slots;
    }

    /**
     * Returns a HashMap of slots and ItemStacks that are equals to the 
     * specified item (type and ammount).
     * <p>
     * The HashMap contains entries where the key is the slot index, and the 
     * value is an ItemStack clone in that slot. If no matching ItemStack with 
     * the given item is found, an empty map is returned.
     * @param item the item to search for
     * @return a HashMap of slots and ItemStacks clone that are similar to the 
     * specified item
     * @throws IllegalArgumentException if the item is null
     * @see #all(Material)
     */
    @Override
    @NotNull
    public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<>();

        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack mapItem = entry.getValue();
            if (mapItem != null && mapItem.equals(item)) {
                slots.put(entry.getKey(), mapItem.clone());
            }
        }
        return slots;
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        Preconditions.checkArgument(material != null, "Material cannot be null");

        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {;
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
            ItemStack mapItem = entry.getValue();
            if (mapItem != null && mapItem.equals(item)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public int firstEmpty() {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            if (itemMap.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        // item.isEmpty() is not considered empty in this method (stack size 0)
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

    /**
     * Returns the first slot with a partial item stack that is similar to the 
     * specified item.
     */
    protected int firstPartial(ItemStack item) {
        if (item == null) {
            return -1;
        }

        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            ItemStack cItem = itemMap.get(i);
            if (cItem != null && cItem.getAmount() < getMaxItemStack(cItem) && cItem.isSimilar(item)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the max stack size of the item stack.
     * The minimum of the max stack size of the item and the max stack size of 
     * the inventory.
     */
    private int getMaxItemStack(ItemStack item) {
        return Math.min(item.getMaxStackSize(), originalInventory.getMaxStackSize());
    }

    /**
     * Stores the given {@link ItemStack} in the {@link Inventory}. This will
     * try to fill existing stacks and empty slots as well as it can.
     * <p>
     * The returned HashMap contains what it couldn't store, where the key is
     * the index of the parameter, and the value is the ItemStack at that index
     * of the varargs parameter. If all items are stored, it will return an
     * empty HashMap.
     * <p>
     * It is known that in some implementations this method will also set the
     * inputted argument amount to the number of that item not placed in slots.
     * (see Note 1)
     * <p>
     * Note 1: The inputted {@link ItemStack} can't be already used (like
     * inside an {@link Inventory}), as it will be modified and cause
     * inconsistency.
     * <p>
     * Note 2: If the method fills a new empty slot with an item, this new slot
     * will be the right class only if the subclass overrides the {@link
     * #clone()} method.
     * 
     * @param items the items to add
     * @return a map of leftover items that could not be added
     * @throws IllegalArgumentException if the items are null
     */
    @Override
    public @NotNull HashMap<Integer, @NotNull ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        Preconditions.checkArgument(items != null, "Items cannot be null");
        for (ItemStack item : items) {
            Preconditions.checkArgument(item != null, "Item cannot be null");
        }

        // PERFORMANCE: After the implementation is stable and unit tests are set up
        // In the first step:
        // - remove the originalInventory.addItem, because the itemMap will in each case overwrite the items inside originalInventory
        // - modify input items so that they are identical to those modified in the original inventory, because the originalInventory will be removed
        // - move the leftover to the itemMap modification, because the originalInventory will be removed
        // - remove the hard copy of the items, because the originalInventory will be removed
        // - remove the throwIfItemStackMismatch, because the originalInventory will be removed
        // Then in the second step:
        // - remove maxIteration inside the while loop
        // - remove the throwIfInventoryIsNotSame, because the originalInventory will be removed

        /* TEMP */ ItemStack[] itemsDefault = Arrays.stream(items)
        /* TEMP */     .map(item -> item.clone())
        /* TEMP */     .toArray(ItemStack[]::new);

        /* TEMP */ HashMap<Integer, ItemStack> leftover = originalInventory.addItem(items);

        // Attempt to add items to the itemMap
        for (int i = 0; i < itemsDefault.length; i++) {
            ItemStack item = itemsDefault[i];
            int toAdd = item.getAmount();

            /* TEMP */ int maxIteration = 0;
            while(true)
            {
                /* TEMP */ //This is a temporary test to prevent infinite loops in case of a mal implementation, can be removed later
                /* TEMP */  if(maxIteration++ >= 54)
                /* TEMP */  {
                /* TEMP */      VanillaEnoughItems.LOGGER.warning("Infinite loop detected in addItem method%nItem: " + item + " toAdd: " + toAdd);
                /* TEMP */      throwIfInventoryIsNotSame();
                /* TEMP */      throw new IllegalStateException("Inventores are the same but infinite loop detected in addItem method");
                /* TEMP */  }

                int firstPartialIndex = firstPartial(item);

                // We don't have any more partial items to add to, so add to an empty slot
                if(firstPartialIndex == -1)
                {
                    int firstEmptyIndex = firstEmpty();
                    
                    // We don't have any empty slots to add to, so we're done
                    if(firstEmptyIndex == -1)
                    {
                        // leftover.put(i, item);
                        break;
                    }
                    // else we have empty slots left, add to it

                    // In the current implementation of the Inventory, if the 
                    // stacksize is greater than the maxStackSize it will add 
                    // the item directly without splitting it, except if the
                    // stacksize is greater than 99, in this case it will split
                    ItemStack newMapItem = item.clone();
                    int amountToAdd = Math.min(toAdd, 99);
                    newMapItem.setAmount(amountToAdd);

                    /* TEMP */ // Check if the original inventory is the same as the itemMap
                    /* TEMP */ // (type and amount), before overwriting
                    /* TEMP */ throwIfItemStackMismatch(newMapItem, firstEmptyIndex);

                    // overwrite the original inventory item to have the same
                    // instance between the two inventories
                    internalSetItem(firstEmptyIndex, newMapItem);
                    toAdd -= amountToAdd;
                }
                else {
                    // Partial item found, add to it
                    ItemStack partialMapItem = getItem(firstPartialIndex);
                    int currentAmount = partialMapItem.getAmount();
                    int maxStackSize = partialMapItem.getMaxStackSize();
                    int amountToAdd = Math.min(maxStackSize - currentAmount, toAdd);
                    partialMapItem.setAmount(currentAmount + amountToAdd);

                    /* TEMP */ // Check if the original inventory is the same as the itemMap
                    /* TEMP */ // (type and amount), before overwriting
                    /* TEMP */ throwIfItemStackMismatch(partialMapItem, firstPartialIndex);

                    // overwrite the original inventory item to have the same
                    // instance between the two inventories
                    internalSetItem(firstPartialIndex, partialMapItem);
                    toAdd -= amountToAdd;
                }

                if(toAdd <= 0)
                {
                    break;
                }
            }
        }
        
        /* TEMP */ // Check if the original inventory is the same as the itemMap
        /* TEMP */ throwIfInventoryIsNotSame();
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
        
        // Attempt to remove items from the itemMap
        for (ItemStack item : items)
        {
            int toDelete = item.getAmount();

            while(true)
            {
                int firstSlotIndex = first(item);

                // We don't have any more items to delete
                if(firstSlotIndex == -1)
                {
                    break;
                }

                ItemStack invItem = getItem(firstSlotIndex);
                int currentAmount = invItem.getAmount();
                if(currentAmount <= toDelete) // clear the slot, all used up and continue
                {
                    toDelete -= currentAmount;
                    internalClear(firstSlotIndex);
                }
                else // remove the amount from the stack and break
                {
                    invItem.setAmount(currentAmount - toDelete);
                    break;
                }
            }
        }

        // Attempt to remove items from the original inventory
        // (after removing items from the itemMap, as this method updates the items)
        HashMap<Integer, ItemStack> leftover = originalInventory.removeItem(items);

        throwIfInventoryIsNotSame();
        return leftover;
    }

    /**
     * Unimplemented method, not supported by {@link InventoryShadow}.
     * Use {@link #removeItem(ItemStack...)} instead (Warning: this method 
     * only removes the items from the storage contents).
     * 
     * @deprecated in favor of {@link #removeItem(ItemStack...)}
     * @return throw {@link UnsupportedOperationException}
     */
    @Override
    @Deprecated
    public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... items) throws UnsupportedOperationException {
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
     * Throws an IllegalStateException if the original inventory is not the same
     * as the InventoryShadow. This method is used to check if the 
     * InventoryShadow was correctly implemented and it's used for debugging 
     * temporarily.
     */
    @Deprecated(forRemoval = true)
    protected void throwIfInventoryIsNotSame() {
        int originalSize = originalInventory.getSize();
        int itemMapSize = itemMap.size();

        if (originalSize != itemMapSize) {
            String message = "The InventoryShadow was not correctly implemented, the original inventory size is not the same as the InventoryShadow size, please check the implementation";
            VanillaEnoughItems.LOGGER.warning(message);
            throw new IllegalStateException(message);
        }

        for (int i = 0; i < originalSize; i++) {
            ItemStack originalItem = originalInventory.getItem(i);
            ItemStack mapItem = itemMap.get(i);

            if (!Objects.equals(originalItem, mapItem)) {
                String introMessage = "The InventoryShadow was not correctly implemented, the original inventory is not the same as the InventoryShadow, please check the implementation";
                VanillaEnoughItems.LOGGER.warning(introMessage);
                
                VanillaEnoughItems.LOGGER.warning("Different Item at slot: %d%n".formatted(i));
                VanillaEnoughItems.LOGGER.warning("Slot %d: %n%s%n != %n%s%n%n".formatted(i, originalItem, mapItem));

                VanillaEnoughItems.LOGGER.warning("Original Inventory:%n");
                for (int j = 0; j < originalSize; j++) {
                    VanillaEnoughItems.LOGGER.warning("OriginalSlot[%d]: %s%n".formatted(j, originalInventory.getItem(j)));
                }

                VanillaEnoughItems.LOGGER.warning("%nItem Map Inventory:%n");
                for (int j = 0; j < itemMapSize; j++) {
                    VanillaEnoughItems.LOGGER.warning("ItemMapSlot[%d]: %s%n".formatted(j, itemMap.get(j)));
                }

                throw new IllegalStateException(introMessage);
            }
        }
    }

    /**
     * Throws an {@link IllegalStateException} if a specific {@link ItemStack}
     * reference is the same as a specific ItemStack in the original inventory.
     * This method is used to check if the InventoryShadow was correctly
     * implemented and it's used for debugging temporarily.
     * @param referenceItemStack the reference ItemStack to compare
     * @param originalInventoryIndex the index of the ItemStack in the original
     * inventory to compare
     */
    @Deprecated(forRemoval = true)
    private void throwIfItemStackMismatch(ItemStack referenceItemStack, int originalInventoryIndex) {
        ItemStack originalItem = originalInventory.getItem(originalInventoryIndex);
        if (originalItem != null && !originalItem.equals(referenceItemStack)) {
            String message = "The inventoryShadow was not correctly implemented, the original inventory will not be the same as the itemMap, please check the implementation";
            VanillaEnoughItems.LOGGER.warning(message);
            VanillaEnoughItems.LOGGER.warning("Different Item at slot %d".formatted(originalInventoryIndex));
            VanillaEnoughItems.LOGGER.warning("Slot %d: %n%s%n != %n%s%n".formatted(originalInventoryIndex, originalItem, referenceItemStack));
            throw new IllegalStateException(message);
        }
    }
}