package me.qheilmann.vei.foundation.gui;

import java.util.Dictionary;
import java.util.Hashtable;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.Service.CustomHeadFactory;
import net.kyori.adventure.text.format.TextColor;

public class VeiStyle {
    private final ItemStack paddingItem;
    private final TextColor color; // color used for component color
    private final TextColor secondaryColor;
    private final Dictionary<ActionType, ItemStack> actionItems;

    public static final VeiStyle LIGHT;
    public static final VeiStyle DARK;

    static{
        LIGHT = new VeiStyle(new ItemStack(Material.WHITE_STAINED_GLASS_PANE), TextColor.color(Color.WHITE.asRGB()), TextColor.color(Color.GRAY.asRGB()), getLightStyleActionItems());
        DARK = new VeiStyle(new ItemStack(Material.BROWN_STAINED_GLASS_PANE), TextColor.color(Color.BLACK.asRGB()), TextColor.color(Color.GRAY.asRGB()), getDarkStyleActionItems());
    }

    public VeiStyle(ItemStack paddingItem, TextColor Color, TextColor secondaryColor, Dictionary<ActionType, ItemStack> actionItems) {
        this.paddingItem = paddingItem;
        this.color = Color;
        this.secondaryColor = secondaryColor;
        this.actionItems = actionItems;
    }

    public ItemStack getActionItem(ActionType actionType) {
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
        actionItems.put(ActionType.WORKBENCH_TYPE_SCROLL_LEFT, CustomHeadFactory.WORKBENCH_TYPE_SCROLL_LEFT);
        actionItems.put(ActionType.WORKBENCH_TYPE_SCROLL_RIGHT, CustomHeadFactory.WORKBENCH_TYPE_SCROLL_RIGHT);
        actionItems.put(ActionType.WORKBENCH_VARIANT_SCROLL_UP, CustomHeadFactory.WORKBENCH_VARIANT_SCROLL_UP);
        actionItems.put(ActionType.WORKBENCH_VARIANT_SCROLL_DOWN, CustomHeadFactory.WORKBENCH_VARIANT_SCROLL_DOWN);
        actionItems.put(ActionType.NEXT_RECIPE, CustomHeadFactory.NEXT_RECIPE);
        actionItems.put(ActionType.PREVIOUS_RECIPE, CustomHeadFactory.PREVIOUS_RECIPE);
        actionItems.put(ActionType.BACK_RECIPE, CustomHeadFactory.BACK_RECIPE);
        actionItems.put(ActionType.FORWARD_RECIPE, CustomHeadFactory.FORWARD_RECIPE);
        actionItems.put(ActionType.MOVE_INGREDIENTS, CustomHeadFactory.MOVE_INGREDIENTS);
        actionItems.put(ActionType.QUICK_LINK, CustomHeadFactory.QUICK_LINK);
        actionItems.put(ActionType.INFO, CustomHeadFactory.INFO);
        actionItems.put(ActionType.BOOKMARK_THIS_RECIPE, new ItemStack(Material.WHITE_CANDLE));
        actionItems.put(ActionType.BOOKMARK_LIST, CustomHeadFactory.BOOKMARK_LIST);
        actionItems.put(ActionType.BOOKMARK_SERVER_LIST, CustomHeadFactory.BOOKMARK_SERVER_LIST);
        actionItems.put(ActionType.EXIT, CustomHeadFactory.EXIT);
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
