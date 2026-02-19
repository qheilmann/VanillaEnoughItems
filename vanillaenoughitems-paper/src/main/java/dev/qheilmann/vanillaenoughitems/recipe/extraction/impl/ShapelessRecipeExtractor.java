package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class ShapelessRecipeExtractor implements RecipeExtractor {

    public static final Key KEY = Key.key("shapeless");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof ShapelessRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
        Set<ItemStack> ingredients = new HashSet<>();
        
        for (RecipeChoice choice : shapeless.getChoiceList()) {
            ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(choice));
        }
        return ingredients;
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Set.of();
    }
}
