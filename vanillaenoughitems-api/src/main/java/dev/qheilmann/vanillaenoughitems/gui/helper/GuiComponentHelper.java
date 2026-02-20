package dev.qheilmann.vanillaenoughitems.gui.helper;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.utils.VeiKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Helper for creating common GUI component items used in process panels and other interfaces.
 * <p>
 * This class is part of the API to allow addon developers to create consistent UI elements.
 */
@NullMarked
public class GuiComponentHelper {
    
    /**
     * VanillaEnoughItems empty model key for transparent item rendering in GUIs.
     */
    private static final NamespacedKey EMPTY_MODEL = VeiKey.namespacedKey("common/empty");

    private GuiComponentHelper() {} // Static helper

    /**
     * Creates a filler item for GUI backgrounds and spacing.
     * <p>
     * When resource pack support is disabled, this returns a light gray stained glass pane.
     * When resource pack support is enabled, it applies the vanilla empty model for a transparent appearance.
     * 
     * @param hasResourcePack whether to apply the empty model (transparent rendering)
     * @return configured filler ItemStack
     */
    public static ItemStack createFillerItem(boolean hasResourcePack) {
        ItemStack item = ItemType.LIGHT_GRAY_STAINED_GLASS_PANE.createItemStack();
        item.editMeta(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (hasResourcePack) {
            item.editMeta(meta -> meta.setItemModel(EMPTY_MODEL));
        }

        return item;
    }

    /**
     * Creates an error indicator item with a custom error message.
     * <p>
     * Displayed as a barrier block with red text.
     * 
     * @param message the error message to display
     * @return configured error ItemStack
     */
    public static ItemStack createErrorItem(String message) {
        Component text = Component.text(message, NamedTextColor.RED);
        
        ItemStack item = ItemType.BARRIER.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(text);
            meta.setMaxStackSize(1);
        });
        return item;
    }
}
