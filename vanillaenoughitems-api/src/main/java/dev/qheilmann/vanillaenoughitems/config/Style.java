package dev.qheilmann.vanillaenoughitems.config;

import org.jspecify.annotations.NullMarked;

import net.kyori.adventure.text.format.TextColor;

/**
 * Immutable style configuration for Vanilla Enough Items.
 * Handles visual appearance including colors and resource-pack settings.
 */
@NullMarked
public final class Style {

    public static final boolean DEFAULT_HAS_RESOURCE_PACK = false;
    public static final TextColor DEFAULT_COLOR_PRIMARY = TextColor.fromHexString("#AEA44d");
    public static final TextColor DEFAULT_COLOR_PRIMARY_VARIANT = TextColor.fromHexString("#959956");
    public static final TextColor DEFAULT_COLOR_SECONDARY = TextColor.fromHexString("#33658A");
    public static final TextColor DEFAULT_COLOR_SECONDARY_VARIANT = TextColor.fromHexString("#86BBD8");

    private final boolean hasResourcePack;
    private final TextColor colorPrimary;
    private final TextColor colorPrimaryVariant;
    private final TextColor colorSecondary;
    private final TextColor colorSecondaryVariant;

    /** Creates a {@code Style} with all default values. */
    public Style() {
        this(DEFAULT_HAS_RESOURCE_PACK,
             DEFAULT_COLOR_PRIMARY,
             DEFAULT_COLOR_PRIMARY_VARIANT,
             DEFAULT_COLOR_SECONDARY,
             DEFAULT_COLOR_SECONDARY_VARIANT);
    }

    /**
     * Creates a {@code Style} with explicit values.
     *
     * @param hasResourcePack       whether the server enforces a VEI resource pack
     * @param colorPrimary          primary highlight colour
     * @param colorPrimaryVariant   primary variant colour
     * @param colorSecondary        secondary / accent colour
     * @param colorSecondaryVariant secondary variant colour
     */
    public Style(boolean hasResourcePack,
                 TextColor colorPrimary,
                 TextColor colorPrimaryVariant,
                 TextColor colorSecondary,
                 TextColor colorSecondaryVariant) {
        this.hasResourcePack       = hasResourcePack;
        this.colorPrimary          = colorPrimary;
        this.colorPrimaryVariant   = colorPrimaryVariant;
        this.colorSecondary        = colorSecondary;
        this.colorSecondaryVariant = colorSecondaryVariant;
    }

    /**
     * Checks whether the server forces resource packs with VEI models
     * @return true if the server forces resource packs with VEI models, false otherwise
     */
    public boolean hasResourcePack() {
        return hasResourcePack;
    }

    /**
     * Gets the primary color
     * @return the primary color
     */
    public TextColor colorPrimary() {
        return colorPrimary;
    }

    /**
     * Gets the primary variant color
     * @return the primary variant color
     */
    public TextColor colorPrimaryVariant() {
        return colorPrimaryVariant;
    }
    
    /**
     * Gets the secondary color
     * @return the secondary color
     */
    public TextColor colorSecondary() {
        return colorSecondary;
    }

    /**
     * Gets the secondary variant color
     * @return the secondary variant color
     */
    public TextColor colorSecondaryVariant() {
        return colorSecondaryVariant;
    }
}
