package me.qheilmann.vei.Menu.Button.Generic;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.Button.GenericButton;
import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

import java.util.List;

public class InfoButton extends GenericButton {

    protected Component displayName = Component.text("Info");
    protected List<? extends Component> lores = List.of(Component.text("See VEI info"));
    
    protected static final String REFERENCE = "info";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new InfoButton(itemStack, menu, manager));
    }

    public InfoButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(InfoButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected InfoButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }
    
    @Override
    public void trigger(InventoryClickEvent inventoryClickEvent) {
        if(!(inventoryClickEvent.getWhoClicked() instanceof Player player)){
            return;
        }
        getMenuManager().openSettingsMenu(player);
    }
}
