package dev.qheilmann.vanillaenoughitems.recipe.index;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Represents how recipes are grouped in a collection.
 */
@NullMarked
public sealed interface Grouping permits 
    Grouping.ByResult, 
    Grouping.ByIngredient, 
    Grouping.ByOther,
    Grouping.ByProcess,
    Grouping.AllRecipes {
    
    /**
     * Recipes grouped by their result item
     */
    record ByResult(ItemStack result) implements Grouping {
        public ByResult {
            result = result.asOne();
        }
    }
    
    /**
     * Recipes grouped by a specific ingredient they use
     */
    record ByIngredient(ItemStack ingredient) implements Grouping {
        public ByIngredient {
            ingredient = ingredient.asOne();
        }
    }

    record ByOther(ItemStack other) implements Grouping {
        public ByOther {
            other = other.asOne();
        }
    }
    
    /**
     * Recipes grouped by their process (single process only).
     */
    record ByProcess(Process process) implements Grouping {
    }
    
    /**
     * All recipes without specific grouping criteria.
     * Represents viewing all recipes across all processes.
     */
    record AllRecipes() implements Grouping {
    }
}
