package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;

public class RecipeChoiceHelper {
    
    public static Set<ItemStack> getItemsFromChoice(RecipeChoice choice) {
        if (choice instanceof ExactChoice exactChoice) {
            return getItemsFromExactChoice(exactChoice);
        } else if (choice instanceof MaterialChoice materialChoice) {
            return getItemsFromMaterialChoice(materialChoice);
        } else if (choice == RecipeChoice.empty()) {
            return Set.of();
        }

        // TODO: Log an optional warning for unhandled RecipeChoice types.
        //       Make the warning toggleable (configurable) and include the choice's
        //       runtime class to aid debugging without spamming logs.
        
        VanillaEnoughItems.LOGGER.warn("Unhandled RecipeChoice type: " + choice.getClass().getName());
        return Set.of();
    }

    private static Set<ItemStack> getItemsFromExactChoice(ExactChoice choice) {
        return Set.copyOf(choice.getChoices());
    }

    private static Set<ItemStack> getItemsFromMaterialChoice(MaterialChoice choice) {
        Set<ItemStack> items = new HashSet<>();
        for (Material material : choice.getChoices()) {
            items.add(new ItemStack(material));
        }
        return Collections.unmodifiableSet(items);
    }
}
