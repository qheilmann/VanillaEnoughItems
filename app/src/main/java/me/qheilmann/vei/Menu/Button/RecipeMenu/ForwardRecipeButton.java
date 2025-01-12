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

public class ForwardRecipeButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Navigate Forward");
    protected List<? extends Component> lores = List.of(Component.text("Return to following recipe in history"));
    
    protected static final String REFERENCE = "forward_recipe";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new ForwardRecipeButton(itemStack, menu, manager));
    }

    public ForwardRecipeButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(ForwardRecipeButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected ForwardRecipeButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement forward button
    }
}
