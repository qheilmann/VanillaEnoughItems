package me.qheilmann.vei.Menu.Button;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

public abstract class RecipeMenuButton extends ButtonItem {
    
    protected Component displayName = Component.text("undefined_recipe_menu_button");
    protected List<? extends Component> lores = List.of(Component.text("undefined_recipe_menu_button"));
    
    protected static final String REFERENCE = "undefined_recipe_menu_button";

    public RecipeMenuButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(GenericButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected RecipeMenuButton(@NotNull ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }
}
