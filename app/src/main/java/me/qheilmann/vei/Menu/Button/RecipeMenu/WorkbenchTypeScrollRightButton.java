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

public class WorkbenchTypeScrollRightButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Scroll right");
    protected List<? extends Component> lores = List.of(Component.text("See next workbench type"));
    
    protected static final String REFERENCE = "workbench_type_scroll_right";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new WorkbenchTypeScrollRightButton(itemStack, menu, manager));
    }

    public WorkbenchTypeScrollRightButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(WorkbenchTypeScrollRightButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected WorkbenchTypeScrollRightButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement scroll right button
    }
}
