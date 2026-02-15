package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorStrategy;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class SmithingTrimRecipeRecipeExtractor implements RecipeExtractorStrategy<@NonNull SmithingTrimRecipe> {
    
    public static final Key KEY = Key.key("smithing_trim");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof SmithingTrimRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(SmithingTrimRecipe recipe) {
        Set<ItemStack> ingredients = new HashSet<>();
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getTemplate()));
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getBase()));
        ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getAddition()));
        return ingredients;
    }

    @Override
    public Set<ItemStack> extractOthers(SmithingTrimRecipe recipe) {
        return Set.of();
    }
}
