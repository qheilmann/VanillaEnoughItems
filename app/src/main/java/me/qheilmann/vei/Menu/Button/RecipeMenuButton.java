package me.qheilmann.vei.Menu.Button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.Menu.RecipeMenu;

public abstract class RecipeMenuButton extends ButtonItem {
    
    public RecipeMenuButton(ItemStack item) {
        super(item);
    }

    public abstract void trigger(RecipeMenu menu, Player player);
}
