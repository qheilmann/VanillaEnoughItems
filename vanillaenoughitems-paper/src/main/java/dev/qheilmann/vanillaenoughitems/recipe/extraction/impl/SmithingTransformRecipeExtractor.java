package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorStrategy;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class SmithingTransformRecipeExtractor implements RecipeExtractorStrategy<@NonNull SmithingTransformRecipe> {
    
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
    public Set<ItemStack> extractIngredients(SmithingTransformRecipe recipe) {
        Set<ItemStack> ingredients = new HashSet<>();
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getTemplate()));
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getBase()));
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getAddition()));
        return ingredients;
    }

    @Override
    public Set<ItemStack> extractOthers(SmithingTransformRecipe recipe) {
        return Set.of();
    }
}
