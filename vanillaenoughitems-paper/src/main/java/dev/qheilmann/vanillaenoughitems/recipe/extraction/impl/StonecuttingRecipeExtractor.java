package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class StonecuttingRecipeExtractor implements RecipeExtractor {

    public static final Key KEY = Key.key("stonecutting");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof StonecuttingRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        StonecuttingRecipe stonecutting = (StonecuttingRecipe) recipe;
        return RecipeChoiceHelper.getItemsFromChoice(stonecutting.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Set.of();
    }
}
