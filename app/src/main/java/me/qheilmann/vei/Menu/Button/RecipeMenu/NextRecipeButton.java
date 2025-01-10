package me.qheilmann.vei.Menu.Button.RecipeMenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.qheilmann.vei.Menu.RecipeMenu;
import me.qheilmann.vei.Menu.Button.RecipeMenuButton;

public class NextRecipeButton extends RecipeMenuButton {

    public NextRecipeButton(ItemStack item) {
        super(item);
    }
    
    @Override
    public void trigger(RecipeMenu menu, Player player) {
        player.openWorkbench(null, true); // TEMP implement next button
    }
}
