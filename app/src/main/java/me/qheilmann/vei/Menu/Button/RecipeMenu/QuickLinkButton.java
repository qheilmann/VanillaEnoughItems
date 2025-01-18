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

public class QuickLinkButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Quick link");
    protected List<? extends Component> lores = List.of(Component.text("click to get the command equivalent for go to this recipe"), Component.text("/recipe <myRecipe> <category>"));
    
    protected static final String REFERENCE = "quick_link";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new QuickLinkButton(itemStack, menu, manager));
    }

    public QuickLinkButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(QuickLinkButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected QuickLinkButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(InventoryClickEvent inventoryClickEvent) {
        if(!(inventoryClickEvent.getWhoClicked() instanceof Player player)){
            return;
        }
        player.openWorkbench(null, true); // TEMP implement quick link button
    }
}
