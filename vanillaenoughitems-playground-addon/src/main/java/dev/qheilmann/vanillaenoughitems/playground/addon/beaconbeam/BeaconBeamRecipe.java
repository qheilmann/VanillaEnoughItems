package dev.qheilmann.vanillaenoughitems.playground.addon.beaconbeam;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

/**
 * Custom recipe type for DEMO 4: Beacon Beam Transformation.
 * <p>
 * This recipe type represents a magical transformation that occurs when an item
 * is dropped over an active beacon beam. It's a completely custom recipe type
 * that doesn't exist in vanilla Minecraft.
 * <p>
 * A BeaconBeamRecipe defines:
 * - An input item that can be transformed
 * - An output item that results from the transformation
 * - A unique key identifier for the recipe
 * <p>
 * This demonstrates how to create custom recipe types that work with VEI's
 * recipe indexation and visualization system.
 * Any implementation of Recipe can be used, the record is just a convenient way to create an immutable data holder for this demo.
 */
public record BeaconBeamRecipe(Key key, ItemStack input, ItemStack output) implements Recipe, Keyed{

    @Override
    public ItemStack getResult() {
        return output.clone();
    }

}
