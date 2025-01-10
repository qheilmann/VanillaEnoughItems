package me.qheilmann.vei.foundation.gui;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.annotation.Nullable;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Service.CustomHeadFactory;
import net.kyori.adventure.text.format.TextColor;

public class VeiStyle {
    private final String name;
    private final ItemStack paddingItem;
    private final TextColor color; // color used for component color
    private final TextColor secondaryColor;
    private final Dictionary<ButtonType, ItemStack> buttonItemSkins; // buttonType associated with the skin of the item

    public static final VeiStyle LIGHT;
    public static final VeiStyle DARK;

    static{
        LIGHT = new VeiStyle("Light", new ItemStack(Material.WHITE_STAINED_GLASS_PANE), TextColor.color(Color.WHITE.asRGB()), TextColor.color(Color.GRAY.asRGB()), getLightStyleButtonItems());
        DARK = new VeiStyle("Dark", new ItemStack(Material.BROWN_STAINED_GLASS_PANE), TextColor.color(Color.BLACK.asRGB()), TextColor.color(Color.GRAY.asRGB()), getDarkStyleButtonItems());
    }

    public VeiStyle(@NotNull String name, @NotNull ItemStack paddingItem, @NotNull  TextColor Color, @NotNull  TextColor secondaryColor, @NotNull Dictionary<@NotNull ButtonType, @NotNull ItemStack> buttonItemSkins) {
        this.name = name;
        this.paddingItem = paddingItem;
        this.color = Color;
        this.secondaryColor = secondaryColor;
        this.buttonItemSkins = buttonItemSkins;
    }

    public String getName() {
        return name;
    }

    public @Nullable ItemStack getButtonSkin(@NotNull ButtonType buttonType) {
        return buttonItemSkins.get(buttonType);
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

    private static Dictionary<ButtonType, ItemStack> getLightStyleButtonItems(){
        Dictionary<ButtonType, ItemStack> buttonItemSkins = new Hashtable<ButtonType, ItemStack>();

        buttonItemSkins.put(ButtonType.WORKBENCH_TYPE_SCROLL_LEFT, CustomHeadFactory.QUARTZ_ARROW_LEFT);
        buttonItemSkins.put(ButtonType.WORKBENCH_TYPE_SCROLL_RIGHT, CustomHeadFactory.QUARTZ_ARROW_RIGHT);
        buttonItemSkins.put(ButtonType.WORKBENCH_VARIANT_SCROLL_UP, CustomHeadFactory.QUARTZ_ARROW_UP);
        buttonItemSkins.put(ButtonType.WORKBENCH_VARIANT_SCROLL_DOWN, CustomHeadFactory.QUARTZ_ARROW_DOWN);
        buttonItemSkins.put(ButtonType.NEXT_RECIPE, CustomHeadFactory.QUARTZ_FORWARD);
        buttonItemSkins.put(ButtonType.PREVIOUS_RECIPE, CustomHeadFactory.QUARTZ_BACKWORD);
        buttonItemSkins.put(ButtonType.BACK_RECIPE, CustomHeadFactory.QUARTZ_BACKWARD_II);
        buttonItemSkins.put(ButtonType.FORWARD_RECIPE, CustomHeadFactory.QUARTZ_FORWARD_II);
        buttonItemSkins.put(ButtonType.MOVE_INGREDIENTS, CustomHeadFactory.QUARTZ_PLUS);
        buttonItemSkins.put(ButtonType.QUICK_LINK, CustomHeadFactory.QUARTZ_SLASH);
        buttonItemSkins.put(ButtonType.INFO, CustomHeadFactory.QUARTZ_REVERSE_EXCLAMATION_MARK);
        buttonItemSkins.put(ButtonType.BOOKMARK_THIS_RECIPE, new ItemStack(Material.WHITE_CANDLE));
        buttonItemSkins.put(ButtonType.BOOKMARK_LIST, CustomHeadFactory.FIREWORK_STAR_CYAN);
        buttonItemSkins.put(ButtonType.BOOKMARK_SERVER_LIST, CustomHeadFactory.FIREWORK_STAR_GREEN);
        buttonItemSkins.put(ButtonType.EXIT, CustomHeadFactory.QUARTZ_X);
        
        return buttonItemSkins;
    }

    private static Dictionary<ButtonType, ItemStack> getDarkStyleButtonItems(){
        Dictionary<ButtonType, ItemStack> buttonItemSkins = new Hashtable<ButtonType, ItemStack>();
        for (ButtonType buttonType : ButtonType.values()) {
            buttonItemSkins.put(buttonType, new ItemStack(Material.DIRT));
        }
        return buttonItemSkins;
    }
}
