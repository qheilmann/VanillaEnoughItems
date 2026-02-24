package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmokingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class SmokingRecipeExtractor implements RecipeExtractor {

    public static final Key KEY = Key.key("smoking");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof SmokingRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        SmokingRecipe smoking = (SmokingRecipe) recipe;
        return RecipeChoiceHelper.getItemsFromChoice(smoking.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Fuels.FUELS;
    }
}
