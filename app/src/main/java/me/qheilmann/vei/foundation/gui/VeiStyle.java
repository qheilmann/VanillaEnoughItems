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
    private final Dictionary<ActionType, ItemStack> actionItems;

    public static final VeiStyle LIGHT;
    public static final VeiStyle DARK;

    static{
        LIGHT = new VeiStyle("Light", new ItemStack(Material.WHITE_STAINED_GLASS_PANE), TextColor.color(Color.WHITE.asRGB()), TextColor.color(Color.GRAY.asRGB()), getLightStyleActionItems());
        DARK = new VeiStyle("Dark", new ItemStack(Material.BROWN_STAINED_GLASS_PANE), TextColor.color(Color.BLACK.asRGB()), TextColor.color(Color.GRAY.asRGB()), getDarkStyleActionItems());
    }

    public VeiStyle(@NotNull String name, @NotNull ItemStack paddingItem, @NotNull  TextColor Color, @NotNull  TextColor secondaryColor, @NotNull Dictionary<@NotNull ActionType, @NotNull ItemStack> actionItems) {
        this.name = name;
        this.paddingItem = paddingItem;
        this.color = Color;
        this.secondaryColor = secondaryColor;
        this.actionItems = actionItems;
    }

    public String getName() {
        return name;
    }

    public @Nullable ItemStack getActionItem(@NotNull ActionType actionType) {
        return actionItems.get(actionType);
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

    private static Dictionary<ActionType, ItemStack> getLightStyleActionItems(){
        Dictionary<ActionType, ItemStack> actionItems = new Hashtable<ActionType, ItemStack>();

        actionItems.put(ActionType.WORKBENCH_TYPE_SCROLL_LEFT, CustomHeadFactory.QUARTZ_ARROW_LEFT);
        actionItems.put(ActionType.WORKBENCH_TYPE_SCROLL_RIGHT, CustomHeadFactory.QUARTZ_ARROW_RIGHT);
        actionItems.put(ActionType.WORKBENCH_VARIANT_SCROLL_UP, CustomHeadFactory.QUARTZ_ARROW_UP);
        actionItems.put(ActionType.WORKBENCH_VARIANT_SCROLL_DOWN, CustomHeadFactory.QUARTZ_ARROW_DOWN);
        actionItems.put(ActionType.NEXT_RECIPE, CustomHeadFactory.QUARTZ_FORWARD);
        actionItems.put(ActionType.PREVIOUS_RECIPE, CustomHeadFactory.QUARTZ_BACKWORD);
        actionItems.put(ActionType.BACK_RECIPE, CustomHeadFactory.QUARTZ_BACKWARD_II);
        actionItems.put(ActionType.FORWARD_RECIPE, CustomHeadFactory.QUARTZ_FORWARD_II);
        actionItems.put(ActionType.MOVE_INGREDIENTS, CustomHeadFactory.QUARTZ_PLUS);
        actionItems.put(ActionType.QUICK_LINK, CustomHeadFactory.QUARTZ_SLASH);
        actionItems.put(ActionType.INFO, CustomHeadFactory.QUARTZ_REVERSE_EXCLAMATION_MARK);
        actionItems.put(ActionType.BOOKMARK_THIS_RECIPE, new ItemStack(Material.WHITE_CANDLE));
        actionItems.put(ActionType.BOOKMARK_LIST, CustomHeadFactory.FIREWORK_STAR_CYAN);
        actionItems.put(ActionType.BOOKMARK_SERVER_LIST, CustomHeadFactory.FIREWORK_STAR_GREEN);
        actionItems.put(ActionType.EXIT, CustomHeadFactory.QUARTZ_X);
        
        return actionItems;
    }

    private static Dictionary<ActionType, ItemStack> getDarkStyleActionItems(){
        Dictionary<ActionType, ItemStack> actionItems = new Hashtable<ActionType, ItemStack>();
        for (ActionType actionType : ActionType.values()) {
            actionItems.put(actionType, new ItemStack(Material.DIRT));
        }
        return actionItems;
    }
}
