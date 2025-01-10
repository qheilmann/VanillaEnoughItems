package me.qheilmann.vei.Menu.Button.Generic;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.Button.GenericButton;

public class BookmarkServerListButton extends GenericButton {
    public BookmarkServerListButton(ItemStack item) {
        super(item);
    }
    
    @Override
    public void trigger(MenuManager menuManager, Player player) {
        menuManager.openServerBookmarkMenu(player);
    }
}
