package dev.qheilmann.vanillaenoughitems.playground.addon;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import net.kyori.adventure.key.Key;

@NullMarked
public class SmookingExtractorSpongeOverride implements RecipeExtractor {

    private static final Key KEY = Key.key("campfire");

    @Override
    public @NotNull Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof CampfireRecipe;
    }

    // For demonstration, we will index a hardcoded dummy ingredient instead of the actual recipe ingredients
    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        return Set.of(ItemStack.of(Material.SPONGE));
    }

    // For demonstration, we will index a hardcoded dummy result instead of the actual recipe result
    @Override
    public Set<ItemStack> extractResults(Recipe recipe) {
        ItemStack result = ItemStack.of(Material.SPONGE);

        return Set.of(result);
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Set.of();
    }
}
