package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class TransmuteRecipeExtractor implements RecipeExtractor {

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
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        TransmuteRecipe transmute = (TransmuteRecipe) recipe;
        Set<ItemStack> inputs = new HashSet<>();
        inputs.addAll(RecipeChoiceHelper.getItemsFromChoice(transmute.getInput()));
        inputs.addAll(RecipeChoiceHelper.getItemsFromChoice(transmute.getMaterial()));
        return inputs;
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Set.of();
    }
}
