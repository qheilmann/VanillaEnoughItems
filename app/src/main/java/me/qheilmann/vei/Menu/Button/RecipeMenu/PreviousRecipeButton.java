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

public class PreviousRecipeButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Previous recipe");
    protected List<? extends Component> lores = List.of(Component.text("Go to the previous variation of the same recipe"));
    
    protected static final String REFERENCE = "previous_recipe";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new PreviousRecipeButton(itemStack, menu, manager));
    }

    public PreviousRecipeButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(PreviousRecipeButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected PreviousRecipeButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
        public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement previous recipe button
    }
}
