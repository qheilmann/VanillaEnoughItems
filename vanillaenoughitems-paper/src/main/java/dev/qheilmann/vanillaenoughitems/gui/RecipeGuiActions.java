package dev.qheilmann.vanillaenoughitems.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Interface for ProcessPanel to communicate with the parent RecipeGui.
 * Allows panels to trigger navigation actions without accessing RecipeGui internals.
 */
@NullMarked
public interface RecipeGuiActions {
    
    /**
     * Navigate to the next recipe in the current process.
     * Does not affect navigation history.
     */
    void nextRecipe();
    
    /**
     * Navigate to the previous recipe in the current process.
     * Does not affect navigation history.
     */
    void previousRecipe();
    
    /**
     * Change the currently displayed recipe to the one producing the given result item.
     * Pushes the current recipe reader onto the navigation history stack.
     * 
     * @param resultItem the result item of the target recipe
     */
    void changeRecipe(ItemStack resultItem);
    
    /**
     * Navigate backward in the recipe history stack.
     */
    void historyBackward();
    
    /**
     * Navigate forward in the recipe history stack (after going backward).
     */
    void historyForward();
    
    /**
     * Get the currently displayed recipe
     * @return the current recipe
     */
    Recipe getCurrentRecipe();
    
    /**
     * Get the currently displayed process
     * @return the current process
     */
    Process getCurrentProcess();
}
