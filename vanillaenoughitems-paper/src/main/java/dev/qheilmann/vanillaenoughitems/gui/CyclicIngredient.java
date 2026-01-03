package dev.qheilmann.vanillaenoughitems.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;

import java.util.List;

/**
 * Manages cycling through multiple ItemStack options for a RecipeChoice.
 * Used to animate ingredient slots that accept multiple items.
 */
@NullMarked
public class CyclicIngredient {
    private final ItemStack[] options;
    private int currentIndex;

    /**
     * Create an CyclicIngredient from a RecipeChoice.
     * @param choice the recipe choice to create from
     * @param seed a seed value to set starting position (same seed = same offset for all items)
     */
    public CyclicIngredient(int seed, RecipeChoice choice) {
        List<ItemStack> items = RecipeChoiceHelper.getItemsFromChoice(choice).stream().toList();
        this.options = items.toArray(new ItemStack[0]);
        this.currentIndex = Math.abs(seed) % options.length;
    }

    /**
     * Create an CyclicIngredient from ItemStacks.
     * Automatically synchronizes with other instances using the same item types and seed.
     * @param seed a seed value to set starting position (same seed = same offset for all items)
     * @param item the items to display
     */
    public CyclicIngredient(int seed, ItemStack... item) {
        if (item.length == 0) {
            throw new IllegalArgumentException("CyclicIngredient must have at least one item");
        }
        this.options = item.clone();
        this.currentIndex = Math.abs(seed) % options.length;
    }

    /**
     * Get the currently displayed ItemStack
     * @return the current item
     */
    public ItemStack getCurrentItem() {
        return options[currentIndex];
    }

    /**
     * Advance to the next item in the cycle
     * @return the new current item
     */
    public ItemStack tickForward() {
        currentIndex = (currentIndex + 1) % options.length;
        return getCurrentItem();
    }

    /**
     * Go back to the previous item in the cycle
     * @return the new current item
     */
    public ItemStack tickBackward() {
        currentIndex = (currentIndex - 1 + options.length) % options.length;
        return getCurrentItem();
    }

    /**
     * Check if this view has multiple options to cycle through
     * @return true if there are multiple options, false if only one
     */
    public boolean hasMultipleOptions() {
        return options.length > 1;
    }

    /**
     * Get the number of options available
     * @return the number of options
     */
    public int getOptionCount() {
        return options.length;
    }

    /**
     * Check if the CyclicIngredient contains a specific ItemStack
     * @param item the item to check
     * @return true if the item is in the options, false otherwise
     */
    public boolean contains(ItemStack item) {
        for (ItemStack option : options) {
            if (option.isSimilar(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pin the current index to a specific item if it exists in the options
     * @param item the item to pin to
     */
    public void pin(ItemStack item) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSimilar(item)) {
                currentIndex = i;
                return;
            }
        }

        throw new IllegalArgumentException("Item not found in CyclicIngredient options");
    }

    /**
     * Set the current index to a specific option, wrapping if necessary
     * @param index the index to set
     */
    public void setIndex(int index) {
        currentIndex = index % options.length;
    }
}
