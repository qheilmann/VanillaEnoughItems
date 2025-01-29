package me.qheilmann.vei.Core.Style.Styles;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.Style.ButtonType.ButtonType;
import net.kyori.adventure.text.format.TextColor;

public abstract class Style {
    private StyleProfile profile;
    private ItemStack paddingItem;
    private TextColor primaryColor; // color used for component color
    private TextColor secondaryColor;
    private final Map<ButtonType, ItemStack> buttonMaterials; // if the key is not present, the null key is used, if the null key is not present, the default skin is used
    private ItemStack defaultMaterial = new ItemStack(Material.STONE);
    
    public Style(@NotNull StyleProfile profile, @NotNull ItemStack paddingItem, @NotNull TextColor color, @NotNull TextColor secondaryColor, @NotNull Map<ButtonType, @NotNull ItemStack> buttonMaterials) {
        Preconditions.checkArgument(profile != null, "profile cannot be null");
        Preconditions.checkArgument(paddingItem != null, "paddingItem cannot be null");
        Preconditions.checkArgument(color != null, "color cannot be null");
        Preconditions.checkArgument(secondaryColor != null, "secondaryColor cannot be null");
        Preconditions.checkArgument(buttonMaterials != null, "buttonMaterials cannot be null");
        for (Map.Entry<ButtonType, ItemStack> entry : buttonMaterials.entrySet()) {
            Preconditions.checkArgument(entry.getValue() != null, "buttonMaterials cannot contain null values");
        }
        
        this.profile = profile;
        this.paddingItem = paddingItem;
        this.primaryColor = color;
        this.secondaryColor = secondaryColor;
        this.buttonMaterials = buttonMaterials;
    }

    @NotNull
    public StyleProfile getProfile() {
        return profile;
    }
    
    @NotNull
    public TextColor getPrimaryColor() {
        return primaryColor;
    }

    @NotNull
    public TextColor getSecondaryColor() {
        return secondaryColor;
    }

    @NotNull
    public ItemStack getButtonMaterial(ButtonType buttonType) {
        return buttonMaterials.getOrDefault(buttonType, buttonMaterials.getOrDefault(null, defaultMaterial));
    }

    @NotNull
    public ItemStack getPaddingItem() {
        return paddingItem;
    }
}