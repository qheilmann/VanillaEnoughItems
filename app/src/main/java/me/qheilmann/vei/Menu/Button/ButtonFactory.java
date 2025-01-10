package me.qheilmann.vei.Menu.Button;

import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.Menu.Button.Generic.BookmarkListButton;
import me.qheilmann.vei.Menu.Button.Generic.BookmarkServerListButton;
import me.qheilmann.vei.Menu.Button.Generic.ExitButton;
import me.qheilmann.vei.Menu.Button.Generic.InfoButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.BackRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.BookmarkThisRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.ForwardRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.MoveIngredientsButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.NextRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.PreviousRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.QuickLinkButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.UnbookmarkThisRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchTypeScrollLeftButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchTypeScrollRightButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchVariantScrollDownButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchVariantScrollUpButton;
import me.qheilmann.vei.foundation.gui.ButtonType;

public class ButtonFactory {
    public static ButtonItem createButton(ButtonType type, ItemStack skin) {
        switch (type) {
            // Generic
            case BOOKMARK_LIST:
                return new BookmarkListButton(skin);
            case BOOKMARK_SERVER_LIST:
                return new BookmarkServerListButton(skin);
            case EXIT:
                return new ExitButton(skin);
            case INFO:
                return new InfoButton(skin);
                
            // Recipe Menu
            case BACK_RECIPE:
                return new BackRecipeButton(skin);
            case BOOKMARK_THIS_RECIPE:
                return new BookmarkThisRecipeButton(skin);
            case FORWARD_RECIPE:
                return new ForwardRecipeButton(skin);
            case MOVE_INGREDIENTS:
                return new MoveIngredientsButton(skin);
            case NEXT_RECIPE:
                return new NextRecipeButton(skin);
            case PREVIOUS_RECIPE:
                return new PreviousRecipeButton(skin);
            case QUICK_LINK:
                return new QuickLinkButton(skin);
            case UNBOOKMARK_THIS_RECIPE:
                return new UnbookmarkThisRecipeButton(skin);
            case WORKBENCH_TYPE_SCROLL_LEFT:
                return new WorkbenchTypeScrollLeftButton(skin);
            case WORKBENCH_TYPE_SCROLL_RIGHT:
                return new WorkbenchTypeScrollRightButton(skin);
            case WORKBENCH_VARIANT_SCROLL_DOWN:
                return new WorkbenchVariantScrollDownButton(skin);
            case WORKBENCH_VARIANT_SCROLL_UP:
                return new WorkbenchVariantScrollUpButton(skin);
            default:
                throw new IllegalArgumentException("Unknown button type: " + type);
        }
    }
}
