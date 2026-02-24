package dev.qheilmann.vanillaenoughitems.config;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jspecify.annotations.NullMarked;

/**
 * Immutable debug configuration for Vanilla Enough Items.
 *
 * @param showStartupIndexSummary Show a summary of the recipe index after it is loaded on startup
 * @param missingImplementationWarnings Warn when a feature has no implementation (e.g. missing {@link TrimMaterial} mapping)
 * @param unhandledRecipesWarning Log a warning for every recipe type that has no registered extractor
 */
@NullMarked
public record DebugConfig(
    boolean showStartupIndexSummary,
    boolean missingImplementationWarnings,
    boolean unhandledRecipesWarning) {}
