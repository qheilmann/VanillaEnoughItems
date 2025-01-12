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

public class WorkbenchTypeScrollLeftButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Scroll left");
    protected List<? extends Component> lores = List.of(Component.text("See previous workbench type"));
    
    protected static final String REFERENCE = "workbench_type_scroll_left";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new WorkbenchTypeScrollLeftButton(itemStack, menu, manager));
    }

    public WorkbenchTypeScrollLeftButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(WorkbenchTypeScrollLeftButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected WorkbenchTypeScrollLeftButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement scroll left button
    }
}
