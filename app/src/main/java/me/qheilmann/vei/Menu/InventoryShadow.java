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
            .toArray(ItemStack[]::new); // This will create an array of type ItemStack[], but elements will retain their actual runtime types
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

    /**
     * Stores the given ItemStacks in the inventory. This will try to fill 
     * existing stacks and empty slots as well as it can.
     * <p>
     * The returned HashMap contains what it couldn't store, where the key is 
     * the index of the parameter, and the value is the ItemStack at that index 
     * of the varargs parameter. If all items are stored, it will return an 
     * empty HashMap.
     * <p>
     * Note: If the method fills a new empty slot with an item, this new slot 
     * will be the right class only if the subclass overrides the clone method.
     * 
     * @param items the items to add
     * @return a map of leftover items that could not be added
     * @throws IllegalArgumentException if the items are null
     */
    @Override
    public HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        Preconditions.checkArgument(items != null, "Items cannot be null");

        for (ItemStack item : items) {
            Preconditions.checkArgument(item != null, "Item cannot be null");
        }

        // Create a hard copy of the items for avoid to add multiple time the
        // same item stack in the inventory (shadow item).
        // Otherwise it will create inconsistency in the inventory
        ItemStack[] itemsCopy = Arrays.stream(items)
            .map(item -> item.clone())
            .toArray(ItemStack[]::new);

        ItemStack[] itemsCopy2 = Arrays.stream(items)
            .map(item -> item.clone())
            .toArray(ItemStack[]::new);
        
        // Attempt to add items to the original inventor
        // (after adding items to the itemMap, as this method updates the items)

        // VanillaEnoughItems.LOGGER.info("Inital Items: ");
        // for (ItemStack entry : items) {
        //     if (entry != null) {
        //         VanillaEnoughItems.LOGGER.info("Entries: " + entry.toString());
        //     }
        // }

        // VanillaEnoughItems.LOGGER.warning("[221] Full Inventory: ");
        // VanillaEnoughItems.LOGGER.warning("[222]Original Inventory: ");
        // for (int j = 0; j < originalInventory.getSize(); j++) {
        //     VanillaEnoughItems.LOGGER.warning("[223#]Original Slot " + j + ": " + originalInventory.getItem(j) + "\n");
        // }

        // VanillaEnoughItems.LOGGER.warning("[224]Item Map: ");
        // for (int j = 0; j < itemMap.size(); j++) {
        //     VanillaEnoughItems.LOGGER.warning("[225#]ItemMap Slot " + j + ": " + itemMap.get(j) + "\n");
        // }

        // Create a hard copy of itemMap
        Map<Integer, ItemStack> itemMapCopy = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : itemMap.entrySet()) {
            ItemStack item = entry.getValue();
            itemMapCopy.put(entry.getKey(), item == null ? null : item.clone());
        }

        // ItemStack itemb5 = getItem(5);
        // ItemStack itemb3 = getItem(3);
        ItemStack itemb5 = itemMap.get(5);
        ItemStack itemb3 = itemMap.get(3);
        VanillaEnoughItems.LOGGER.info("[644a] InvItem: " + itemb5 + " Ref: " + System.identityHashCode(itemb5));

        HashMap<Integer, ItemStack> leftover = originalInventory.addItem(itemsCopy2);
        
        // To make sure the packets are sent to the client
        // for (ListIterator<ItemStack> it = originalInventory.iterator(); it.hasNext(); ) {
        //     int slot = it.nextIndex();
        //     ItemStack item = it.next();
        //     originalInventory.setItem(slot, item);
        // }
        // JavaPlugin plugin = VanillaEnoughItems.getPlugin(VanillaEnoughItems.class);
        // plugin.getServer().getScheduler().scheduleSyncDelayedTask(
        //     plugin, new Runnable() {
        //         @Override
        //         public void run() {
        //             for (HumanEntity humanEntity : originalInventory.getViewers()) {
        //             Player player = (Player) humanEntity;
        //             player.updateInventory();
        //             }
        //         }
        //     },
        //     1L
        // );

        ItemStack itemc5 = itemMapCopy.get(5);
        ItemStack itemc3 = itemMapCopy.get(3);

        // ItemStack itema5 = getItem(5);
        // ItemStack itema3 = getItem(3);
        ItemStack itema5 = itemMap.get(5);
        ItemStack itema3 = itemMap.get(3);

        VanillaEnoughItems.LOGGER.info("[644b] itema5: " + itema5 + " Ref: " + System.identityHashCode(itema5));
        VanillaEnoughItems.LOGGER.info("[644d] itema3: " + itema3 + " Ref: " + System.identityHashCode(itema3));
        VanillaEnoughItems.LOGGER.info("[644h] itemc5: " + itemc5 + " Ref: " + System.identityHashCode(itemc5));

        if (itemb5 != itema5) {
            VanillaEnoughItems.LOGGER.info("[644c] itemb5 and itema5 are different instances.");
        } else {
            VanillaEnoughItems.LOGGER.info("[644c] itemb5 and itema5 are the same instance.");
        }

        if (itemb3 != itema3) {
            VanillaEnoughItems.LOGGER.info("[644e] itemb3 and itema3 are different instances.");
        } else {
            VanillaEnoughItems.LOGGER.info("[644e] itemb3 and itema3 are the same instance.");
        }

        if (itema5 != itema3) {
            VanillaEnoughItems.LOGGER.info("[644f] itema5 and itema3 are different instances.");
        } else {
            VanillaEnoughItems.LOGGER.info("[644f] itema5 and itema3 are the same instance.");
        }

        if (itemb5 != itemb3) {
            VanillaEnoughItems.LOGGER.info("[644g] itemb5 and itemb3 are different instances.");
        } else {
            VanillaEnoughItems.LOGGER.info("[644g] itemb5 and itemb3 are the same instance.");
        }



        // VanillaEnoughItems.LOGGER.warning("[331] Full Inventory: ");
        // VanillaEnoughItems.LOGGER.warning("[332]Original Inventory: ");
        // for (int j = 0; j < originalInventory.getSize(); j++) {
        //     VanillaEnoughItems.LOGGER.warning("[333#]Original Slot " + j + ": " + originalInventory.getItem(j) + "\n");
        // }

        // VanillaEnoughItems.LOGGER.warning("[334]Item Map: ");
        // for (int j = 0; j < itemMap.size(); j++) {
        //     VanillaEnoughItems.LOGGER.warning("[335#]ItemMap Slot " + j + ": " + itemMap.get(j) + "\n");
        // }

        // VanillaEnoughItems.LOGGER.info("After original addItem: ");
        // for (ItemStack entry : items) {
        //     if (entry != null) {
        //         VanillaEnoughItems.LOGGER.info("After original: " + entry.toString());
        //     }
        // }

        // VanillaEnoughItems.LOGGER.info("Leftover: ");
        // for (Map.Entry<Integer, ItemStack> entry : leftover.entrySet()) {
        //     ItemStack value = entry.getValue();
        //     if (value != null) {
        //         VanillaEnoughItems.LOGGER.info("Leftover: " + entry.getKey() + " " + value);
        //     }
        // }

        // for (ItemStack entry : itemCopy) {
        //     if (entry != null) {
        //         VanillaEnoughItems.LOGGER.info("ItemCopy: " + entry.toString());
        //     }
        // }

        // HashMap<Integer, ItemStack> leftover = new HashMap<>();

        // Attempt to add items to the itemMap
        for (int i = 0; i < itemsCopy.length; i++) {
            ItemStack item = itemsCopy[i];
            int toAdd = item.getAmount();

            int maxIteration = 0;
            while(true)
            {
                // PERFORMANCE: This is a temporary test to prevent infinite loops in case of a mal implementation, can be removed later
                if(maxIteration++ >= 54)
                {
                    VanillaEnoughItems.LOGGER.warning("Infinite loop detected in addItem method");
                    VanillaEnoughItems.LOGGER.warning("Item: " + item + " toAdd: " + toAdd);
                    throwIfInventoryIsNotSame();
                    throw new IllegalStateException("Inventores are the same but infinite loop detected in addItem method");
                }

                int firstPartialIndex = firstPartial(item);
                VanillaEnoughItems.LOGGER.info("[441] First partial index: " + firstPartialIndex);

                // We don't have any more partial items to add to, so add to an empty slot
                if(firstPartialIndex == -1)
                {
                    int firstEmptyIndex = firstEmpty();
                    VanillaEnoughItems.LOGGER.info("[442] First empty index: " + firstEmptyIndex);
                    
                    // We don't have any empty slots to add to, so we're done
                    if(firstEmptyIndex == -1)
                    {
                        // ItemStack left = item.clone();
                        // left.setAmount(toAdd);
                        // leftover.put(i, left);
                        break;
                    }
                    
                    VanillaEnoughItems.LOGGER.info("[425a] toAdd (before): " + toAdd);

                    // Add to the empty slot
                    
                    // In the current implementation of the Inventory, if the 
                    // stacksize is greater than the maxStackSize it will add 
                    // the item directly without splitting it, except if the
                    // stacksize is greater than 99, in this case it will split
                    // int amountToAddOld = Math.min(toAdd, 99);
                    // // int amountToAdd = Math.min(toAdd, item.getMaxStackSize());

                    // VanillaEnoughItems.LOGGER.info("[443] Amount to add: " + amountToAddOld);
                    // ItemStack newItemStackOld = item.clone();
                    // newItemStackOld.setAmount(amountToAddOld);
                    // itemMap.put(firstEmptyIndex, newItemStackOld);
                    // toAdd -= amountToAddOld;
                    // VanillaEnoughItems.LOGGER.info("[444] toAdd (after): " + toAdd);

                    ItemStack newMapItem = item.clone();

                    // In the current implementation of the Inventory, if the 
                    // stacksize is greater than the maxStackSize it will add 
                    // the item directly without splitting it, except if the
                    // stacksize is greater than 99, in this case it will split
                    int amountToAdd = Math.min(toAdd, 99);
                    // int amountToAdd = Math.min(toAdd, item.getMaxStackSize());

                    VanillaEnoughItems.LOGGER.info("[443] Amount to add: " + amountToAdd);


                    newMapItem.setAmount(amountToAdd);

                    // Check if the original inventory is the same as the inventoryShadow (type and amount)
                    ItemStack originalItem = originalInventory.getItem(firstEmptyIndex);
                    if (originalItem != null && !originalItem.equals(newMapItem)) {
                        VanillaEnoughItems.LOGGER.warning("The inventoryShadow was not correctly implemented, the original inventory is not the same as the inventoryShadow, please check the implementation");
                        VanillaEnoughItems.LOGGER.warning("Different Item at slot " + firstEmptyIndex);
                        VanillaEnoughItems.LOGGER.warning("Slot " + firstEmptyIndex + ": \n" + originalItem + "\n != \n" + newMapItem + "\n");
                        throw new IllegalStateException("The inventoryShadow was not correctly implemented, the original inventory is not the same as the inventoryShadow please check the implementation");
                    }

                    internalSetItem(firstEmptyIndex, newMapItem);
                    toAdd -= amountToAdd;

                    VanillaEnoughItems.LOGGER.info("[444] toAdd (after): " + toAdd);
                }
                else {
                    // Partial item found, add to it
                    VanillaEnoughItems.LOGGER.info("[444b] First partial index redo: " + firstPartialIndex);
                    ItemStack partialMapItem = getItem(firstPartialIndex);

                    VanillaEnoughItems.LOGGER.info("[444c] InvItem: " + partialMapItem);
                    int currentAmount = partialMapItem.getAmount();
                    VanillaEnoughItems.LOGGER.info("[445a] toAdd (before): " + toAdd);
                    VanillaEnoughItems.LOGGER.info("[445b] Current amount: " + currentAmount);
                    int maxStackSize = partialMapItem.getMaxStackSize();
                    VanillaEnoughItems.LOGGER.info("[445c] Max stack size: " + maxStackSize);
                    int amountToAdd = Math.min(maxStackSize - currentAmount, toAdd);
                    VanillaEnoughItems.LOGGER.info("[445] Amount to add: " + amountToAdd);

                    partialMapItem.setAmount(currentAmount + amountToAdd);

                    // Check if the original inventory is the same as the inventoryShadow (type and amount)
                    ItemStack originalItem = originalInventory.getItem(firstPartialIndex);
                    if (originalItem != null && !originalItem.equals(partialMapItem)) {
                        VanillaEnoughItems.LOGGER.warning("The inventoryShadow was not correctly implemented, the original inventory is not the same as the inventoryShadow, please check the implementation");
                        VanillaEnoughItems.LOGGER.warning("Different Item at slot " + firstPartialIndex);
                        VanillaEnoughItems.LOGGER.warning("Slot " + firstPartialIndex + ": \n" + originalItem + "\n != \n" + partialMapItem + "\n");
                        throw new IllegalStateException("The inventoryShadow was not correctly implemented, the original inventory is not the same as the inventoryShadow please check the implementation");
                    }

                    internalSetItem(firstPartialIndex, partialMapItem);
                    toAdd -= amountToAdd;

                    VanillaEnoughItems.LOGGER.info("[446] toAdd (after): " + toAdd);
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

        // Attempt to remove items from the original inventory
        // (after removing items from the itemMap, as this method updates the items)
        HashMap<Integer, ItemStack> leftover = originalInventory.removeItem(items);

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
                VanillaEnoughItems.LOGGER.warning("Slot " + i + ": \n" + originalItem + "\n != \n" + mapItem + "\n");

                VanillaEnoughItems.LOGGER.warning("Full Inventory: ");
                VanillaEnoughItems.LOGGER.warning("Original Inventory: \n");
                for (int j = 0; j < originalInventory.getSize(); j++) {
                    VanillaEnoughItems.LOGGER.warning("Original Slot " + j + ": " + originalInventory.getItem(j) + "\n");
                }

                VanillaEnoughItems.LOGGER.warning("\nItem Map: \n");
                for (int j = 0; j < itemMap.size(); j++) {
                    VanillaEnoughItems.LOGGER.warning("ItemMap Slot " + j + ": " + itemMap.get(j) + "\n");
                }

                throw new IllegalStateException("The inventoryFix was not correctly implemented, the original inventory is not the same as the inventoryFix, please check the implementation");
            }
        }
    }
}