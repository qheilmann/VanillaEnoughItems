package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorStrategy;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class TransmuteRecipeExtractor implements RecipeExtractorStrategy<@NonNull TransmuteRecipe> {

    public static final Key KEY = Key.key("transmute");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof TransmuteRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(TransmuteRecipe recipe) {
        Set<ItemStack> inputs = new HashSet<>();
        inputs.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getInput()));
        inputs.addAll(RecipeChoiceHelper.getItemsFromChoice(recipe.getMaterial()));
        return inputs;
    }

    @Override
    public Set<ItemStack> extractOthers(TransmuteRecipe recipe) {
        return Set.of();
    }
}
