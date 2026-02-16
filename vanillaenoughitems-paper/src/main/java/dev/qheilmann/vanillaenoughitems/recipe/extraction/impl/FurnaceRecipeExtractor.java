package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorStrategy;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.Fuels;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class FurnaceRecipeExtractor implements RecipeExtractorStrategy<@NonNull FurnaceRecipe> {
    
    public static final Key KEY = Key.key("furnace");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof FurnaceRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(FurnaceRecipe recipe) {
        return RecipeChoiceHelper.getItemsFromChoice(recipe.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(FurnaceRecipe recipe) {
        return Fuels.FUELS;
    }
}
