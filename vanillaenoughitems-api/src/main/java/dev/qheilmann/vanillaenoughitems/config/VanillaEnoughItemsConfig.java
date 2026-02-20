package dev.qheilmann.vanillaenoughitems.config;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import net.kyori.adventure.text.format.TextColor;

/**
 * Configuration for Vanilla Enough Items
 * The configuration is server wide
 */
public class VanillaEnoughItemsConfig {
   
    private static final boolean DEFAULT_DEBUG_MISSING_IMPLEMENTATION_WARNINGS = false;
    private static final boolean DEFAULT_DEBUG_UNHANDLED_RECIPES_WARNING = false;

    private boolean debugMissingImplementationWarnings = DEFAULT_DEBUG_MISSING_IMPLEMENTATION_WARNINGS;
    private boolean debugUnhandledRecipesWarning = DEFAULT_DEBUG_UNHANDLED_RECIPES_WARNING;
    private boolean isQuickRecipeLookupEnabled = true;
    private final Style style = new Style();

    /**
     * Checks whether to show warnings for missing implementations (Like a NonImplementedException)
     * @return true if warnings for missing implementations are shown, false otherwise
     */
    public boolean debugMissingImplementationWarning() {
        return debugMissingImplementationWarnings;
    }

    /**
     * Checks whether to log debug messages for recipes without registered extractors.
     * Useful during development to identify new recipe types that need extractors.
     * @return true to log unhandled recipes, false to skip silently (default)
     */
    public boolean debugUnhandledRecipesWarning() {
        return debugUnhandledRecipesWarning;
    }

    public boolean isQuickRecipeLookupEnabled() {
        return isQuickRecipeLookupEnabled;
    }

    /**
     * Gets the style configuration
     * @return the style configuration object
     */
    public Style style() {
        return style;
    }
    
    /**
     * Checks whether the server forces resource packs with VEI models
     * @return true if the server forces resource packs with VEI models, false otherwise
     */
    public boolean hasRessourcePack() {
        return style.hasResourcePack();
    }

    /**
     * Gets the primary color
     * @return the primary color
     */
    public TextColor colorPrimary() {
        return style.colorPrimary();
    }

    /**
     * Gets the primary variant color
     * @return the primary variant color
     */
    public TextColor colorPrimaryVariant() {
        return style.colorPrimaryVariant();
    }

    /**
     * Gets the secondary color
     * @return the secondary color
     */
    public TextColor colorSecondary() {
        return style.colorSecondary();
    }

    /**
     * Gets the secondary variant color
     * @return the secondary variant color
     */
    public TextColor colorSecondaryVariant() {
        return style.colorSecondaryVariant();
    }

    // Setters

    /**
     * Sets whether to show warnings for missing implementations (Like a NonImplementedException)
     * @param debugMissingImplementationWarnings whether to show warnings for missing implementations
     * @return this config instance for method chaining
     */
    public VanillaEnoughItemsConfig setDebugMissingImplementationWarnings(boolean debugMissingImplementationWarnings) {
        this.debugMissingImplementationWarnings = debugMissingImplementationWarnings;
        return this;
    }

    /**
     * Sets whether to log debug messages for recipes without registered extractors.
     * Enable this during development to audit which recipe types are not being handled.
     * @param debugUnhandledRecipesWarning true to log unhandled recipes, false to skip silently
     * @return this config instance for method chaining
     */
    public VanillaEnoughItemsConfig setDebugUnhandledRecipesWarning(boolean debugUnhandledRecipesWarning) {
        this.debugUnhandledRecipesWarning = debugUnhandledRecipesWarning;
        return this;
    }

    /**
     * Enables or disables the quick recipe lookup feature
     * @param isQuickRecipeLookupEnabled whether the quick recipe lookup feature is enabled
     * @return this config instance for method chaining
     */    
    public VanillaEnoughItemsConfig enableQuickRecipeLookup(boolean isQuickRecipeLookupEnabled) {
        this.isQuickRecipeLookupEnabled = isQuickRecipeLookupEnabled;
        return this;
    }

    /**
     * Sets the style configuration, copying all values from the given style
     * @param style the style configuration to set
     * @return this config instance for method chaining
     */
    public VanillaEnoughItemsConfig setStyle(Style style) {
        this.style.setHasResourcePack(style.hasResourcePack());
        this.style.setColors(
            style.colorPrimary(),
            style.colorPrimaryVariant(),
            style.colorSecondary(),
            style.colorSecondaryVariant()
        );
        return this;
    }
}
