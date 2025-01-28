package me.qheilmann.vei.Core.Style;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;

import me.qheilmann.vei.Core.Style.Styles.DarkStyle;
import me.qheilmann.vei.Core.Style.Styles.LightStyle;
import me.qheilmann.vei.Core.Style.Styles.Style;

public class StyleManager {
    private final Map<NamespacedKey, Style> styles = new HashMap<>();
    public static final Style DEFAULT_STYLE = LightStyle.STYLE;

    public StyleManager() {
        registerStyle(LightStyle.STYLE);
        registerStyle(DarkStyle.STYLE);
    }

    public void registerStyle(Style style) {
        styles.put(style.getProfile().getId(), style);
    }

    public void unregisterStyle(NamespacedKey styleId) {
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

    public Style[] getAllStyle() {
        return styles.values().toArray(new Style[0]);
    }
}


// TODO set the nullable annotation on all style class (a least the public ones)