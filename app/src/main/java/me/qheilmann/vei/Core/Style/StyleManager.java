package me.qheilmann.vei.Core.Style;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.NamespacedKey;

import me.qheilmann.vei.Core.Style.Styles.DarkStyle;
import me.qheilmann.vei.Core.Style.Styles.LightStyle;
import me.qheilmann.vei.Core.Style.Styles.Style;

public class StyleManager {
    public static final Style DEFAULT_STYLE = LightStyle.STYLE;
    
    private final Map<NamespacedKey, Style> styles = new HashMap<>();

    public StyleManager() {
        registerStyle(LightStyle.STYLE);
        registerStyle(DarkStyle.STYLE);
    }

    public void registerStyle(@NotNull Style style) {
        styles.put(style.getProfile().getId(), style);
    }

    public void unregisterStyle(@NotNull NamespacedKey styleId) {
        styles.remove(styleId);
    }

    public void unregisterAllStyles() {
        styles.clear();
    }

    /**
     * Returns the style with the specified id, or null if the style does not exist.
     */
    @Nullable
    public Style getStyle(NamespacedKey styleId) {
        return styles.getOrDefault(styleId, null);
    }

    @NotNull
    public Map<NamespacedKey,Style> getAllStyle() {
        return Collections.unmodifiableMap(styles);
    }
}