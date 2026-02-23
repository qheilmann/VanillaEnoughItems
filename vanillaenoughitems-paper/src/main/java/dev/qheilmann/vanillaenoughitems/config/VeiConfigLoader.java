package dev.qheilmann.vanillaenoughitems.config;

import net.kyori.adventure.text.format.TextColor;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Loads the {@link VanillaEnoughItemsConfig} from {@code config.yml} in the plugin data folder.
 */
@NullMarked
public final class VeiConfigLoader {

    public static final String CONFIG_FILE = "config.yml";

    private VeiConfigLoader() {}

    /**
     * Loads the plugin configuration from the given {@code configFile} path.
     * Creates the file with defaults if it does not exist, and back-fills any
     * missing keys into an existing file.
     *
     * @param configFile absolute path to the config file (e.g. {@code dataFolder/config.yml})
     * @return a fully populated, immutable {@link VanillaEnoughItemsConfig}
     * @throws ConfigurateException if the file cannot be read or written
     */
    @SuppressWarnings("unused") // false positive, data is actualy nullable
    public static VanillaEnoughItemsConfig load() throws ConfigurateException {

        Path configFile = Bukkit.getPluginManager().getPlugin(VanillaEnoughItems.PLUGIN_NAME).getDataFolder().toPath().resolve(VeiConfigLoader.CONFIG_FILE);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                .nodeStyle(NodeStyle.BLOCK)
                .path(configFile)
                .build();

        CommentedConfigurationNode root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new ConfigurateException("Failed to load VEI config file", e);
        }

        @Nullable PluginConfigData data = root.get(PluginConfigData.class);
        if (data == null) {
            throw new ConfigurateException("Failed to load VEI config: root node is null");
        }

        // Write defaults back so every key is always present in the file
        root.set(PluginConfigData.class, data);
        loader.save(root);

        return toApiConfig(data);
    }

    //#region Mapping

    private static VanillaEnoughItemsConfig toApiConfig(PluginConfigData data) {
        StyleData styleData = data.style;

        TextColor primary          = parseColor(styleData.colorPrimary,          Style.DEFAULT_COLOR_PRIMARY);
        TextColor primaryVariant   = parseColor(styleData.colorPrimaryVariant,   Style.DEFAULT_COLOR_PRIMARY_VARIANT);
        TextColor secondary        = parseColor(styleData.colorSecondary,        Style.DEFAULT_COLOR_SECONDARY);
        TextColor secondaryVariant = parseColor(styleData.colorSecondaryVariant, Style.DEFAULT_COLOR_SECONDARY_VARIANT);

        Style style = new Style(styleData.hasResourcePack, primary, primaryVariant, secondary, secondaryVariant);

        DebugConfig debug = new DebugConfig(
            data.debug.showStartupIndexSummary,
            data.debug.missingImplementationWarnings,
            data.debug.unhandledRecipesWarning
        );

        return new VanillaEnoughItemsConfig(debug, data.quickRecipeLookup, style);
    }

    /**
     * Parses a hex colour string (e.g. {@code "#AEA44d"}).
     * Falls back to {@code fallback} if the string is {@code null}, blank, or invalid.
     */
    private static TextColor parseColor(String hex, TextColor fallback) {
        if (hex.isBlank()) return fallback;
        TextColor parsed = TextColor.fromHexString(hex.startsWith("#") ? hex : "#" + hex);
        return parsed != null ? parsed : fallback;
    }

    //#endregion Mapping

    //#region Config Data

    @ConfigSerializable
    static final class PluginConfigData {

        // Allow players to quickly look up recipes by interacting with items in their inventory.
        @Setting("quick-recipe-lookup")
        boolean quickRecipeLookup = true;

        DebugData debug = new DebugData();

        StyleData style = new StyleData();
    }

    @ConfigSerializable
    static final class DebugData {

        // Show index summary on startup
        @Setting("show-startup-index-summary")
        boolean showStartupIndexSummary = false;

        // Warn when a feature has no implementation (e.g. missing TrimMaterial mapping).
        // Useful during development.
        @Setting("missing-implementation-warnings")
        boolean missingImplementationWarnings = false;

        // Log a warning for every recipe type that has no registered extractor.
        // Enable during development to audit missing recipe support.
        @Setting("unhandled-recipes-warning")
        boolean unhandledRecipesWarning = false;
    }

    @ConfigSerializable
    static final class StyleData {

        // Whether the server enforces a resource pack that includes VEI custom models.
        // When true, model-based GUI elements are used instead of text fallbacks.
        @Setting("has-resource-pack")
        boolean hasResourcePack = false;

        // Primary colour used for titles and highlights (hex, e.g. "#AEA44d").
        @Setting("color-primary")
        String colorPrimary = "#AEA44d";

        // A slightly darker/lighter variant of the primary colour (hex).
        @Setting("color-primary-variant")
        String colorPrimaryVariant = "#959956";

        // Secondary / accent colour (hex).
        @Setting("color-secondary")
        String colorSecondary = "#33658A";

        // A lighter variant of the secondary colour (hex).
        @Setting("color-secondary-variant")
        String colorSecondaryVariant = "#86BBD8";
    }

    //#endregion Config Data
}
