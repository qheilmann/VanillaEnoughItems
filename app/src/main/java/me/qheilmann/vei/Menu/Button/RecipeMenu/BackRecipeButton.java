package me.qheilmann.vei.Menu.Button.RecipeMenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Menu.Button.RecipeMenuButton;
import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

import java.util.List;

public class BackRecipeButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Navigate Back");
    protected List<? extends Component> lores = List.of(Component.text("Go back to the preceding recipe in the history"));
    
    protected static final String REFERENCE = "back_recipe";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new BackRecipeButton(itemStack, menu, manager));
    }

    public BackRecipeButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(BackRecipeButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected BackRecipeButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement back button
    }
}
