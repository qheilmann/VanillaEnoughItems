package me.qheilmann.vei.Menu.Button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.Menu.MenuManager;

public abstract class GenericButton extends ButtonItem {
    
    public GenericButton(ItemStack item) {
        super(item);
    }
    
    public abstract void trigger(MenuManager menuManager, Player player);
}
