package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.IRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class CampfireRecipeExtractor implements IRecipeExtractor<@NonNull CampfireRecipe> {
    
    public static final Key KEY = Key.key("campfire");

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof CampfireRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(CampfireRecipe recipe) {
        return RecipeChoiceHelper.getItemsFromChoice(recipe.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(CampfireRecipe recipe) {
        return Set.of();
    }
}
