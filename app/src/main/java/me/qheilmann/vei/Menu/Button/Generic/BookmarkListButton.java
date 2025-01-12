package me.qheilmann.vei.Menu.Button.Generic;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.Button.GenericButton;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

public class BookmarkListButton extends GenericButton {

    protected Component displayName = Component.text("Bookmark list");
    protected List<? extends Component> lores = List.of(Component.text("See your bookmarked recipes"));
    
    protected static final String REFERENCE = "bookmark_list";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new BookmarkListButton(itemStack, menu, manager));
    }

    public BookmarkListButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(BookmarkListButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected BookmarkListButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }
    
    public void trigger(Player player){
        getMenuManager().openBookmarkMenu(player);
    }
}
