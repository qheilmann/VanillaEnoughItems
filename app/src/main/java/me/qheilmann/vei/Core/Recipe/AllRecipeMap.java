package me.qheilmann.vei.Core.Recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import me.qheilmann.vei.Core.Utils.NotNullMap;

public class AllRecipeMap {

    private final NotNullMap<ItemStack, ItemRecipeMap> recipes;

    public AllRecipeMap() {
        this.recipes = new NotNullMap<>(new HashMap<>());
    }

    public AllRecipeMap(@NotNull AllRecipeMap itemWorkbenchRecipeCollection) {
        this.recipes = new NotNullMap<>(new HashMap<>(), itemWorkbenchRecipeCollection.recipes);
    }

    // Add methods to delegate to the wrapped NotNullMap
    
    /**
     * Returns the number of different items who can be crafted.
     */
    public int size() {
        return recipes.size();
    }

    /**
     * Returns true if the map contains no items.
     */
    public boolean isEmpty() {
        return recipes.isEmpty();
    }

    /**
     * Returns true if the map contains the specified item.
     * 
     * @param item the item to check for
     * @return true if the map contains the specified item
     */
    public boolean containsItem(@Nullable Object item) {
        ItemStack singleItem = getSingleItem(item);
        if (singleItem == null) {
            return false;
        }

        return recipes.containsKey(singleItem);
    }

    /**
     * Returns the recipes for the specified item.
     * 
     * @param key the item to get the recipes for
     * @return the recipes map for the specified item
     */
    @Nullable
    public ItemRecipeMap getItemRecipeMap(@Nullable Object key) {
        ItemStack singleItem = getSingleItem(key);
        if (singleItem == null) {
            return null;
        }
        return recipes.get(singleItem);
    }

    /**
     * Adds a recipe map to the set.
     * 
     * @param item the item to add the recipe map for
     * @param recipeMap the recipe map to add
     * @return the previous recipe map for the item, or null if there was none
     */    
    @Nullable
    public ItemRecipeMap putItemRecipeMap(@NotNull ItemStack item, @NotNull ItemRecipeMap recipeMap) {
        ItemStack singleItem = getSingleItem(item);
        if (singleItem == null) {
            return null;
        }
        
        return recipes.put(singleItem, recipeMap);
    }

    /**
     * Removes the recipe map for the specified item.
     * 
     * @param item the item to remove the recipe map for
     * @return the recipe map for the item, or null if there was none
     */
    @Nullable
    public ItemRecipeMap remove(@Nullable Object item) {
        ItemStack singleItem = getSingleItem(item);
        if (singleItem == null) {
            return null;
        }
        
        return recipes.remove(singleItem);
    }

    /**
     * Adds all recipe maps in the specified collection to the set.
     * 
     * @param c the collection of recipe maps to add
     * @return true if the set was modified
     */
    public void putAllRecipeMap(@NotNull AllRecipeMap c) {
        recipes.putAll(c.recipes);
    }

    /**
     * Removes all recipe maps from the set.
     */
    public void clear() {
        recipes.clear();
    }

    /**
     * Returns the set of items who can be crafted.
     */
    @NotNull
    public Set<ItemStack> getItems() {
        return recipes.keySet();
    }

    /**
     * Returns an unmodifiable view of the map.
     */
    @NotNull
    public Map<ItemStack, ItemRecipeMap> asMap() {
        return Collections.unmodifiableMap(recipes);
    }

    /**
     * Returns a collection view of the recipe maps contained in the map.
     */
    @NotNull
    public Collection<ItemRecipeMap> recipeMapSet() {
        return recipes.values();
    }

    /**
     * Returns a set view of the mappings contained in this map.
     */
    @NotNull
    public Set<Map.Entry<ItemStack, ItemRecipeMap>> entrySet() {
        return recipes.entrySet();
    }

    /**
     * Returns true if the map is equal to the specified object.
     */
    public boolean equals(@Nullable Object o) {
        return recipes.equals(o);
    }

    /**
     * Returns the hash code of the map.
     */
    public int hashCode() {
        return recipes.hashCode();
    }

    /**
     * Returns a string representation of the map.
     */
    @NotNull
    public String toString() {
        return recipes.toString();
    }

    /**
     * Returns a single item stack from the specified object or null if the 
     * object is null or not an item stack.
     * 
     * @param item the object to get the item stack from
     * @return the single item stack
     */
    private ItemStack getSingleItem(@Nullable Object item) {
        if (item == null || !(item instanceof ItemStack itemStack)) {
            return null;
        }
        
        ItemStack singleItem = itemStack.clone();
        singleItem.setAmount(1);
        return singleItem;
    }
}