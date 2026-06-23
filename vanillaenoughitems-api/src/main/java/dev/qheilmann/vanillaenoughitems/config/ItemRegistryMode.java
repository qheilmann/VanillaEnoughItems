package dev.qheilmann.vanillaenoughitems.config;

import java.util.Locale;

import org.jspecify.annotations.NullMarked;

/**
 * Controls VEI integration with the shared ItemRegistry plugin.
 */
@NullMarked
public enum ItemRegistryMode {
    /** VEI will automatically register items with the ItemRegistry if it is present. */
    AUTO,
    /** VEI requires the ItemRegistry plugin to be present. */
    REQUIRED,
    /** VEI will not interact with the ItemRegistry plugin. */
    DISABLED;

    /**
     * Converts a configuration value to an ItemRegistryMode.
     *
     * @param rawValue the raw configuration value
     * @return the corresponding ItemRegistryMode
     */
    public static ItemRegistryMode fromConfigValue(String rawValue) {
        return switch (rawValue.toLowerCase(Locale.ROOT).trim()) {
            case "required" -> REQUIRED;
            case "disabled" -> DISABLED;
            default -> AUTO;
        };
    }

    /**
     * Converts this ItemRegistryMode to a configuration value.
     *
     * @return the corresponding configuration value
     */
    public String configValue() {
        return name().toLowerCase(Locale.ROOT);
    }
}
