package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class SmithingTransformRecipeExtractor implements RecipeExtractor {
    
    public static final Key KEY = Key.key("smithing_transform");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof SmithingTransformRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        SmithingTransformRecipe smithing = (SmithingTransformRecipe) recipe;
        Set<ItemStack> ingredients = new HashSet<>();
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(smithing.getTemplate()));
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(smithing.getBase()));
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(smithing.getAddition()));
        return ingredients;
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Set.of();
    }
}
