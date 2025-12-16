package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmokingRecipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.IRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class SmokingRecipeExtractor implements IRecipeExtractor<@NonNull SmokingRecipe> {

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
    public Set<ItemStack> extractIngredients(SmokingRecipe recipe) {
        return RecipeChoiceHelper.getItemsFromChoice(recipe.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(SmokingRecipe recipe) {
        return Fuels.FUELS;
    }
}
