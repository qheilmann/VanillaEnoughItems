package me.qheilmann.vei.Core.Menu;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Command.CraftCommand;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.Recipe.ItemRecipeMap;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Slot.Collection.SlotRange;
import me.qheilmann.vei.Core.Slot.Implementation.MaxChestSlot;
import me.qheilmann.vei.Core.Style.ButtonType.VeiButtonType;
import me.qheilmann.vei.Core.Style.Styles.Style;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * <h1>RecipeMenu</h1>
 * This class is used to display the recipe menu (9x6) in a GUI.
 * <p>
 * GUI representation (with ShapedRecipePanel):
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
 * RecipePanel example (ShapedRecipePanel):
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

    //#region Static Constants
    private static final MaxChestSlot QUICK_LINK_SLOT                  = new MaxChestSlot(0, 0);
    private static final MaxChestSlot PROCESS_SCROLL_LEFT_SLOT         = new MaxChestSlot(1, 0);
    private static final MaxChestSlot PROCESS_SCROLL_RIGHT_SLOT        = new MaxChestSlot(7, 0);
    private static final MaxChestSlot INFO_SLOT                        = new MaxChestSlot(8, 0);
    private static final MaxChestSlot WORKBENCH_SCROLL_UP_SLOT         = new MaxChestSlot(0, 1);
    private static final MaxChestSlot WORKBENCH_SCROLL_DOWN_SLOT       = new MaxChestSlot(0, 5);
    private static final MaxChestSlot BOOKMARK_THIS_RECIPE_TOGGLE_SLOT = new MaxChestSlot(8, 2);
    private static final MaxChestSlot BOOKMARK_LIST_SLOT               = new MaxChestSlot(8, 3);
    private static final MaxChestSlot BOOKMARK_SERVER_LIST_SLOT        = new MaxChestSlot(8, 4);
    private static final MaxChestSlot EXIT_SLOT                        = new MaxChestSlot(8, 5);

    private static final SlotRange<MaxChestSlot> PROCESS_SLOT_RANGE      = new SlotRange<>(new MaxChestSlot(2, 0), new MaxChestSlot(6, 0));
    private static final SlotRange<MaxChestSlot> WORKBENCH_SLOT_RANGE    = new SlotRange<>(new MaxChestSlot(0, 2), new MaxChestSlot(0, 4));
    private static final SlotRange<MaxChestSlot> RECIPE_PANEL_SLOT_RANGE = new SlotRange<>(new MaxChestSlot(1, 1), new MaxChestSlot(7, 5));
    //#endregion Static Constants

    //#region Instance variables
    private final Style style;
    private ProcessPanel<?> recipePanel;

    // Keeps track of the currently displayed recipe
    private ItemRecipeMap itemRecipeMap;
    private Process<?> currentProcess;
    private int currentVariant;

    private GuiItem<RecipeMenu> quickLinkItem;
    private GuiItem<RecipeMenu> processScrollLeftItem;
    private GuiItem<RecipeMenu> processScrollRightItem;
    private GuiItem<RecipeMenu> infoItem;
    private GuiItem<RecipeMenu> workbenchScrollUpItem;
    private GuiItem<RecipeMenu> workbenchScrollDownItem;
    private GuiItem<RecipeMenu> bookmarkThisRecipeItemToggle;
    private GuiItem<RecipeMenu> bookmarkListItem;
    private GuiItem<RecipeMenu> bookmarkServerListItem;
    private GuiItem<RecipeMenu> exitItem;
    private GuiItem<RecipeMenu> nextRecipeItem;
    private GuiItem<RecipeMenu> previousRecipeItem;
    private GuiItem<RecipeMenu> forwardRecipeItem;
    private GuiItem<RecipeMenu> backwardRecipeItem;
    private GuiItem<RecipeMenu> moveIngredientsItem;

    private boolean isProcessScrollLeftVisible = false; // TODO: Implement scroll page workbench type
    private boolean isProcessScrollRightVisible = false;
    private boolean isWorkbenchScrollUpVisible = false; // TODO: Implement scroll page workbench variant
    private boolean isWorkbenchScrollDownVisible = false;
    private boolean isNextRecipeVisible = true;
    private boolean isPreviousRecipeVisible = true;
    private boolean isForwardRecipeVisible = false; // TODO: Implement forward/backward recipe
    private boolean isBackwardRecipeVisible = false;
    private boolean isMoveIngredientsVisible = false; // TODO: Implement move ingredients
    //#endregion Instance variables

    public RecipeMenu(@NotNull Style style, @NotNull ItemRecipeMap itemRecipeMap) {
        this(style, itemRecipeMap, itemRecipeMap.getAllProcess().iterator().next());
    }

    public RecipeMenu(@NotNull Style style, @NotNull ItemRecipeMap itemRecipeMap, Process<?> process) {
        this(style, itemRecipeMap, process, 0);
    }

    public <R extends Recipe> RecipeMenu(@NotNull Style style, @NotNull ItemRecipeMap itemRecipeMap, Process<R> process, int variant) {
        super((owner) -> BaseGui.plugin.getServer().createInventory(owner, 6*9, Component.text("Recipe Menu")), InteractionModifier.VALUES);

        // Validate and set fields
        Objects.requireNonNull(style, "style cannot be null");
        Objects.requireNonNull(itemRecipeMap, "itemRecipeMap cannot be null");

        this.style = style;
        this.itemRecipeMap = itemRecipeMap;
        this.currentProcess = process;
        this.currentVariant = variant;

        ProcessRecipeSet<R> processRecipeSet = itemRecipeMap.getProcessRecipeSet(process);
        Objects.requireNonNull(processRecipeSet, "No recipe set found for process: " + process.getProcessName());

        Recipe recipe = processRecipeSet.getVariant(variant);
        Objects.requireNonNull(recipe, "No variant " + variant + " inside the recipe set for process: " + process.getProcessName());

        this.recipePanel = process.generateProcessPanel(processRecipeSet, variant);

        // Menu configuration
        initAllItem();
        render();
    }

    void initAllItem() {
        // Global action
        setDefaultClickAction((event, context) -> event.setCancelled(true)); // Cancel the event for the entire GUI

        // Initialize static items
        this.processScrollLeftItem          = buildProcessScrollLeftButton();
        this.processScrollRightItem         = buildProcessScrollRightButton();
        this.infoItem                       = buildInfoButton();
        this.workbenchScrollUpItem          = buildWorkbenchScrollUpButton();
        this.workbenchScrollDownItem        = buildWorkbenchScrollDownButton();
        this.bookmarkThisRecipeItemToggle   = buildBookmarkRecipeToggleButton();
        this.bookmarkListItem               = buildBookmarkListButton();
        this.bookmarkServerListItem         = buildBookmarkServerListButton();
        this.exitItem                       = buildExitButton();
        this.nextRecipeItem                 = buildNextRecipeButton();
        this.previousRecipeItem             = buildPreviousRecipeButton();
        this.forwardRecipeItem              = buildForwardRecipeButton();
        this.backwardRecipeItem             = buildBackwardRecipeButton();
        this.moveIngredientsItem            = buildMoveIngredientsButton();

        // Render static buttons
        setItem(QUICK_LINK_SLOT, quickLinkItem);
        setItem(INFO_SLOT, infoItem);
        setItem(BOOKMARK_THIS_RECIPE_TOGGLE_SLOT, bookmarkThisRecipeItemToggle);
        setItem(BOOKMARK_LIST_SLOT, bookmarkListItem);
        setItem(BOOKMARK_SERVER_LIST_SLOT, bookmarkServerListItem);
        setItem(EXIT_SLOT, exitItem);

        // Initialize process panel
        initProcessPanel();
    }

    private void initProcessPanel() {
        // Attach action to the recipe panel
        recipePanel.attachPannelButton(ProcessPanel.AttachedButtonType.NEXT_RECIPE, nextRecipeItem);
        recipePanel.attachPannelButton(ProcessPanel.AttachedButtonType.PREVIOUS_RECIPE, previousRecipeItem);
        recipePanel.attachPannelButton(ProcessPanel.AttachedButtonType.FORWARD_RECIPE, forwardRecipeItem);
        recipePanel.attachPannelButton(ProcessPanel.AttachedButtonType.BACKWARD_RECIPE, backwardRecipeItem);
        recipePanel.attachPannelButton(ProcessPanel.AttachedButtonType.MOVE_INGREDIENTS, moveIngredientsItem);
    }

    public void render() {

        // Update visibility booleans
        updateNextPreviousRecipeVisibility();

        // Apply menu button visibility
        setItemOnVisibility(PROCESS_SCROLL_LEFT_SLOT, processScrollLeftItem, isProcessScrollLeftVisible);
        setItemOnVisibility(PROCESS_SCROLL_RIGHT_SLOT, processScrollRightItem, isProcessScrollRightVisible);
        setItemOnVisibility(WORKBENCH_SCROLL_UP_SLOT, workbenchScrollUpItem, isWorkbenchScrollUpVisible);
        setItemOnVisibility(WORKBENCH_SCROLL_DOWN_SLOT, workbenchScrollDownItem, isWorkbenchScrollDownVisible);
        // Always visible buttons don't need to be re rendered (render once in the constructor)

        renderRecipePanel();
        renderWorkbenchRange();
        renderQuickLink();

        // Padding empty slots (except valide air slot in the recipe panel)
        padEmptySlots();
        fixOverPadding();
    }

    private void renderRecipePanel(){

        // First clear completely the recipe panel area
        for (MaxChestSlot slot : RECIPE_PANEL_SLOT_RANGE) {
            setItem(slot, null);
        }

        // Calculate the new recipe panel, then place it in the GUI
        recipePanel.render(getAllVisibleAttachedButtonsSet()); 
        recipePanel.getContentPanel(ProcessPanel.SlotType.ALL).forEach((slot, item) -> setItem(slot.asMaxChestSlot(), item));
    }

    private void renderWorkbenchRange() {

        MaxChestSlot[] processSlots = PROCESS_SLOT_RANGE.toArray();
        Process<?>[] processes = itemRecipeMap.getAllProcess().toArray(new Process<?>[0]);

        int totalIteration = Math.min(processSlots.length, processes.length);
        if (processes.length > totalIteration) {
            VanillaEnoughItems.LOGGER.warn("Not enough process slots to render all processes"); // TODO: Implement scroll page workbench type
            return;
        }

        for (int i = 0; i < totalIteration ; i++) {
            Process<?> process = processes[i];
            MaxChestSlot slot = processSlots[i];
            setItem(slot, buildChangeProcessButton(process));
        }
    }

    private void renderQuickLink() {
        setItem(QUICK_LINK_SLOT, buildQuickLinkItem(getQuickLinkString()));
    }

    //#region Button setup

    private GuiItem<RecipeMenu> buildQuickLinkItem(String quickLink) {
        GuiItem<RecipeMenu> button;
        button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.QUICK_LINK));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Quick link").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("Copy %s".formatted(quickLink)).color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::quickLinkAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildProcessScrollLeftButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_TYPE_SCROLL_LEFT));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Scroll left").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See previous workbench type").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::processScrollLeftAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildProcessScrollRightButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_TYPE_SCROLL_RIGHT));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Scroll right").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See next workbench type").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::processScrollRightAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildInfoButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.Generic.INFO));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Info").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See VEI info").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::infoAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildWorkbenchScrollUpButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_VARIANT_SCROLL_UP));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Scroll up").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See previous workbench variant").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::workbenchScrollUpAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildWorkbenchScrollDownButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.WORKBENCH_VARIANT_SCROLL_DOWN));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Scroll down").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See next workbench variant").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::workbenchDownAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildBookmarkRecipeToggleButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Bookmark").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("Add this recipe to your bookmark").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::bookmarkRecipeToggleAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildBookmarkListButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_LIST));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Bookmark list").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See your bookmarked recipes").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::bookmarkListAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildBookmarkServerListButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_SERVER_LIST));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Bookmark server list").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See the server bookmarked recipes").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::bookmarkServerListAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildExitButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.Generic.EXIT));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Exit").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("Exit the recipe menu").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::exitAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildNextRecipeButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.NEXT_RECIPE));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Next recipe").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See the next recipe").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::nextRecipeAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildPreviousRecipeButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.PREVIOUS_RECIPE));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Previous recipe").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("See the previous recipe").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::previousRecipeAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildForwardRecipeButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.FORWARD_RECIPE));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Forward recipe").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("Return to the following recipe in the history").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::forwardRecipeAction);
        return button;
    }
        
    private GuiItem<RecipeMenu> buildBackwardRecipeButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BACKWARD_RECIPE));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Backward recipe").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("Go back to the preceding recipe in the history").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::backwardRecipeAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildMoveIngredientsButton() {
        GuiItem<RecipeMenu> button = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.MOVE_INGREDIENTS));
        button.editMeta(meta -> {
            meta.displayName(Component.text("Move ingredients").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("Automatically move all the ingredients inside the workbench").color(style.getSecondaryColor()),
                Component.text("This work only if a empty accessible workbench is around you").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::moveIngredientsAction);
        return button;
    }

    private GuiItem<RecipeMenu> buildChangeProcessButton(Process<?> process) {
        GuiItem<RecipeMenu> item = new GuiItem<>(process.getProcessIcon());

        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(process.getProcessName(), style.getPrimaryColor()));

        if (process.equals(currentProcess)) {
            meta.setEnchantmentGlintOverride(true);
        }

        item.setItemMeta(meta);

        item.setAction((event, menu) -> {
            changeProcessAction(event, menu, process);
        });
        return item;
    }
    
    //#endregion Button setup

    //#region Button actions

    private void quickLinkAction(InventoryClickEvent event, RecipeMenu menu) {
        HumanEntity humanEntity = event.getWhoClicked();
        String quickLink = getQuickLinkString();
        Component message = Component.text()
            .color(NamedTextColor.GRAY)
            .append(Component.text("Quick link "))
            .append(Component
                .text(quickLink)
                .decorate(TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(Component.text("Click to sugest")))
                .clickEvent(ClickEvent.suggestCommand(quickLink))
            )
            .append(Component.text(" copied"))
            .build();

        this.close(humanEntity);
        setClipBoard(getQuickLinkString());
        humanEntity.sendMessage(message);
    }

    private void processScrollLeftAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Scroll left action");
    }

    private void processScrollRightAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Scroll right action");
    }

    private void infoAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Info action");
    }

    private void workbenchScrollUpAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Scroll up action");
    }

    private void workbenchDownAction(InventoryClickEvent event, RecipeMenu menu) {
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
        this.close(event.getWhoClicked());
    }

    private void nextRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        currentVariant = calcNextVariantIndex();
        recipePanel.setRecipVariantIndex(currentVariant);
        render();
    }

    private void previousRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        currentVariant = calcPreviousVariantIndex();
        recipePanel.setRecipVariantIndex(currentVariant);
        render();
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

    private <R extends Recipe> void changeProcessAction(InventoryClickEvent event, RecipeMenu menu, Process<R> process) {
        currentProcess = process;
        currentVariant = 0;

        ProcessRecipeSet<R> processRecipeSet = itemRecipeMap.getProcessRecipeSet(process);
        recipePanel = process.generateProcessPanel(processRecipeSet, currentVariant);
        initProcessPanel();

        render();
    }

    //#endregion Button actions

    private void setItemOnVisibility(MaxChestSlot slot, GuiItem<RecipeMenu> item, boolean isVisible) {
        setItem(slot, isVisible ? item : null);
    }

    protected void padEmptySlots(){
        GuiItem<RecipeMenu> padding = new GuiItem<>(style.getPaddingItem());
        ItemMeta meta = padding.getItemMeta();
        meta.displayName(Component.empty());
        meta.setMaxStackSize(1);
        meta.setHideTooltip(true);
        padding.setItemMeta(meta);
        fillEmpty(padding);
    }

    /**
     * Due to the implementation of the inner inventory, we cannot distinguish
     * between empty slots and padding slots, so we need to clear ingredients
     * / results / consumables slots that are empty.
    */
    protected void fixOverPadding(){
        EnumSet<ProcessPanel.SlotType> slotType = EnumSet.of(ProcessPanel.SlotType.INGREDIENTS, ProcessPanel.SlotType.RESULTS, ProcessPanel.SlotType.CONSUMABLES);
        recipePanel.getContentPanel(slotType).forEach((slot, item) -> {
            if (item == null || item.isEmpty()) {
                setItem(slot.asMaxChestSlot(), null);
            }
        });
    }

    private int calcNextVariantIndex() {
        int nextVariantIndex = recipePanel.getCurrentVariantIndex() + 1;
        int maxVariantIndex = recipePanel.getVariantCount() - 1;

        if (nextVariantIndex <= maxVariantIndex) {
            return nextVariantIndex;
        } else { // invalide case
            return 0;
        }
    }

    private int calcPreviousVariantIndex() {
        int previousVariantIndex = recipePanel.getCurrentVariantIndex() - 1;

        if (previousVariantIndex >= 0) {
            return previousVariantIndex;
        } else { // invalide case
            return 0;
        }
    }

    private void updateNextPreviousRecipeVisibility() {
        int variantIndex = recipePanel.getCurrentVariantIndex();
        int maxVariantIndex = recipePanel.getVariantCount() - 1;

        if (variantIndex >= maxVariantIndex) {
            isNextRecipeVisible = false;
        } else {
            isNextRecipeVisible = true;
        }

        if (variantIndex <= 0) {
            isPreviousRecipeVisible = false;
        } else {
            isPreviousRecipeVisible = true;
        }
    }

    private EnumSet<ProcessPanel.AttachedButtonType> getAllVisibleAttachedButtonsSet() {
        EnumSet<ProcessPanel.AttachedButtonType> set = EnumSet.noneOf(ProcessPanel.AttachedButtonType.class);
        if (isNextRecipeVisible)      set.add(ProcessPanel.AttachedButtonType.NEXT_RECIPE);
        if (isPreviousRecipeVisible)  set.add(ProcessPanel.AttachedButtonType.PREVIOUS_RECIPE);
        if (isForwardRecipeVisible)   set.add(ProcessPanel.AttachedButtonType.FORWARD_RECIPE);
        if (isBackwardRecipeVisible)  set.add(ProcessPanel.AttachedButtonType.BACKWARD_RECIPE);
        if (isMoveIngredientsVisible) set.add(ProcessPanel.AttachedButtonType.MOVE_INGREDIENTS);
        return set;
    }

    /**
     * Get the quick link string to open the recipe menu with the current item, process and variant.
     * @return the quick link string with something like "/craft minecraft:iron_ingot smelting 2"
     */
    private String getQuickLinkString() {
        return "/" + CraftCommand.NAME + " " 
            + itemRecipeMap.getItem().getType().getKey() + " " 
            +  currentProcess.getProcessName().toLowerCase() + " " 
            + (currentVariant + 1); // (Command is 1 based because of the final user)
    }

    private void setClipBoard(String str) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(str);
        clipboard.setContents(strSel, null);
    }
}