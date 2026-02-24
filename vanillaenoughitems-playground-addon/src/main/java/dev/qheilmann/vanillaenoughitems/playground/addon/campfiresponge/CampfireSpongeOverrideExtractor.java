package dev.qheilmann.vanillaenoughitems.playground.addon.campfiresponge;

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
public class CampfireSpongeOverrideExtractor implements RecipeExtractor {

    // We use the same key as the vanilla campfire extractor, so we override it
    private static final Key KEY = Key.key("campfire");

    @Override
    public @NotNull Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof CampfireRecipe;
    }

    // For this demo, we index a hardcoded dummy ingredient instead of actual recipe ingredients
    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        return Set.of(ItemStack.of(Material.WET_SPONGE));
    }

    // For this demo, we index a hardcoded dummy result instead of the actual recipe result
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
