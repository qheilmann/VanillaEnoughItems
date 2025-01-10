package me.qheilmann.vei.Menu.Button.Generic;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.Button.GenericButton;

public class BookmarkListButton extends GenericButton {
    public BookmarkListButton(ItemStack item) {
        super(item);
    }
    
    @Override
    public void trigger(MenuManager menuManager, Player player) {
        menuManager.openBookmarkMenu(player);
    }
}
