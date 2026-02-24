package dev.qheilmann.vanillaenoughitems.playground.addon.beaconbeam;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.playground.addon.PlaygroundAddonPlugin;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import net.kyori.adventure.key.Key;

/**
 * RecipeExtractor for DEMO 4: Beacon Beam recipes.
 * <p>
 * This extractor tells VEI how to index and search for BeaconBeamRecipe instances.
 * It extracts:
 * - Ingredients: The input item that will be transformed
 * - Results: The output item produced by the transformation
 * - Others: None (this recipe type has fuels, etc)
 * <p>
 * When registered, this allows VEI to:
 * - Index beacon beam recipes by their input and output items
 * - Find recipes when searching with /craft commands
 * - Navigate between related recipes in the recipe GUI
 */
@NullMarked
public class BeaconBeamExtractor implements RecipeExtractor {

    private static final Key KEY = Key.key(PlaygroundAddonPlugin.NAMESPACE, "beacon_beam");

    @Override
    public @NotNull Key key() {
        return KEY;
    }

    @Override
    public boolean canHandle(Recipe recipe) {
        return recipe instanceof BeaconBeamRecipe;
    }

    @Override
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        BeaconBeamRecipe beaconBeamRecipe = (BeaconBeamRecipe) recipe;
        return Set.of(beaconBeamRecipe.input());
    }

    @Override
    public Set<ItemStack> extractResults(Recipe recipe) {
        BeaconBeamRecipe beaconBeamRecipe = (BeaconBeamRecipe) recipe;
        return Set.of(beaconBeamRecipe.output());
    }

    @Override
    public Set<ItemStack> extractOthers(Recipe recipe) {
        return Set.of();
    }
}
