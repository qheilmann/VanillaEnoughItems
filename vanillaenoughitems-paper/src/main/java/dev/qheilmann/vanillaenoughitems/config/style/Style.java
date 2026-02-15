package dev.qheilmann.vanillaenoughitems.config.style;

import net.kyori.adventure.text.format.TextColor;

/**
 * Style configuration for Vanilla Enough Items
 * Handles visual appearance including colors and resource pack settings
 */
public class Style {

    private static final boolean DEFAULT_HAS_RESOURCE_PACK = false;
    private static final TextColor DEFAULT_COLOR_PRIMARY = TextColor.fromHexString("#AEA44d");
    private static final TextColor DEFAULT_COLOR_PRIMARY_VARIANT = TextColor.fromHexString("#959956");
    private static final TextColor DEFAULT_COLOR_SECONDARY = TextColor.fromHexString("#33658A");
    private static final TextColor DEFAULT_COLOR_SECONDARY_VARIANT = TextColor.fromHexString("#86BBD8");
    
    private boolean hasResourcePack = DEFAULT_HAS_RESOURCE_PACK;
    private TextColor colorPrimary = DEFAULT_COLOR_PRIMARY;
    private TextColor colorPrimaryVariant = DEFAULT_COLOR_PRIMARY_VARIANT;
    private TextColor colorSecondary = DEFAULT_COLOR_SECONDARY;
    private TextColor colorSecondaryVariant = DEFAULT_COLOR_SECONDARY_VARIANT;

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

    // Setters

    /**
     * Sets whether the server forces resource packs with VEI models.
     * Default is {@link #DEFAULT_HAS_RESOURCE_PACK}.
     * @param hasResourcePack whether the server forces resource packs with VEI models
     * @return this style instance for method chaining
     */
    public Style setHasResourcePack(boolean hasResourcePack) {
        this.hasResourcePack = hasResourcePack;
        return this;
    }

    /**
     * Sets all colors.
     * Defaults are:
     * <ul>
     * <li>Primary: {@link #DEFAULT_COLOR_PRIMARY}</li>
     * <li>Primary Variant: {@link #DEFAULT_COLOR_PRIMARY_VARIANT}</li>
     * <li>Secondary: {@link #DEFAULT_COLOR_SECONDARY}</li>
     * <li>Secondary Variant: {@link #DEFAULT_COLOR_SECONDARY_VARIANT}</li>
     * </ul>
     * @param primary the primary color
     * @param primaryVariant the primary variant color
     * @param secondary the secondary color
     * @param secondaryVariant the secondary variant color
     * @return this style instance for method chaining
     */
    public Style setColors(TextColor primary, TextColor primaryVariant, TextColor secondary, TextColor secondaryVariant) {
        this.colorPrimary = primary;
        this.colorPrimaryVariant = primaryVariant;
        this.colorSecondary = secondary;
        this.colorSecondaryVariant = secondaryVariant;
        return this;
    }
}
