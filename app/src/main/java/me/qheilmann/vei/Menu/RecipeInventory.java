package me.qheilmann.vei.Menu;

import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Menu.RecipeView.IRecipeView;
import me.qheilmann.vei.Menu.RecipeView.RecipeViewFactory;
import me.qheilmann.vei.foundation.gui.ActionType;
import me.qheilmann.vei.foundation.gui.GuiItemService;
import me.qheilmann.vei.foundation.gui.VeiStyle;

/**
 * <h1>RecipeMenu</h1>
 * This class is used to display the recipe menu (9x6) in a GUI.
 * <p>
 * GUI representation(with ShapedRecipeView):
 * 
 * <pre>
 *  
 *-> x 0  1  2  3  4  5  6  7  8
 * y +---------------------------+
 * 0 | #  <  t  t  t        >  i |
 * 1 | ^  1           2          |
 * 2 | a     g  g  g           b |
 * 3 | a     g  g  g  w  o     l |
 * 4 |       g  g  g     +     s |
 * 5 | v     3     4           e |
 *   +---------------------------+
 * </pre>
 * <ul>
 * <li>#: quick link to crafting</li>
 * <li><, >: workbench type scroll</li>
 * <li>t: workbench type</li>
 * <li>^, v: workbench variant scroll</li>
 * <li>i: info</li>
 * <li>a: workbench variant</li>
 * <li>b: bookmark this recipe</li>
 * <li>l: bookmark list</li>
 * <li>s: bookmark server list</li>
 * <li>e: exit</li>
 * </ul>
 * Recipeview example (ShapedRecipeView):
 * <ul>
 * <li>g: inputs (crafting grid)</li>
 * <li>o: outputs</li>
 * <li>w: workbench</li>
 * <li>1, 2: next/previous recipe</li>
 * <li>3, 4: back/forward recipe</li>
 * <li>+: move ingredients</li>
 * </ul>
 */
public class RecipeInventory implements InventoryHolder, IOwnedByMenu {

    private final Inventory inventory;
    private final IMenu ownerMenu;
    private final JavaPlugin plugin;
    private IRecipeView<Recipe> recipeView;
    private final GuiItemService guiItemService;
    private boolean isReady = false;

    public RecipeInventory(IMenu ownerMenu, JavaPlugin plugin, GuiItemService guiItemService) {
        this.ownerMenu = ownerMenu;
        this.plugin = plugin;
        this.guiItemService = guiItemService;
        this.inventory = this.plugin.getServer().createInventory(this, 54, Component.text("Recipe"));
        initInventory();
    }

    /**
     * return the inventory of the RecipeMenu.
     */
    @Override
    public Inventory getInventory() {
        if (!isReady()) {
            throw new IllegalStateException("RecipeMenu is not initialized, set a recipe first");
        }
        updateCycle();
        return inventory;
    }

    @Override
    public IMenu getOwnedMenu() {
        return ownerMenu;
    }

    public void setRecipe(@NotNull Recipe recipe) {
        recipeView = RecipeViewFactory.createRecipeView(recipe);
        recipeView.setRecipe(recipe);
        updateRecipeViewPart();
        isReady = true;
    }

    public boolean isReady() {
        return isReady;
    }

