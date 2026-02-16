package dev.qheilmann.vanillaenoughitems.recipe.helper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Utility for extracting ItemStacks from {@link RecipeChoice} instances.
 * Handles ExactChoice, MaterialChoice, and empty/null choices.
 */
@NullMarked
public class RecipeChoiceHelper {
    
    private RecipeChoiceHelper() {}

    /**
     * Extract ItemStacks from a RecipeChoice.
     * 
     * @param choice the recipe choice (may be null)
     * @return an unmodifiable set of ItemStacks from the choice
     * @throws IllegalArgumentException if the choice type is not recognized
     */
    public static Set<ItemStack> getItemsFromChoice(@Nullable RecipeChoice choice) {
        if (choice == null || choice == RecipeChoice.empty()) {
            return Set.of();
        } else if (choice instanceof ExactChoice exactChoice) {
            return Set.copyOf(exactChoice.getChoices());
        } else if (choice instanceof MaterialChoice materialChoice) {
            Set<ItemStack> items = new HashSet<>();
            for (Material material : materialChoice.getChoices()) {
                items.add(ItemStack.of(material));
            }
            return Collections.unmodifiableSet(items);
        }
        throw new IllegalArgumentException("Unhandled RecipeChoice type: " + choice.getClass().getName());
    }
}
