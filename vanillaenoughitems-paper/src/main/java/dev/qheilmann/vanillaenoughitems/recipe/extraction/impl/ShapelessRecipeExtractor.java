package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.IRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class ShapelessRecipeExtractor implements IRecipeExtractor<@NonNull ShapelessRecipe> {

    public static final Key KEY = Key.key("shapeless");

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof ShapelessRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(ShapelessRecipe recipe) {
        Set<ItemStack> ingredients = new HashSet<>();
        
        for (RecipeChoice choice : recipe.getChoiceList()) {
            ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(choice));
        }
        return ingredients;
    }

    @Override
    public Set<ItemStack> extractOthers(ShapelessRecipe recipe) {
        return Set.of();
    }

    @Override
    public @NotNull Key key() {
        return KEY;
    }
}
