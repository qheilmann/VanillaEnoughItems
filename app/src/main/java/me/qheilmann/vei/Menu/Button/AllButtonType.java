package me.qheilmann.vei.Menu.Button;

import java.util.Locale;

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
// TODO remove thiss
public enum AllButtonType {
    // Generic
    BOOKMARK_LIST(BookmarkListButton.class),
    BOOKMARK_SERVER_LIST(BookmarkServerListButton.class),
    EXIT(ExitButton.class),
    INFO(InfoButton.class),
    // RecipeMenu
    BACK_RECIPE(BackRecipeButton.class),
    BOOKMARK_THIS_RECIPE(BookmarkThisRecipeButton.class),
    FORWARD_RECIPE(ForwardRecipeButton.class),
    MOVE_INGREDIENTS(MoveIngredientsButton.class),
    NEXT_RECIPE(NextRecipeButton.class),
    PREVIOUS_RECIPE(PreviousRecipeButton.class),
    QUICK_LINK(QuickLinkButton.class),
    UNBOOKMARK_THIS_RECIPE(UnbookmarkThisRecipeButton.class),
    WORKBENCH_TYPE_SCROLL_LEFT(WorkbenchTypeScrollLeftButton.class),
    WORKBENCH_TYPE_SCROLL_RIGHT(WorkbenchTypeScrollRightButton.class),
    WORKBENCH_VARIANT_SCROLL_DOWN(WorkbenchVariantScrollDownButton.class),
    WORKBENCH_VARIANT_SCROLL_UP(WorkbenchVariantScrollUpButton.class);

    private final Class<? extends ButtonItem> buttonClass;

    public static final String REFERENCE_KEY = "recipe_action";

    AllButtonType(Class<? extends ButtonItem> buttonClass) {
        this.buttonClass = buttonClass;
    }

    public Class<? extends ButtonItem> getButtonClass() {
        return buttonClass;
    }

    public String getReference() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public static AllButtonType fromReference(String reference) {
        reference = reference.toLowerCase(Locale.ROOT);
        // PERF improve the toString() method, (dont forget the getRef() methode) (use an array of strings)
        for (AllButtonType buttonType : AllButtonType.values()) {
            if (buttonType.toString().toLowerCase(Locale.ROOT).equals(reference)) { 
                return buttonType;
            }
        }
        throw new IllegalArgumentException("No ButtonType with reference " + reference + " found.");
    }

    public static AllButtonType fromClass(Class<? extends ButtonItem> buttonClass) {
        for (AllButtonType buttonType : AllButtonType.values()) {
            if (buttonType.buttonClass == buttonClass) {
                return buttonType;
            }
        }
        throw new IllegalArgumentException("No ButtonType with class " + buttonClass + " found.");
    }
}
