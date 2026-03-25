package dev.qheilmann.vanillaenoughitems.config;

import java.util.Locale;

import org.jspecify.annotations.NullMarked;

/**
 * Controls VEI integration with the shared ItemRegistry plugin.
 */
@NullMarked
public enum ItemRegistryMode {
    AUTO,
    REQUIRED,
    DISABLED;

    public static ItemRegistryMode fromConfigValue(String rawValue) {
        return switch (rawValue.toLowerCase(Locale.ROOT).trim()) {
            case "required" -> REQUIRED;
            case "disabled" -> DISABLED;
            default -> AUTO;
        };
    }

    public String configValue() {
        return name().toLowerCase(Locale.ROOT);
    }
}
