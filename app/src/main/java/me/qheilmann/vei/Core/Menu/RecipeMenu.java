package me.qheilmann.vei.Core.Menu;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.RecipePanel.RecipePanel;
import me.qheilmann.vei.Core.RecipePanel.Panels.FurnaceRecipePanel;
import me.qheilmann.vei.Core.RecipePanel.Panels.ShapedRecipePanel;
import me.qheilmann.vei.Core.Slot.Collection.SlotRange;
import me.qheilmann.vei.Core.Slot.Implementation.MaxChestSlot;
import me.qheilmann.vei.Core.Style.ButtonType.VeiButtonType;
import me.qheilmann.vei.Core.Style.Styles.Style;
import net.kyori.adventure.text.Component;

/**
 * <h1>RecipeMenu</h1>
 * This class is used to display the recipe menu (9x6) in a GUI.
 * <p>
 * GUI representation (with ShapedRecipeView):
 * <pre>
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
public class RecipeMenu extends BaseGui<RecipeMenu, MaxChestSlot> {
    private static final MaxChestSlot QUICK_LINK_SLOT                    = new MaxChestSlot(0, 0);
    private static final MaxChestSlot WORKBENCH_TYPE_SCROLL_LEFT_SLOT    = new MaxChestSlot(1, 0);
    private static final MaxChestSlot WORKBENCH_TYPE_SCROLL_RIGHT_SLOT   = new MaxChestSlot(7, 0);
    private static final MaxChestSlot INFO_SLOT                          = new MaxChestSlot(8, 0);
    private static final MaxChestSlot WORKBENCH_VARIANT_SCROLL_UP_SLOT   = new MaxChestSlot(0, 1);
    private static final MaxChestSlot WORKBENCH_VARIANT_SCROLL_DOWN_SLOT = new MaxChestSlot(0, 5);
    private static final MaxChestSlot BOOKMARK_THIS_RECIPE_TOGGLE_SLOT   = new MaxChestSlot(8, 2);
    private static final MaxChestSlot BOOKMARK_LIST_SLOT                 = new MaxChestSlot(8, 3);
    private static final MaxChestSlot BOOKMARK_SERVER_LIST_SLOT          = new MaxChestSlot(8, 4);
    private static final MaxChestSlot EXIT_SLOT                          = new MaxChestSlot(8, 5);

    private static final SlotRange<MaxChestSlot> WORKBENCH_SLOT_RANGE         = new SlotRange<>(new MaxChestSlot(2, 0), new MaxChestSlot(6, 0));
    private static final SlotRange<MaxChestSlot> WORKBENCH_VARIANT_SLOT_RANGE = new SlotRange<>(new MaxChestSlot(0, 2), new MaxChestSlot(0, 4));
    private static final SlotRange<MaxChestSlot> RECIPE_VIEW_SLOT_RANGE       = new SlotRange<>(new MaxChestSlot(1, 1), new MaxChestSlot(7, 5));

    private GuiItem<RecipeMenu> quickLinkItem;
    private GuiItem<RecipeMenu> workbenchTypeScrollLeftItem;
    private GuiItem<RecipeMenu> workbenchTypeScrollRightItem;
    private GuiItem<RecipeMenu> infoItem;
    private GuiItem<RecipeMenu> workbenchVariantScrollUpItem;
    private GuiItem<RecipeMenu> workbenchVariantScrollDownItem;
    private GuiItem<RecipeMenu> bookmarkThisRecipeItemToggle;
    private GuiItem<RecipeMenu> bookmarkListItem;
    private GuiItem<RecipeMenu> bookmarkServerListItem;
    private GuiItem<RecipeMenu> exitItem;
    private GuiItem<RecipeMenu> nextRecipeItem;
    private GuiItem<RecipeMenu> previousRecipeItem;
    private GuiItem<RecipeMenu> forwardRecipeItem;
    private GuiItem<RecipeMenu> backwardRecipeItem;
    private GuiItem<RecipeMenu> moveIngredientsItem;

    private static final String BOOKMARK_MESSAGE       = "Bookmark";
    // private static final String UNBOOKMARK_MESSAGE     = "Unbookmark";
    private static final String BOOKMARK_LORE_MESSAGE   = "Add this recipe to your bookmark";
    // private static final String UNBOOKMARK_LORE_MESSAGE = "Remove this recipe from your bookmark";

    private final Style style;
    private RecipePanel<? extends Recipe> recipeView;

    public RecipeMenu(Style style, Recipe recipe) {
        super((owner) -> BaseGui.plugin.getServer().createInventory(owner, 6*9, Component.text("Recipe Menu")), InteractionModifier.VALUES);
        this.style = style;

        // TODO place here a factory to create the right RecipeView
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            recipeView = new ShapedRecipePanel(shapedRecipe);
        } else if (recipe instanceof FurnaceRecipe FurnaceRecipe) {
            recipeView = new FurnaceRecipePanel(FurnaceRecipe);
        } else {
            throw new IllegalArgumentException("Unsupported recipe type: " + recipe.getClass().getSimpleName());
        }

        setDefaultClickAction((event, context) -> event.setCancelled(true)); // Cancel the event for the entire GUI
        
        // Prepare buttons
        setupQuickLinkButton();
        setupWorkbenchTypeScrollLeftButton();
        setupWorkbenchTypeScrollRightButton();
        setupInfoButton();
        setupWorkbenchVariantScrollUpButton();
        setupWorkbenchVariantScrollDownButton();
        setupBookmarkRecipeToggleButton();
        setupBookmarkListButton();
        setupBookmarkServerListButton();
        setupExitButton();
        setupNextRecipeButton();
        setupPreviousRecipeButton();
        setupForwardRecipeButton();
        setupBackwardRecipeButton();
        setupMoveIngredientsButton();

        // TODO populate the outer menu part (not inside the setupMethodes)
        
        // Padding
        padEmptySlots();

        // Populate the recipe view
        populateRecipeView();
    }

    //#region Button setup

    private void setupQuickLinkButton() {
        quickLinkItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.QUICK_LINK));
        quickLinkItem.editMeta(meta -> meta.displayName(
            Component.text("Quick link").color(style.getPrimaryColor())
        ));
        quickLinkItem.editMeta(meta -> meta.lore(List.of(
            Component.text("click to get the command equivalent for go to this recipe").color(style.getSecondaryColor()),
            Component.text("/recipe <myRecipe> <category>").color(style.getSecondaryColor())
        )));
        quickLinkItem.setAction(this::quickLinkAction);
        setItem(QUICK_LINK_SLOT, quickLinkItem);
    }

    private void setupWorkbenchTypeScrollLeftButton() {
        workbenchTypeScrollLeftItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_TYPE_SCROLL_LEFT));
        workbenchTypeScrollLeftItem.editMeta(meta -> meta.displayName(
            Component.text("Scroll left").color(style.getPrimaryColor())
        ));
        workbenchTypeScrollLeftItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See previous workbench type").color(style.getSecondaryColor())
        )));
        workbenchTypeScrollLeftItem.setAction(this::workbenchTypeScrollLeftAction);
        setItem(WORKBENCH_TYPE_SCROLL_LEFT_SLOT, workbenchTypeScrollLeftItem);
    }

    private void setupWorkbenchTypeScrollRightButton() {
        workbenchTypeScrollRightItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_TYPE_SCROLL_RIGHT));
        workbenchTypeScrollRightItem.editMeta(meta -> meta.displayName(
            Component.text("Scroll right").color(style.getPrimaryColor())
        ));
        workbenchTypeScrollRightItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See next workbench type").color(style.getSecondaryColor())
        )));
        workbenchTypeScrollRightItem.setAction(this::workbenchTypeScrollRightAction);
        setItem(WORKBENCH_TYPE_SCROLL_RIGHT_SLOT, workbenchTypeScrollRightItem);
    }

    private void setupInfoButton() {
        infoItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.Generic.INFO));
        infoItem.editMeta(meta -> meta.displayName(
            Component.text("Info").color(style.getPrimaryColor())
        ));
        infoItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See VEI info").color(style.getSecondaryColor())
        )));
        infoItem.setAction(this::infoAction);
        setItem(INFO_SLOT, infoItem);
    }

    private void setupWorkbenchVariantScrollUpButton() {
        workbenchVariantScrollUpItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_VARIANT_SCROLL_UP));
        workbenchVariantScrollUpItem.editMeta(meta -> meta.displayName(
            Component.text("Scroll up").color(style.getPrimaryColor())
        ));
        workbenchVariantScrollUpItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See previous workbench variant").color(style.getSecondaryColor())
        )));
        workbenchVariantScrollUpItem.setAction(this::workbenchVariantScrollUpAction);
        setItem(WORKBENCH_VARIANT_SCROLL_UP_SLOT, workbenchVariantScrollUpItem);
    }

    private void setupWorkbenchVariantScrollDownButton() {
        workbenchVariantScrollDownItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_VARIANT_SCROLL_DOWN));
        workbenchVariantScrollDownItem.editMeta(meta -> meta.displayName(
            Component.text("Scroll down").color(style.getPrimaryColor())
        ));
        workbenchVariantScrollDownItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See next workbench variant").color(style.getSecondaryColor())
        )));
        workbenchVariantScrollDownItem.setAction(this::workbenchVariantScrollDownAction);
        setItem(WORKBENCH_VARIANT_SCROLL_DOWN_SLOT, workbenchVariantScrollDownItem);
    }

    private void setupBookmarkRecipeToggleButton() {
        bookmarkThisRecipeItemToggle = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE));
        bookmarkThisRecipeItemToggle.editMeta(meta -> meta.displayName(
            Component.text(BOOKMARK_MESSAGE).color(style.getPrimaryColor())
        ));
        bookmarkThisRecipeItemToggle.editMeta(meta -> meta.lore(
            List.of(Component.text(BOOKMARK_LORE_MESSAGE).color(style.getSecondaryColor())
        )));
        bookmarkThisRecipeItemToggle.setAction(this::bookmarkRecipeToggleAction);
        setItem(BOOKMARK_THIS_RECIPE_TOGGLE_SLOT, bookmarkThisRecipeItemToggle);
    }

    private void setupBookmarkListButton() {
        bookmarkListItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_LIST));
        bookmarkListItem.editMeta(meta -> meta.displayName(
            Component.text("Bookmark list").color(style.getPrimaryColor())
        ));
        bookmarkListItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See your bookmarked recipes").color(style.getSecondaryColor())
        )));
        bookmarkListItem.setAction(this::bookmarkListAction);
        setItem(BOOKMARK_LIST_SLOT, bookmarkListItem);
    }

    private void setupBookmarkServerListButton() {
        bookmarkServerListItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_SERVER_LIST));
        bookmarkServerListItem.editMeta(meta -> meta.displayName(
            Component.text("Bookmark server list").color(style.getPrimaryColor())
        ));
        bookmarkServerListItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See the server bookmarked recipes").color(style.getSecondaryColor())
        )));
        bookmarkServerListItem.setAction(this::bookmarkServerListAction);
        setItem(BOOKMARK_SERVER_LIST_SLOT, bookmarkServerListItem);
    }

    private void setupExitButton() {
        exitItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.Generic.EXIT));
        exitItem.editMeta(meta -> meta.displayName(
            Component.text("Exit").color(style.getPrimaryColor())
        ));
        exitItem.editMeta(meta -> meta.lore(List.of(
            Component.text("Exit the recipe menu").color(style.getSecondaryColor())
        )));
        exitItem.setAction(this::exitAction);
        setItem(EXIT_SLOT, exitItem);
    }

    private void setupNextRecipeButton() {
        nextRecipeItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.NEXT_RECIPE));
        nextRecipeItem.editMeta(meta -> meta.displayName(
            Component.text("Next recipe").color(style.getPrimaryColor())
        ));
        nextRecipeItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See the next recipe").color(style.getSecondaryColor())
        )));
        nextRecipeItem.setAction(this::nextRecipeAction);
        recipeView.attachMenuButton(RecipePanel.ButtonType.NEXT_RECIPE, nextRecipeItem);
    }

    private void setupPreviousRecipeButton() {
        previousRecipeItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.PREVIOUS_RECIPE));
        previousRecipeItem.editMeta(meta -> meta.displayName(
            Component.text("Previous recipe").color(style.getPrimaryColor())
        ));
        previousRecipeItem.editMeta(meta -> meta.lore(List.of(
            Component.text("See the previous recipe").color(style.getSecondaryColor())
        )));
        previousRecipeItem.setAction(this::previousRecipeAction);
        recipeView.attachMenuButton(RecipePanel.ButtonType.PREVIOUS_RECIPE, previousRecipeItem);
    }

    private void setupForwardRecipeButton() {
        forwardRecipeItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.FORWARD_RECIPE));
        forwardRecipeItem.editMeta(meta -> meta.displayName(
            Component.text("Forward recipe").color(style.getPrimaryColor())
        ));
        forwardRecipeItem.editMeta(meta -> meta.lore(List.of(
            Component.text("Return to the following recipe in the history").color(style.getSecondaryColor())
        )));
        forwardRecipeItem.setAction(this::forwardRecipeAction);
        recipeView.attachMenuButton(RecipePanel.ButtonType.FORWARD_RECIPE, forwardRecipeItem);
    }
    
    private void setupBackwardRecipeButton() {
        backwardRecipeItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BACKWARD_RECIPE));
        backwardRecipeItem.editMeta(meta -> meta.displayName(
            Component.text("Backward recipe").color(style.getPrimaryColor())
        ));
        backwardRecipeItem.editMeta(meta -> meta.lore(List.of(
            Component.text("Go back to the preceding recipe in the history").color(style.getSecondaryColor())
        )));
        backwardRecipeItem.setAction(this::backwardRecipeAction);
        recipeView.attachMenuButton(RecipePanel.ButtonType.BACKWARD_RECIPE, backwardRecipeItem);
    }

    private void setupMoveIngredientsButton() {
        moveIngredientsItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.MOVE_INGREDIENTS));
        moveIngredientsItem.editMeta(meta -> meta.displayName(
            Component.text("Move ingredients").color(style.getPrimaryColor())
        ));
        moveIngredientsItem.editMeta(meta -> meta.lore(List.of(
            Component.text("Automatically move all the ingredients inside the workbench").color(style.getSecondaryColor()),
            Component.text("This work only if a empty accessible workbench is around you").color(style.getSecondaryColor())
        )));
        moveIngredientsItem.setAction(this::moveIngredientsAction);
        recipeView.attachMenuButton(RecipePanel.ButtonType.MOVE_INGREDIENTS, moveIngredientsItem);
    }
    
    //#endregion Button setup

    //#region Button actions

    private void quickLinkAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Quick link action");
    }

    private void workbenchTypeScrollLeftAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Scroll left action");
    }

    private void workbenchTypeScrollRightAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Scroll right action");
    }

    private void infoAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Info action");
    }

    private void workbenchVariantScrollUpAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Scroll up action");
    }

    private void workbenchVariantScrollDownAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Scroll down action");
    }

    private void bookmarkRecipeToggleAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Bookmark toggle action");
    }

    private void bookmarkListAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Bookmark list action");
    }

    private void bookmarkServerListAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Bookmark server list action");
    }

    private void exitAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Exit action");
    }

    private void nextRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Next recipe action");
    }

    private void previousRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Previous recipe action");
    }

    private void forwardRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Forward recipe action");
    }

    private void backwardRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Backward recipe action");
    }

    private void moveIngredientsAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Move ingredients action");
    }

    //#endregion Button actions

    protected void padEmptySlots(){
        GuiItem<RecipeMenu> padding = new GuiItem<>(style.getPaddingItem());
        ItemMeta meta = padding.getItemMeta();
        meta.displayName(Component.empty());
        meta.setMaxStackSize(1);
        meta.setHideTooltip(true);
        padding.setItemMeta(meta);
        fillEmpty(padding);
    }

    protected void populateRecipeView(){
        populateRecipeView(RecipePanel.SlotType.ALL);
    }

    protected void populateRecipeView(EnumSet<RecipePanel.SlotType> slotType){
        recipeView.getContentView(slotType).forEach((slot, item) -> setItem(slot.asMaxChestSlot(), item));
    }
}