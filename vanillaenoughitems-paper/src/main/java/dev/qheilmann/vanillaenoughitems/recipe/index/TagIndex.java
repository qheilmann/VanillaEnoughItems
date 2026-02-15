package dev.qheilmann.vanillaenoughitems.recipe.index;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;

/**
 * Index for item tags.
 * Maps items to their tags and provides lookup for tags that exactly match a set of items.
 */
@NullMarked
public class TagIndex {
    
    private final ConcurrentHashMap<ItemType, Set<TagKey<ItemType>>> itemToTags = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<TagKey<ItemType>, Set<ItemType>> tagToItems = new ConcurrentHashMap<>();
    
    /**
     * Build the tag index from a collection of tags
     * @param tags the tags to index
     * @param itemRegistry the item registry to resolve typed keys to ItemType instances
     */
    public void index(Iterable<Tag<ItemType>> tags, Registry<ItemType> itemRegistry) {
        int tagCount = 0;
        AtomicInteger itemCount = new AtomicInteger(0);
        
        // Iterate through all tags
        for (Tag<ItemType> tag : tags) {
            TagKey<ItemType> tagKey = tag.tagKey();
            tagCount++;
            
            Set<ItemType> tagItemsSet = ConcurrentHashMap.newKeySet();
            
            // For each item in the tag, add the tag to that item's set and cache tag contents
            tag.forEach(typedKey -> {
                ItemType itemType = itemRegistry.get(typedKey);
                if (itemType != null) {
                    itemToTags.computeIfAbsent(itemType, k -> ConcurrentHashMap.newKeySet()).add(tagKey);
                    tagItemsSet.add(itemType);
                    itemCount.incrementAndGet();
                }
            });
            
            // Cache the tag's items for exact matching
            tagToItems.put(tagKey, tagItemsSet);
        }
        
        VanillaEnoughItems.LOGGER.info("Tag index: {} tags, {} item-tag mappings", tagCount, itemCount.get());
    }
    
    /**
     * Get all tags that an item belongs to
     * @param itemType the item type
     * @return set of tags, or empty set if none
     */
    public Set<TagKey<ItemType>> getTags(ItemType itemType) {
        Set<TagKey<ItemType>> tags = itemToTags.get(itemType);
        return tags != null ? tags : Set.of();
    }
    
    /**
     * Find all tags that exactly match the given set of items.
     * A tag matches if its items are exactly the same as the provided set.
     * 
     * @param itemTypes the set of item types to match
     * @return set of tags that exactly match, or empty set if none
     */
    public Set<TagKey<ItemType>> getTagsExactlyMatching(Set<ItemType> itemTypes) {
        if (itemTypes.isEmpty()) {
            return Set.of();
        }
        
        // Start with tags from the first item
        Iterator<ItemType> itemIterator = itemTypes.iterator();
        ItemType firstItem = itemIterator.next();
        Set<TagKey<ItemType>> candidateTags = new HashSet<>(getTags(firstItem));
        
        if (candidateTags.isEmpty()) {
            return Set.of();
        }
        
        // Intersect with tags from each subsequent item
        while (itemIterator.hasNext() && !candidateTags.isEmpty()) {
            ItemType nextItem = itemIterator.next();
            candidateTags.retainAll(getTags(nextItem));
        }
        
        if (candidateTags.isEmpty()) {
            return Set.of();
        }
        
        // Now verify exact match: check that each candidate tag contains exactly our items
        Set<TagKey<ItemType>> result = new HashSet<>();
        
        for (TagKey<ItemType> candidateTag : candidateTags) {
            // Get the cached tag items
            Set<ItemType> tagItems = tagToItems.get(candidateTag);
            
            // Check if sets are exactly equal (tag doesn't contain extra items)
            if (tagItems != null && tagItems.equals(itemTypes)) {
                result.add(candidateTag);
            }
        }
        
        return result;
    }
    
    /**
     * Get the total number of unique items indexed
     * @return item count
     */
    public int getIndexedItemCount() {
        return itemToTags.size();
    }
}
