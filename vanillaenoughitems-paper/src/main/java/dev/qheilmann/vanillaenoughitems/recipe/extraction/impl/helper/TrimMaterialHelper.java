package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper;

import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;

public class TrimMaterialHelper {

    public static TrimMaterial byMaterialName(Material material) {

        switch (material) {
            case AMETHYST_SHARD:
                return TrimMaterial.AMETHYST;
            case COPPER_INGOT:
                return TrimMaterial.COPPER;
            case DIAMOND:
                return TrimMaterial.DIAMOND;
            case EMERALD:
                return TrimMaterial.EMERALD;
            case GOLD_INGOT:
                return TrimMaterial.GOLD;
            case IRON_INGOT:
                return TrimMaterial.IRON;
            case LAPIS_LAZULI:
                return TrimMaterial.LAPIS;
            case NETHERITE_INGOT:
                return TrimMaterial.NETHERITE;
            case QUARTZ:
                return TrimMaterial.QUARTZ;
            case REDSTONE:
                return TrimMaterial.REDSTONE;
            case RESIN_BRICK:
                return TrimMaterial.RESIN;
            default:
                if (VanillaEnoughItems.config().hasMissingImplementationWarnings()) {
                    VanillaEnoughItems.LOGGER.warn("No TrimMaterial found for material: " + material.name() + ", using default (AMETHYST)");
                }
                return TrimMaterial.AMETHYST;
        }
    }
}
