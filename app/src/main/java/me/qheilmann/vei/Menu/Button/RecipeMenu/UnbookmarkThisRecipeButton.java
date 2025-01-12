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

public class UnbookmarkThisRecipeButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Unbookmark this recipe");
    protected List<? extends Component> lores = List.of(Component.text("Remove this recipe from your bookmark"));
    
    protected static final String REFERENCE = "unbookmark_this_recipe";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new UnbookmarkThisRecipeButton(itemStack, menu, manager));
    }

    public UnbookmarkThisRecipeButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(UnbookmarkThisRecipeButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected UnbookmarkThisRecipeButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
        public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement unbookmark button
    }
}
