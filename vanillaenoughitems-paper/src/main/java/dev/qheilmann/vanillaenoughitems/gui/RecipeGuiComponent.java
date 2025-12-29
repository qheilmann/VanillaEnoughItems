package dev.qheilmann.vanillaenoughitems.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.utils.playerhead.PlayerHeadRegistry;
import net.kyori.adventure.text.Component;
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
    private final TextColor colorPrimaryVariant;

    public RecipeGuiComponent(Style style) {
        this.hasResourcePack = style.hasResourcePack();
        this.colorPrimary = style.colorPrimary();
        this.colorPrimaryVariant = style.colorPrimaryVariant();
    }

    //#region Navigation Buttons

    public ItemStack createNextRecipeButton() {
        ItemStack item = PlayerHeadRegistry.quartzForward();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Next Recipe", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.FORWARD);
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
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.BACKWARD);
            });
        }

        return item;
    }

    public ItemStack createForwardNavigationButton() {
        ItemStack item = PlayerHeadRegistry.quartzForwardDouble();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Forward in History", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.FORWARD_DOUBLE);
            });
        }

        return item;
    }

    public ItemStack createBackwardNavigationButton() {
        ItemStack item = PlayerHeadRegistry.quartzBackwardDouble();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Backward in History", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.BACKWARD_DOUBLE);
            });
        }

        return item;
    }

    //#endregion Navigation Buttons

    //#region Bookmark Buttons

    public ItemStack createBookmarkButton(boolean isBookmarked) {
        ItemStack item = isBookmarked ? ItemType.ORANGE_CANDLE.createItemStack() : ItemType.WHITE_CANDLE.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Bookmark this recipe", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                if (isBookmarked) {
                    meta.setItemModel(VeiPack.ItemModel.Gui.Button.BOOKMARK_UNBOOKMARK);
                } else {
                    meta.setItemModel(VeiPack.ItemModel.Gui.Button.BOOKMARK_BOOKMARK);
                }
            });
        }

        return item;
    }

    public ItemStack createBookmarkListButton() {
        ItemStack item = new ItemStack(Material.BOOKSHELF);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Your Bookmarks", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("Click to open your bookmark list", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.BOOKMARK_LIST);
            });
        }

        return item;
    }

    public ItemStack createBookmarkServerListButton() {
        ItemStack item = new ItemStack(Material.COMPASS);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Server Bookmarks", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("Click to see server-wide bookmarks", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.BOOKMARK_SERVER);
            });
        }

        return item;
    }

    //#endregion Bookmark Buttons

    //#region Scroll Buttons

    public ItemStack createProcessScrollLeftButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Left", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more to the left", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.ARROW_LEFT);
            });
        }

        return item;
    }

    public ItemStack createProcessScrollRightButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Right", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more to the right", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.ARROW_RIGHT);
            });
        }

        return item;
    }

    public ItemStack createWorkbenchScrollUpButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Up", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more above", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.ARROW_UP);
            });
        }

        return item;
    }

    public ItemStack createWorkbenchScrollDownButton(int moreCount) {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Down", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("+ " + moreCount + " more below", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.ARROW_DOWN);
            });
        }

        return item;
    }

    //#endregion Scroll Buttons

    //#region Utility Buttons

    public ItemStack createInfoButton() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Info", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("Click for more information", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.INFO);
            });
        }

        return item;
    }

    public ItemStack createQuickLinkButton() {
        ItemStack item = new ItemStack(Material.PAPER);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Quick Link", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("Click to get a command link", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false),
                Component.text("that opens this recipe", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.QUICKLINK);
            });
        }

        return item;
    }

    public ItemStack createQuickCraftButton() {
        ItemStack item = new ItemStack(Material.WHITE_DYE);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Quick Craft", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.PLUS);
            });
        }

        return item;
    }

    //#endregion Utility Buttons

    //#region Others

    public ItemStack createFillerItem() {
        return createFillerItem(hasResourcePack);
    }

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

    //#endregion Others
}
