package dev.qheilmann.vanillaenoughitems.config;

import org.jspecify.annotations.NullMarked;

/**
 * Immutable configuration snapshot for Vanilla Enough Items.
 *
 * @param debug debug settings
 * @param quickRecipeLookupEnabled enable the quick recipe-lookup feature
 * @param style visual style settings
 */
@NullMarked
public record VanillaEnoughItemsConfig(
        DebugConfig debug,
        boolean quickRecipeLookupEnabled,
        Style style) {

    // Delegate for convenience

    /** 
     * Whether the server enforces a resource pack that includes VEI custom models. 
     * 
     * @return true if the server enforces a VEI resource pack, false otherwise
     */
    public boolean hasResourcePack() {
        return style.hasResourcePack();
    }
}
