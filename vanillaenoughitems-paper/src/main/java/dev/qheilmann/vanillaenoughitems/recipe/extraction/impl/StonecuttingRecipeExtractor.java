package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorStrategy;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class StonecuttingRecipeExtractor implements RecipeExtractorStrategy<@NonNull StonecuttingRecipe> {

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
    public Set<ItemStack> extractIngredients(StonecuttingRecipe recipe) {
        return RecipeChoiceHelper.getItemsFromChoice(recipe.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(StonecuttingRecipe recipe) {
        return Set.of();
    }
}
