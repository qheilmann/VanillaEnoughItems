package me.qheilmann.vei.Core.Style.Styles;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class StyleProfile {

    private final NamespacedKey id;
    private final String name;
    private final String description;
    private final ItemStack icon;

    /**
     * Constructs a new StyleProfile with the specified name, description, and icon.
     * <p>
     * Note: The item meta name and lore will not be used, they will be
     * overwritten by the name and description
     *
     * @param id the id of the style, must be unique
     * @param name the name of the style
     * @param description a brief description of the style
     * @param icon the icon of the style
     */
    public StyleProfile(NamespacedKey id, String name, String description, ItemStack icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    /**
     * Returns the id of the style.
     */
    public NamespacedKey getId() {
        return id;
    }

    /**
     * Returns the name of the style.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the style.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the icon of the style.
     */
    public ItemStack getIcon() {
        return icon;
    }
}
