package me.qheilmann.vei.Core.Item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class ItemBuilder {

    /**
     * Build an item instance with no tooltip
     * @param item The item
     * @return The noTooltip new item instance
     */
    public static ItemStack buildNoTooltipItem(ItemStack item) {
        return buildNoTooltipItemInternal(new ItemStack(item));
    }

    /**
     * Build an item instance with no tooltip
     * <p>
     * *IMPORTANT*: An ItemStack is only designed to contain items. Use only materials for which Material.isItem() returns false.
     * @param material The material of the item
     * @return The noToolTip item
     */
    public static ItemStack buildNoTooltipItem(Material material) {
        return buildNoTooltipItemInternal(new ItemStack(material));
    }

    private static ItemStack buildNoTooltipItemInternal(ItemStack currentItem) {
        ItemMeta meta = currentItem.getItemMeta();
        meta.displayName(Component.empty());
        meta.setMaxStackSize(1);
        meta.setHideTooltip(true);

        currentItem.setItemMeta(meta);
        return currentItem;
    }
}
