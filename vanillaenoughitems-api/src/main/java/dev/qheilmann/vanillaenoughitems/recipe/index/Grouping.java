package dev.qheilmann.vanillaenoughitems.recipe.index;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import net.kyori.adventure.key.Key;

/**
 * Represents how recipes are grouped in a collection.
 */
@NullMarked
public sealed interface Grouping permits 
    Grouping.ByResult, 
    Grouping.ByIngredient, 
    Grouping.ByOther,
    Grouping.ByProcess,
    Grouping.ByKey,
    Grouping.AllRecipes {
    
    /**
     * Recipes grouped by their result item
     * @param result the result item that all recipes in this group produce (must be an actual result item, not just any item in the recipe)
     */
    record ByResult(ItemStack result) implements Grouping {
        /**
         * Get the result item
         * Note: the result is normalized to an amount of 1
         */
        public ByResult {
            result = result.asOne();
        }
    }
    
    /**
     * Recipes grouped by a specific ingredient they use
     * @param ingredient the ingredient item that all recipes in this group share (must be an actual ingredient, not just any item in the recipe)
     */
    record ByIngredient(ItemStack ingredient) implements Grouping {
        /**
         * Get the ingredient item
         * Note: the ingredient is normalized to an amount of 1
         */
        public ByIngredient {
            ingredient = ingredient.asOne();
        }
    }

    /**
     * Recipes grouped by another specific item they are associated with (e.g., a catalyst item).
     * The exact meaning of "other" is determined by the context in which this grouping is used.
     * @param other the item that recipes in this group are associated with, (must be an actual other item type in the recipe, not just any item in the recipe)
     */
    record ByOther(ItemStack other) implements Grouping {
        /**
         * Get the "other" item
         * Note: the other item is normalized to an amount of 1
         */
        public ByOther {
            other = other.asOne();
        }
    }
    
    /**
     * Recipes grouped by their process (single process only).
     * @param process the process that all recipes in this group share
     */
    record ByProcess(Process process) implements Grouping {
    }

    /**
     * Recipes grouped by their unique recipe ID.
     * @param key the unique key identifying the recipe
     */
    record ByKey(Key key) implements Grouping {
    }
    
    /**
     * All recipes without specific grouping criteria.
     * Represents viewing all recipes across all processes.
     */
    record AllRecipes() implements Grouping {
        // This record has no fields, so a singleton instance can be used.
        public static final AllRecipes INSTANCE = new AllRecipes();
    }
}
