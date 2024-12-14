package me.qheilmann.vei.Menu;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import me.qheilmann.vei.Menu.RecipeView.IRecipeView;
import me.qheilmann.vei.Menu.RecipeView.RecipeViewFactory;
import me.qheilmann.vei.Service.CustomHeadFactory;

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
public class RecipeMenu implements InventoryHolder {

    public static final Vector2i QUICK_LINK_COORDS = new Vector2i(0, 0);
    public static final Vector2i WORKBENCH_TYPE_SCROLL_LEFT_COORD = new Vector2i(1, 0);
    public static final Vector2i WORKBENCH_TYPE_SCROLL_RIGHT_COORD = new Vector2i(7, 0);
    public static final Pair<Vector2i, Vector2i> WORKBENCH_ARRAY_COORDS = Pair.of(new Vector2i(2, 0),
            new Vector2i(6, 0));
    public static final Vector2i INFO_COORDS = new Vector2i(8, 0);
    public static final Vector2i WORKBENCH_VARIANT_SCROLL_UP = new Vector2i(0, 1);
    public static final Vector2i WORKBENCH_VARIANT_SCROLL_DOWN = new Vector2i(0, 5);
    public static final Pair<Vector2i, Vector2i> WORKBENCH_VARIANT_ARRAY_COORDS = Pair.of(new Vector2i(0, 2),
            new Vector2i(0, 4));
    public static final Vector2i BOOKMARK_THIS_RECIPE_COORDS = new Vector2i(8, 2);
    public static final Vector2i BOOKMARK_LIST_COORDS = new Vector2i(8, 3);
    public static final Vector2i BOOKMARK_SERVER_LIST_COORDS = new Vector2i(8, 4);
    public static final Pair<Vector2i, Vector2i> RECIPE_VIEW_COORDS = Pair.of(new Vector2i(1, 1), new Vector2i(7, 5));
    public static final Vector2i EXIT_COORDS = new Vector2i(8, 5);

    // Octothorpe / slash
    // quartz
    // https://minecraft-heads.com/custom-heads/tag/mathematical-symbol?page=4

    private Inventory inventory;
    private IRecipeView<Recipe> recipeView;
    private JavaPlugin plugin;

    public RecipeMenu(JavaPlugin plugin, Recipe recipe) {
        this.plugin = plugin;
        this.inventory = this.plugin.getServer().createInventory(this, 54, Component.text("Recipe"));
        initInventory();
        setRecipe(recipe);
    }

    @Override
    public Inventory getInventory() {
        updateCycle();
        return inventory;
    }

    public void setRecipe(@NotNull Recipe recipe) {
        recipeView = RecipeViewFactory.createRecipeView(recipe);
        recipeView.setRecipe(recipe);
        updateRecipeViewPart();
    }

    private void initInventory() {
        inventory.setItem(menuCoordAsMenuIndex(QUICK_LINK_COORDS)                   , CustomHeadFactory.QUICK_LINK);
        inventory.setItem(menuCoordAsMenuIndex(WORKBENCH_TYPE_SCROLL_LEFT_COORD)    , CustomHeadFactory.WORKBENCH_TYPE_SCROLL_LEFT);
        inventory.setItem(menuCoordAsMenuIndex(WORKBENCH_TYPE_SCROLL_RIGHT_COORD)   , CustomHeadFactory.WORKBENCH_TYPE_SCROLL_RIGHT);
        inventory.setItem(menuCoordAsMenuIndex(INFO_COORDS)                         , CustomHeadFactory.INFO);
        inventory.setItem(menuCoordAsMenuIndex(WORKBENCH_VARIANT_SCROLL_UP)         , CustomHeadFactory.WORKBENCH_VARIANT_SCROLL_UP);
        inventory.setItem(menuCoordAsMenuIndex(WORKBENCH_VARIANT_SCROLL_DOWN)       , CustomHeadFactory.WORKBENCH_VARIANT_SCROLL_DOWN);
        inventory.setItem(menuCoordAsMenuIndex(BOOKMARK_LIST_COORDS)                , CustomHeadFactory.BOOKMARK_LIST);
        inventory.setItem(menuCoordAsMenuIndex(BOOKMARK_SERVER_LIST_COORDS)         , CustomHeadFactory.BOOKMARK_SERVER_LIST);
        inventory.setItem(menuCoordAsMenuIndex(EXIT_COORDS)                         , CustomHeadFactory.EXIT);

        inventory.setItem(menuCoordAsMenuIndex(BOOKMARK_THIS_RECIPE_COORDS)         , new ItemStack(Material.WHITE_CANDLE));

        return;
    }

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
}
