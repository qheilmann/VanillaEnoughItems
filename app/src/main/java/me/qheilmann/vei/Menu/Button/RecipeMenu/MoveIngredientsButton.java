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

public class MoveIngredientsButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Move ingredients");
    protected List<? extends Component> lores = List.of(Component.text("Automatically move all the ingredients inside the workbench"), Component.text("This work only if a empty accessible workbench is around you"));
    
    protected static final String REFERENCE = "move_ingredients";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new MoveIngredientsButton(itemStack, menu, manager));
    }

    public MoveIngredientsButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(MoveIngredientsButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected MoveIngredientsButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement move ingredients button
    }
}
