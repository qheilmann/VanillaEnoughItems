package dev.qheilmann.vanillaenoughitems.config;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import net.kyori.adventure.text.format.TextColor;

/**
 * Configuration for Vanilla Enough Items
 * The configuration is server wide
 */
public class VanillaEnoughItemsConfig {
   
    private static final boolean DEFAULT_MISSING_IMPLEMENTATION_WARNINGS = false;
    private static final boolean DEFAULT_MISSING_RECIPE_PROCESS = true;

    private boolean missingImplementationWarnings = DEFAULT_MISSING_IMPLEMENTATION_WARNINGS;
    private boolean missingRecipeProcess = DEFAULT_MISSING_RECIPE_PROCESS;

    /**
     * Style configuration
     */
    private final Style style = new Style();

    /**
     * Checks whether to show warnings for missing implementations (Like a NonImplementedException)
     * @return true if warnings for missing implementations are shown, false otherwise
     */
    public boolean hasMissingImplementationWarnings() {
        return missingImplementationWarnings;
    }

    /**
     * Checks whether any recipe extractor, process, process panel or similar is missing
     * @return true if any recipe extractor, process, process panel or similar is missing, false otherwise
     */
    public boolean hasMissingRecipeProcess() {
        return missingRecipeProcess;
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
     * @param missingImplementationWarnings whether to show warnings for missing implementations
     * @return this config instance for method chaining
     */
    public VanillaEnoughItemsConfig setMissingImplementationWarnings(boolean missingImplementationWarnings) {
        this.missingImplementationWarnings = missingImplementationWarnings;
        return this;
    }

    /**
     * Sets whether any recipe extractor, process, process panel or similar is missing
     * @param missingRecipeProcess whether any recipe extractor, process, process panel or similar is missing
     * @return this config instance for method chaining
     */
    public VanillaEnoughItemsConfig setMissingRecipeProcess(boolean missingRecipeProcess) {
        this.missingRecipeProcess = missingRecipeProcess;
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
