package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorStrategy;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class BlastingRecipeExtractor implements RecipeExtractorStrategy<@NonNull BlastingRecipe> {

    public static final Key KEY = Key.key("blasting");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof BlastingRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(BlastingRecipe recipe) {
        return RecipeChoiceHelper.getItemsFromChoice(recipe.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(BlastingRecipe recipe) {
        return Fuels.FUELS;
    }
}
