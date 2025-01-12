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

public class WorkbenchVariantScrollUpButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Scroll up");
    protected List<? extends Component> lores = List.of(Component.text("See previous workbench variant"));
    
    protected static final String REFERENCE = "workbench_variant_scroll_up";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new WorkbenchVariantScrollUpButton(itemStack, menu, manager));
    }

    public WorkbenchVariantScrollUpButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(WorkbenchVariantScrollUpButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected WorkbenchVariantScrollUpButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement scroll up button
    }
}
