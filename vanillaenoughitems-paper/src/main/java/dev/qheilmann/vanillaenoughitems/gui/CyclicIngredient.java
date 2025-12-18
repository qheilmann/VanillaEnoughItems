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
     * Create an IngredientView from a RecipeChoice
     * @param choice the recipe choice to create from
     */
    public CyclicIngredient(RecipeChoice choice) {
        List<ItemStack> items = RecipeChoiceHelper.getItemsFromChoice(choice).stream().toList();
        this.options = items.toArray(new ItemStack[0]);
        setRandomIndex();
    }

    /**
     * Create an IngredientView from a single ItemStack
     * @param item the single item to display
     */
    public CyclicIngredient(ItemStack... item) {
        if (item.length == 0) {
            throw new IllegalArgumentException("IngredientView must have at least one item");
        }
        
        this.options = item.clone();
        setRandomIndex();
    }

    public void setRandomIndex() {
        this.currentIndex = (int) (Math.random() * options.length);
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
     * Set the current index to a specific option, wrapping if necessary
     * @param index the index to set
     */
    public void setIndex(int index) {
        currentIndex = index % options.length;
    }
}
