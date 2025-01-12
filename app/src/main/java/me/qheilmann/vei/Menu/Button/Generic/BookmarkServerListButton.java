package me.qheilmann.vei.Menu.Button.Generic;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.Button.GenericButton;
import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

import java.util.List;

public class BookmarkServerListButton extends GenericButton {

    protected Component displayName = Component.text("Bookmark server list");
    protected List<? extends Component> lores = List.of(Component.text("See the server bookmarked recipes"));
    
    protected static final String REFERENCE = "bookmark_server_list";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new BookmarkServerListButton(itemStack, menu, manager));
    }

    public BookmarkServerListButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(BookmarkServerListButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected BookmarkServerListButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }
    
    @Override
    public void trigger(Player player) {
        getMenuManager().openServerBookmarkMenu(player);
    }
}
