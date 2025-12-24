package dev.qheilmann.vanillaenoughitems.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.utils.playerhead.PlayerHeadRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Factory class for creating all buttons used in the RecipeGui.
 * Centralizes button creation logic, styling, and resource pack integration.
 */
@NullMarked
public class RecipeGuiComponent {

    private final boolean hasResourcePack;
    private final TextColor colorPrimary;

    public RecipeGuiComponent(Style style) {
        this.hasResourcePack = style.hasResourcePack();
        this.colorPrimary = style.colorPrimary();
    }

    //#region Navigation Buttons

    public ItemStack createNextRecipeButton() {
        ItemStack item = PlayerHeadRegistry.quartzForward();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Next Recipe", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(new NamespacedKey(VanillaEnoughItems.NAMESPACE, "recipegui/right_arrow"));
            });
        }

        return item;
    }

    public ItemStack createPreviousRecipeButton() {
        ItemStack item = PlayerHeadRegistry.quartzBackward();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Previous Recipe", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(new NamespacedKey(VanillaEnoughItems.NAMESPACE, "recipegui/left_arrow"));
            });
        }

        return item;
    }

    public ItemStack createForwardNavigationButton() {
        ItemStack item = PlayerHeadRegistry.quartzForwardII();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Forward in History", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(new NamespacedKey(VanillaEnoughItems.NAMESPACE, "recipegui/right_arrow"));
            });
        }

        return item;
    }

    public ItemStack createBackwardNavigationButton() {
        ItemStack item = PlayerHeadRegistry.quartzBackwardII();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Backward in History", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(new NamespacedKey(VanillaEnoughItems.NAMESPACE, "recipegui/left_arrow"));
            });
        }

        return item;
    }

    //#endregion Navigation Buttons

    //#region Bookmark Buttons

    public ItemStack createBookmarkButton(boolean isBookmarked) {
        ItemStack item = isBookmarked ? ItemType.YELLOW_CANDLE.createItemStack() : ItemType.WHITE_CANDLE.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Bookmark this recipe", NamedTextColor.WHITE));
        });
        return item;
    }

    public ItemStack createBookmarkListButton() {
        ItemStack item = new ItemStack(Material.BOOKSHELF);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Your Bookmarks", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("Click to open your bookmark list", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    public ItemStack createBookmarkServerListButton() {
        ItemStack item = new ItemStack(Material.COMPASS);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Server Bookmarks", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("Click to see server-wide bookmarks", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("(Recipes shared by the community)", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    //#endregion Bookmark Buttons

    //#region Scroll Buttons

    public ItemStack createProcessScrollLeftButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Left", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more to the left", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    public ItemStack createProcessScrollRightButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Right", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more to the right", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    public ItemStack createWorkbenchScrollUpButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Up", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more above", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    public ItemStack createWorkbenchScrollDownButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Down", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more below", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    //#endregion Scroll Buttons

    //#region Utility Buttons

    public ItemStack createInfoButton() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Info", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("Click for more information", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    public ItemStack createQuickLinkButton() {
        ItemStack item = new ItemStack(Material.PAPER);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Quick Link", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("Click to get a command link", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("that opens this recipe", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    public ItemStack createQuickCraftButton() {
        ItemStack item = new ItemStack(Material.WHITE_DYE);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Quick Craft", NamedTextColor.WHITE));
        });
        return item;
    }

    //#endregion Utility Buttons

    //#region Others

    public ItemStack createFillerItem() {
        ItemStack item = ItemType.LIGHT_GRAY_STAINED_GLASS_PANE.createItemStack();
        item.editMeta(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(new NamespacedKey(VanillaEnoughItems.NAMESPACE, "common/empty"));
            });
        }
        return item;
    }

    //#endregion Others
}
