package dev.qheilmann.vanillaenoughitems.config;

import org.jspecify.annotations.NullMarked;

import net.kyori.adventure.text.format.TextColor;

/**
 * Immutable style configuration for Vanilla Enough Items.
 * Handles visual appearance including colors and resource-pack settings.
 *
 * @param hasResourcePack       whether the server enforces a VEI resource pack
 * @param colorPrimary          primary highlight colour
 * @param colorPrimaryVariant   primary variant colour
 * @param colorSecondary        secondary / accent colour
 * @param colorSecondaryVariant secondary variant colour
 */
@NullMarked
public record Style(
        boolean hasResourcePack,
        TextColor colorPrimary,
        TextColor colorPrimaryVariant,
        TextColor colorSecondary,
        TextColor colorSecondaryVariant) {
}
