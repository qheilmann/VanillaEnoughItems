package dev.qheilmann.vanillaenoughitems.gui.helper;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GuiComponentHelper {

    public static ItemStack createFillerItem(boolean hasResourcePack) {
        ItemStack item = ItemType.LIGHT_GRAY_STAINED_GLASS_PANE.createItemStack();
        item.editMeta(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Common.EMPTY);
            });
        }

        return item;
    }

    public static ItemStack createErrorItem(String string) {
        Component text = Component.text(string, NamedTextColor.RED);
        
        ItemStack item = ItemType.BARRIER.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(text);
            meta.setMaxStackSize(1);
        });
        return item;
    }
}
