package me.qheilmann.vei.Core.Style.Styles;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.Core.Style.ButtonType.ButtonType;
import net.kyori.adventure.text.format.TextColor;

public abstract class Style {
    private StyleProfile profile;
    private ItemStack paddingItem;
    private TextColor color; // color used for component color
    private TextColor secondaryColor;
    private final Map<ButtonType, ItemStack> buttonMaterials; // if the key is not present, the null key is used, if the null key is not present, the default skin is used
    private ItemStack defaultMaterial = new ItemStack(Material.STONE);
    
    public Style(StyleProfile profile, ItemStack paddingItem, TextColor color, TextColor secondaryColor, Map<ButtonType, ItemStack> buttonMaterials) {
        this.profile = profile;
        this.paddingItem = paddingItem;
        this.color = color;
        this.secondaryColor = secondaryColor;
        this.buttonMaterials = buttonMaterials;
    }

    public StyleProfile getProfile() {
        return profile;
    }
    
    public ItemStack getPaddingItem() {
        return paddingItem;
    }

    public TextColor getColor() {
        return color;
    }

    public TextColor getSecondaryColor() {
        return secondaryColor;
    }

    public ItemStack getButtonMaterial(ButtonType buttonType) {
        return buttonMaterials.getOrDefault(buttonType, buttonMaterials.getOrDefault(null, defaultMaterial));
    }
}