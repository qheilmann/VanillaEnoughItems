package me.qheilmann.vei.Menu.Button.RecipeMenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.qheilmann.vei.Menu.RecipeMenu;
import me.qheilmann.vei.Menu.Button.RecipeMenuButton;

public class BackRecipeButton extends RecipeMenuButton {

    public BackRecipeButton(ItemStack item) {
        super(item);
    }

    @Override
    public void trigger(RecipeMenu menu, Player player) {
        player.openWorkbench(null, true); // TEMP implement back button
    }
}
