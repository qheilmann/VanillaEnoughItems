package me.qheilmann.vei.Menu.Button.RecipeMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Menu.Button.RecipeMenuButton;
import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

import java.util.List;

public class WorkbenchVariantScrollDownButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Scroll down");
    protected List<? extends Component> lores = List.of(Component.text("See next workbench variant"));
    
    protected static final String REFERENCE = "workbench_variant_scroll_down";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new WorkbenchVariantScrollDownButton(itemStack, menu, manager));
    }

    public WorkbenchVariantScrollDownButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(WorkbenchVariantScrollDownButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected WorkbenchVariantScrollDownButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }
    
    @Override
    public void trigger(InventoryClickEvent inventoryClickEvent) {
        if(!(inventoryClickEvent.getWhoClicked() instanceof Player player)){
            return;
        }
        player.openWorkbench(null, true); // TEMP implement scroll down button
    }
}
