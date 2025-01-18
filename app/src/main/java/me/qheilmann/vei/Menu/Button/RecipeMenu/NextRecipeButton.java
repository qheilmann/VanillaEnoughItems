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

public class NextRecipeButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Next recipe");
    protected List<? extends Component> lores = List.of(Component.text("Go to the next variation of the same recipe"));
    
    protected static final String REFERENCE = "next_recipe";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new NextRecipeButton(itemStack, menu, manager));
    }

    public NextRecipeButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(NextRecipeButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected NextRecipeButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(InventoryClickEvent inventoryClickEvent) {
        if(!(inventoryClickEvent.getWhoClicked() instanceof Player player)){
            return;
        }
        player.openWorkbench(null, true); // TEMP implement next button
    }
}
