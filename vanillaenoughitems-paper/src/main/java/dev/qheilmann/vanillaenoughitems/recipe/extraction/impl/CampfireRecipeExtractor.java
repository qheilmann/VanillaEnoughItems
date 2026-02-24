package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.Set;

import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class CampfireRecipeExtractor implements RecipeExtractor {
    
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
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        CampfireRecipe campfire = (CampfireRecipe) recipe;
        return RecipeChoiceHelper.getItemsFromChoice(campfire.getInputChoice());
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Set.of();
    }
}
