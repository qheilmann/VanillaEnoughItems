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

public class BookmarkThisRecipeButton extends RecipeMenuButton {

    protected Component displayName = Component.text("Bookmark this recipe");
    protected List<? extends Component> lores = List.of(Component.text("Add this recipe to your bookmark"));
    
    protected static final String REFERENCE = "bookmark_this_recipe";

    static {
        registerButtonItem(REFERENCE, (itemStack, menu, manager) -> new BookmarkThisRecipeButton(itemStack, menu, manager));
    }

    public BookmarkThisRecipeButton(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(BookmarkThisRecipeButton.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected BookmarkThisRecipeButton(ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin, ownerMenu, menuManager);
    }

    @Override
    public void trigger(Player player) {
        player.openWorkbench(null, true); // TEMP implement bookmark button
    }
}
