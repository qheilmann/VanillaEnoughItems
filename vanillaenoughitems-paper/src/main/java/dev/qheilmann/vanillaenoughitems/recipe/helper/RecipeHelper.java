package dev.qheilmann.vanillaenoughitems.recipe.helper;

import java.util.Comparator;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RecipeHelper {

    public static final Comparator<Recipe> RECIPE_COMPARATOR = recipeComparator();

    private static Comparator<Recipe> recipeComparator() {

        return (Recipe r1, Recipe r2) -> {
            // Check if both recipes refer to the same object
            if (r1 == r2) {
                return 0;
            } 
            
            // Check normal cases, keyed recipes
            if (r1 instanceof org.bukkit.Keyed keyed1 && r2 instanceof org.bukkit.Keyed keyed2) {
                // Compare the keys of the recipes
                int keyComparison = keyed1.getKey().compareTo(keyed2.getKey());
                if (keyComparison != 0) {
                    return keyComparison;
                }
            } else if (r1 instanceof org.bukkit.Keyed) {
                return -1; // r1 is a Keyed recipe, r2 is not
            } else if (r2 instanceof org.bukkit.Keyed) {
                return 1; // r2 is a Keyed recipe, r1 is not
            }

            // If neither is a Keyed recipe, or not differentiable by key, we can compare them by their hash codes
            int deltaHashCode = r1.hashCode() - r2.hashCode();
            if (deltaHashCode != 0) {
                return deltaHashCode;
            } 

            // If hash codes are equal, compare the class names for a consistent order (rare case)
            return r1.getClass().getName().compareTo(r2.getClass().getName());
        };
    }
}
