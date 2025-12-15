package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.IRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper.RecipeChoiceHelper;
import net.kyori.adventure.key.Key;

@NullMarked
public class ShapedRecipeExtractor implements IRecipeExtractor<@NonNull ShapedRecipe> {

    public static final Key KEY = Key.key("shaped");

    @Override
    public @NotNull Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof ShapedRecipe;
    }

    @Override
    public @NonNull Set<ItemStack> extractIngredients(ShapedRecipe recipe) {
        Set<ItemStack> ingredients = new HashSet<>();

        for (RecipeChoice choice : recipe.getChoiceMap().values()) {
            ingredients.addAll(RecipeChoiceHelper.getItemsFromChoice(choice));
        }
        return ingredients;
    }

    @Override
    public @NonNull Set<ItemStack> extractOthers(ShapedRecipe recipe) {
        return Set.of();
    }

}
