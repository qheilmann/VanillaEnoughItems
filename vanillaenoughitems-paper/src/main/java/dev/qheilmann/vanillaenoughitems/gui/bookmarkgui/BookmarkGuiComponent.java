package dev.qheilmann.vanillaenoughitems.gui.bookmarkgui;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.Style;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.utils.playerhead.PlayerHeadRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Factory class for creating all buttons used in the BookmarkGui.
 * Centralizes button creation logic, styling, and resource pack integration.
 */
@NullMarked
public class BookmarkGuiComponent {

    private final boolean hasResourcePack;
    private final TextColor colorPrimary;
    private final TextColor colorPrimaryVariant;

    public BookmarkGuiComponent(Style style) {
        this.hasResourcePack = style.hasResourcePack();
        this.colorPrimary = style.colorPrimary();
        this.colorPrimaryVariant = style.colorPrimaryVariant();
    }

    //#region Navigation Buttons

    public ItemStack createPreviousPageButton() {
        ItemStack item = PlayerHeadRegistry.quartzArrowLeft();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Previous Page", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.ARROW_LEFT);
            });
        }

        return item;
    }

    public ItemStack createNextPageButton() {
        ItemStack item = PlayerHeadRegistry.quartzArrowRight();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Next Page", colorPrimary).decoration(TextDecoration.ITALIC, false));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.ARROW_RIGHT);
            });
        }

        return item;
    }

    public ItemStack createReturnButton() {
        ItemStack item = PlayerHeadRegistry.quartzArrawLeftUp();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Return", colorPrimary).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text("Go back to the previous recipe", colorPrimaryVariant).decoration(TextDecoration.ITALIC, false)
            ));
        });

        if (hasResourcePack) {
            item.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Button.ARROW_RETURN);
            });
        }

        return item;
    }

    //#endregion Navigation Buttons
}