    private void initInventory() {
        VeiStyle style = VeiStyle.LIGHT;

        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.QUICK_LINK),                    guiItemService.CreateActionItem(ActionType.QUICK_LINK,                    style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.WORKBENCH_TYPE_SCROLL_LEFT),    guiItemService.CreateActionItem(ActionType.WORKBENCH_TYPE_SCROLL_LEFT,    style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.WORKBENCH_TYPE_SCROLL_RIGHT),   guiItemService.CreateActionItem(ActionType.WORKBENCH_TYPE_SCROLL_RIGHT,   style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.INFO),                          guiItemService.CreateActionItem(ActionType.INFO,                          style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.WORKBENCH_VARIANT_SCROLL_UP),   guiItemService.CreateActionItem(ActionType.WORKBENCH_VARIANT_SCROLL_UP,   style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.WORKBENCH_VARIANT_SCROLL_DOWN), guiItemService.CreateActionItem(ActionType.WORKBENCH_VARIANT_SCROLL_DOWN, style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.BOOKMARK_LIST),                 guiItemService.CreateActionItem(ActionType.BOOKMARK_LIST,                 style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.BOOKMARK_SERVER_LIST),          guiItemService.CreateActionItem(ActionType.BOOKMARK_SERVER_LIST,          style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.EXIT),                          guiItemService.CreateActionItem(ActionType.EXIT,                          style));
        inventory.setItem(menuCoordAsMenuIndex(SingleItemCoord.BOOKMARK_THIS_RECIPE),          guiItemService.CreateActionItem(ActionType.BOOKMARK_THIS_RECIPE,          style));
    }

    /**
     * Update the recipe view part of the inventory.
     * This method is needed to update and retrieve items from the RecipeViewContainer inside the Menu inventory after a recipe change/update.
     */
    private void updateRecipeViewPart() {
        for (var slot : recipeView.getRecipeContainer().getSlots()) {
            int index = menuCoordAsMenuIndex(viewCoordAsMenuCoord(slot.getCoord()));
            inventory.setItem(index, slot.getCurrentItemStack());
        }
    }

    private void updateCycle() {
        recipeView.getRecipeContainer().updateCycle();
        updateRecipeViewPart();
        return;
    }

    static private Vector2i viewCoordAsMenuCoord(Vector2i coord) {
        Validate.inclusiveBetween(0, 6, coord.x, "x must be between 0 and 6");
        Validate.inclusiveBetween(0, 4, coord.y, "y must be between 0 and 4");

        return new Vector2i(coord.x + 1, coord.y + 1);
    }

    static private int menuCoordAsMenuIndex(Vector2i coord) {
        return coord.x + coord.y * 9;
    }

    static private int menuCoordAsMenuIndex(SingleItemCoord coord) {
        return menuCoordAsMenuIndex(coord.getCoord());
    }

    /**
     * Define the coordinates of single items in the inventory.
     */
    public enum SingleItemCoord {
        QUICK_LINK(0, 0),
        WORKBENCH_TYPE_SCROLL_LEFT(1, 0),
        WORKBENCH_TYPE_SCROLL_RIGHT(7, 0),
        INFO(8, 0),
        WORKBENCH_VARIANT_SCROLL_UP(0, 1),
        WORKBENCH_VARIANT_SCROLL_DOWN(0, 5),
        BOOKMARK_THIS_RECIPE(8, 2),
        BOOKMARK_LIST(8, 3),
        BOOKMARK_SERVER_LIST(8, 4),
        EXIT(8, 5);

        private final Vector2i coord;

        SingleItemCoord(int x, int y) {
            Preconditions.checkArgument(x >= 0 && x < 9, "x must be between 0 and 8, current value: %d", x);
            Preconditions.checkArgument(y >= 0 && y < 6, "y must be between 0 and 5, current value: %d", y);

            this.coord = new Vector2i(x, y);
        }

        public Vector2i getCoord() {
            return coord;
        }
    }

    /**
     * Define the coordinates of ranges of items in the inventory.
     */
    public enum MultiItemCoord {
        WORKBENCH_ARRAY(new Vector2i(2, 0), new Vector2i(6, 0)),
        WORKBENCH_VARIANT_ARRAY(new Vector2i(0, 2), new Vector2i(0, 4)),
        RECIPE_VIEW(new Vector2i(1, 1), new Vector2i(7, 5));

        private final Set<Vector2i> coords;

        MultiItemCoord(Vector2i coord1, Vector2i coord2) {
            Preconditions.checkArgument(coord1.x <= coord2.x, "coord1 must be on the left side of coord2, current values: %s, %s", coord1, coord2);
            Preconditions.checkArgument(coord1.y <= coord2.y, "coord1 must be on the top side of coord2, current values: %s, %s", coord1, coord2);

            LinkedHashSet<Vector2i> set = new LinkedHashSet<>();
            for (int x = coord1.x; x <= coord2.x; x++) {
                for (int y = coord1.y; y <= coord2.y; y++) {
                    set.add(new Vector2i(x, y));
                }
            }

            coords = Collections.unmodifiableSet(set);
        }

        public Set<Vector2i> getCoords() {
            return coords;
        }
    }
}
