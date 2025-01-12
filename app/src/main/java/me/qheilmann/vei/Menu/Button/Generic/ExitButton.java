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

public class ExitButton extends GenericButton {

    protected Component displayName = Component.text("Exit");
    protected List<? extends Component> lores = List.of(Component.text("Exit the recipe menu"));
    
    protected static final String REFERENCE = "exit";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new ExitButton(itemStack, menu, manager));
    }

    public ExitButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(ExitButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected ExitButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        getMenuManager().closeMenu(player);
    }
}
