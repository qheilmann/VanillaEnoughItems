package me.qheilmann.vei.Core.Menu;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import dev.triumphteam.gui.components.InteractionModifier;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Command.CraftCommand;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.Recipe.RecipeHistory;
import me.qheilmann.vei.Core.Recipe.Bookmark.Bookmark;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Slot.Collection.SlotRange;
import me.qheilmann.vei.Core.Slot.Implementation.MaxChestSlot;
import me.qheilmann.vei.Core.Style.ButtonType.VeiButtonType;
import me.qheilmann.vei.Core.Style.Styles.Style;
import net.kyori.adventure.key.Keyed;
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

    private static final Map<UUID, RecipeHistory> historyMap = new java.util.HashMap<>();

    //#region Instance variables
    private final Style style;
    private final RecipeIndexService recipeIndex;
    private final MixedProcessRecipeReader mixedProcessRecipeReader;
    private ProcessPanel<?> processPanel;
    private int workbenchOptionOffset = 0;
    private int processOptionOffset;

    // menu items
    private GuiItem<RecipeMenu> processScrollLeftItem;
    private GuiItem<RecipeMenu> processScrollRightItem;
    private GuiItem<RecipeMenu> infoItem;
    private GuiItem<RecipeMenu> workbenchScrollUpItem;
    private GuiItem<RecipeMenu> workbenchScrollDownItem;
    private GuiItem<RecipeMenu> bookmarkThisRecipeItemItem;
    private GuiItem<RecipeMenu> bookmarkListItem;
    private GuiItem<RecipeMenu> bookmarkServerListItem;
    private GuiItem<RecipeMenu> exitItem;
    // process panel items
    private GuiItem<RecipeMenu> nextRecipeItem;
    private GuiItem<RecipeMenu> previousRecipeItem;
    private GuiItem<RecipeMenu> forwardRecipeItem;
    private GuiItem<RecipeMenu> backwardRecipeItem;
    private GuiItem<RecipeMenu> moveIngredientsItem;
    // process panel visibility booleans
    private boolean isNextRecipeVisible = true;
    private boolean isPreviousRecipeVisible = true;
    private boolean isForwardRecipeVisible = true;
    private boolean isBackwardRecipeVisible = true;
    private boolean isMoveIngredientsVisible = false; // TODO: Implement move ingredients
    private boolean isInfoButtonVisible = false; // TODO: Implement info button
    private boolean isBookmarkListVisible = false; // TODO: Implement bookmark list

    /**
     * Represents the last viewer who opened this menu.
     * This if for example used to determine the forward/backward button with his history.
     */
    private @Nullable HumanEntity lastViewer = null;
    //#endregion Instance variables

    public static @Nullable RecipeHistory getHistory(@NotNull HumanEntity viewer) {
        return historyMap.get(viewer.getUniqueId());
    }

    public <R extends Recipe> RecipeMenu(@NotNull Style style, @NotNull RecipeIndexService reicpeIndex, @NotNull MixedProcessRecipeReader mixedProcessRecipeReader) {
        super((owner) -> BaseGui.plugin.getServer().createInventory(owner, 6*9, Component.text("Recipe Menu")), InteractionModifier.VALUES);

        // Validate and set fields
        Objects.requireNonNull(style, "style cannot be null");
        Objects.requireNonNull(reicpeIndex, "recipeIndex cannot be null");
        Objects.requireNonNull(mixedProcessRecipeReader, "recipesReader cannot be null");

        this.style = style;
        this.recipeIndex = reicpeIndex;
        this.mixedProcessRecipeReader = mixedProcessRecipeReader;
        this.processOptionOffset = getInitalProcessOptionOffset(mixedProcessRecipeReader);

        // Menu configuration
        initAllItem();
    }

    @SuppressWarnings("unchecked")
    public Process<Recipe> getCurrentProcess() {
        return (Process<Recipe>)mixedProcessRecipeReader.currentProcess();
    }

    public Recipe getCurrentRecipe() {
        return mixedProcessRecipeReader.currentProcessRecipeReader().currentRecipe();
    }

    private void initAllItem() {
        // Global action
        setDefaultClickAction((event, context) -> event.setCancelled(true)); // Cancel the event for the entire GUI

        // Initialize static items
        this.processScrollLeftItem          = buildProcessScrollLeftButton();
        this.processScrollRightItem         = buildProcessScrollRightButton();
        this.infoItem                       = buildInfoButton();
        this.workbenchScrollUpItem          = buildWorkbenchScrollUpButton();
        this.workbenchScrollDownItem        = buildWorkbenchScrollDownButton();
        this.bookmarkListItem               = buildBookmarkListButton();
        this.bookmarkServerListItem         = buildBookmarkServerListButton();
        this.exitItem                       = buildExitButton();
        this.nextRecipeItem                 = buildNextRecipeButton();
        this.previousRecipeItem             = buildPreviousRecipeButton();
        this.forwardRecipeItem              = buildForwardRecipeButton();
        this.backwardRecipeItem             = buildBackwardRecipeButton();
        this.moveIngredientsItem            = buildMoveIngredientsButton();

        // Render static buttons
        setItem(EXIT_SLOT, exitItem);
        if(isInfoButtonVisible) {
            setItem(INFO_SLOT, infoItem);
        }
        if(isBookmarkListVisible) {
            setItem(BOOKMARK_LIST_SLOT, bookmarkListItem);
            setItem(BOOKMARK_SERVER_LIST_SLOT, bookmarkServerListItem);
        }
    }

    private void initProcessPanel() {
        // Attach action to the recipe panel
        processPanel.attachPannelButton(ProcessPanel.AttachedButtonType.NEXT_RECIPE, nextRecipeItem);
        processPanel.attachPannelButton(ProcessPanel.AttachedButtonType.PREVIOUS_RECIPE, previousRecipeItem);
        processPanel.attachPannelButton(ProcessPanel.AttachedButtonType.FORWARD_RECIPE, forwardRecipeItem);
        processPanel.attachPannelButton(ProcessPanel.AttachedButtonType.BACKWARD_RECIPE, backwardRecipeItem);
        processPanel.attachPannelButton(ProcessPanel.AttachedButtonType.MOVE_INGREDIENTS, moveIngredientsItem);
    }

    private void regenerateProcessPanel() {
        // Regenerate the recipe panel
        @SuppressWarnings("unchecked") // Casting to the super type
        Process<Recipe> process = (Process<Recipe>) mixedProcessRecipeReader.currentProcess();
        @SuppressWarnings("unchecked") // Casting to the super type
        ProcessRecipeReader<Recipe> processRecipeReader = (ProcessRecipeReader<Recipe>) mixedProcessRecipeReader.currentProcessRecipeReader();
        this.processPanel = process.generateProcessPanel(style, recipeIndex, processRecipeReader);
        initProcessPanel();
    }

    //#region Render methods

    public void render() {
        // Always visible buttons don't need to be re rendered (render once in the constructor)

        // Update visibility booleans
        updateNextPreviousRecipeVisibility();
        updateForwardBackwardRecipeVisibility();

        // Render per part
        renderRecipePanel();
        renderProcessRange();
        renderWorkbenchRange();
        renderQuickLink();
        renderBookmarkThisRecipe();

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
        processPanel.render(getAllVisibleAttachedButtonsSet()); 
        processPanel.getContentPanel(ProcessPanel.SlotType.ALL).forEach((slot, item) -> setItem(slot.asMaxChestSlot(), item));
    }

    private void renderProcessRange() {
        // first clear old process options
        for (MaxChestSlot slot : PROCESS_SLOT_RANGE) {
            setItem(slot, null);
        }

        NavigableSet<Process<?>> processOptions = mixedProcessRecipeReader.getAllProcess();
        Iterator<Process<?>> filteredProcessIterator = processOptions.stream().skip(processOptionOffset).iterator();

        // Process options
        for (MaxChestSlot slot : PROCESS_SLOT_RANGE) {
            if (!filteredProcessIterator.hasNext()) {
                break; // No more process options to render
            }
            Process<?> process = filteredProcessIterator.next();
            setItem(slot, buildChangeProcessButton(process));
        }

        // Left / Right option
        if (processOptionOffset > 0) {
            setItem(PROCESS_SCROLL_LEFT_SLOT, processScrollLeftItem);
        } else {
            setItem(PROCESS_SCROLL_LEFT_SLOT, null);
        }
        if (filteredProcessIterator.hasNext()) {
            setItem(PROCESS_SCROLL_RIGHT_SLOT, processScrollRightItem);
        } else {
            setItem(PROCESS_SCROLL_RIGHT_SLOT, null);
        }
    }

    private void renderWorkbenchRange() {
        // first clear old workbench options
        for (MaxChestSlot slot : WORKBENCH_SLOT_RANGE) {
            setItem(slot, null);
        }

        Set<ItemStack> workbenchOptions = mixedProcessRecipeReader.currentProcess().getWorkbenchOptions();
        Iterator<ItemStack> filteredWorkbenchIterator = workbenchOptions.stream().skip(workbenchOptionOffset).iterator();

        // Workbench options
        for (MaxChestSlot slot : WORKBENCH_SLOT_RANGE) {
            if (!filteredWorkbenchIterator.hasNext()) {
                break; // No more workbench options to render
            }
            ItemStack workbench = filteredWorkbenchIterator.next();
            setItem(slot, buildWorkbenchOptionButton(workbench));
        }

        // Up / Down option
        if (workbenchOptionOffset > 0) {
            setItem(WORKBENCH_SCROLL_UP_SLOT, workbenchScrollUpItem);
        } else {
            setItem(WORKBENCH_SCROLL_UP_SLOT, null);
        }
        if (filteredWorkbenchIterator.hasNext()) {
            setItem(WORKBENCH_SCROLL_DOWN_SLOT, workbenchScrollDownItem);
        } else {
            setItem(WORKBENCH_SCROLL_DOWN_SLOT, null);
        }
    }

    private void renderQuickLink() {
        setItem(QUICK_LINK_SLOT, buildQuickLinkItem(getQuickLinkString()));
    }

    private void renderBookmarkThisRecipe() {
        renderBookmarkThisRecipe(true);
    }

    private void renderBookmarkThisRecipe(boolean updateBeforeRender) {
        if (updateBeforeRender) {
            updateBookmarkRecipeToggleButton(mixedProcessRecipeReader.currentProcessRecipeReader().currentRecipe());
        }
        setItem(BOOKMARK_THIS_RECIPE_TOGGLE_SLOT, bookmarkThisRecipeItemItem);
    }
    
    //#endregion Render methods
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
                Component.text("Browse previous processes").color(style.getSecondaryColor()),
                Component.text("Hold Shift to scroll faster").color(style.getSecondaryColor())
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
                Component.text("Browse additional processes").color(style.getSecondaryColor()),
                Component.text("Hold Shift to scroll faster").color(style.getSecondaryColor())
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
                Component.text("Browse previous workbench options").color(style.getSecondaryColor()),
                Component.text("Hold Shift to scroll faster").color(style.getSecondaryColor())
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
                Component.text("Browse additional workbench options").color(style.getSecondaryColor()),
                Component.text("Hold Shift to scroll faster").color(style.getSecondaryColor())
            ));
        });
        button.setAction(this::workbenchDownAction);
        return button;
    }

    private void updateBookmarkRecipeToggleButton(@NotNull Recipe recipe) {

        // Check viewer
        if (lastViewer == null) {
            VanillaEnoughItems.LOGGER.warn("Viewer is null, cannot calculate the bookmark state");
            bookmarkThisRecipeItemItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_UNABLE)); // TODO: use warning guiItem
            bookmarkThisRecipeItemItem.editMeta(meta -> {
                meta.displayName(Component.text("Bookmark calculation unavailable").color(style.getPrimaryColor()));
                meta.lore(List.of(
                    Component.text("The last viewer is not set").color(style.getSecondaryColor())
                ));
            });
            return;
        }

        // Edge case: recipe without an ID
        if(!(recipe instanceof Keyed)) {
            bookmarkThisRecipeItemItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_UNABLE));
            bookmarkThisRecipeItemItem.editMeta(meta -> {
                meta.displayName(Component.text("Unable to Bookmark this Recipe").color(style.getPrimaryColor()));
                meta.lore(List.of(
                    Component.text("This recipe does not have a recipe ID.").color(style.getSecondaryColor())
                ));
            });
            bookmarkThisRecipeItemItem.setAction((event, context) -> {}); // No action
            return;
        }

        // Normal recipe (with async check)
        Keyed keyedRecipe = (Keyed) recipe;
        @SuppressWarnings("null")
        CompletableFuture<Boolean> isAlreadyBookmarkedFuture = Bookmark.hasBookmarkAsync(lastViewer.getUniqueId(), keyedRecipe);
        // After async
        isAlreadyBookmarkedFuture.thenAccept(isAlreadyBookmarked -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                // Remove the waiting icon and set the correct one
                if (isAlreadyBookmarked) {
                    bookmarkThisRecipeItemItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_BOOKMARKED)); // Show bookmark icon
                    bookmarkThisRecipeItemItem.editMeta(meta -> {
                        meta.displayName(Component.text("Bookmarked recipe").color(style.getPrimaryColor()));
                        meta.lore(List.of(
                            Component.text("Click to remove this recipe from your bookmark list").color(style.getSecondaryColor())
                        ));
                    });
                    bookmarkThisRecipeItemItem.setAction((event, context) -> bookmarkRecipeAction(event, context, false)); // Unbookmark action
                } else {
                    bookmarkThisRecipeItemItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_UNBOOKMARKED));
                    bookmarkThisRecipeItemItem.editMeta(meta -> {
                        meta.displayName(Component.text("Bookmark this recipe").color(style.getPrimaryColor()));
                        meta.lore(List.of(
                            Component.text("Click to add this recipe to your bookmark list").color(style.getSecondaryColor())
                        ));
                    });
                    bookmarkThisRecipeItemItem.setAction((event, context) -> bookmarkRecipeAction(event, context, true)); // Bookmark action
                }
                renderBookmarkThisRecipe(false); // Re-render the bookmark button
            });
        });

        // During the async check, we load a waiting icon (same itemstack as the unbookmarked one to avoid flashing)
        bookmarkThisRecipeItemItem = new GuiItem<>(style.getButtonMaterial(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_UNBOOKMARKED));
        bookmarkThisRecipeItemItem.editMeta(meta -> {
            meta.displayName(Component.text("Bookmark calculation in progress").color(style.getPrimaryColor()));
            meta.lore(List.of(
                Component.text("Please wait...").color(style.getSecondaryColor())
            ));
        });
        bookmarkThisRecipeItemItem.setAction((event, context) -> {}); // No action
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

        if (process.equals(mixedProcessRecipeReader.currentProcess())) {
            meta.setEnchantmentGlintOverride(true);
        }

        item.setItemMeta(meta);

        item.setAction((event, menu) -> {
            changeProcessAction(event, menu, process);
        });
        return item;
    }

    private GuiItem<RecipeMenu> buildWorkbenchOptionButton(ItemStack workbench) {
        GuiItem<RecipeMenu> item = new GuiItem<>(workbench);
        item.setAction((event, menu) -> {
            workbenchOptionAction(event, workbench);
        });
        return item;
    }
    
    //#endregion Button setup
    //#region Button actions

    public void quickLinkAction(InventoryClickEvent event, RecipeMenu menu) {
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

        setClipBoard(quickLink);
        humanEntity.sendMessage(message);
    }

    private void processScrollLeftAction(InventoryClickEvent event, RecipeMenu menu) {
        boolean isShiftClicked = event.isShiftClick();
        int previousOffset = processOptionOffset;

        if (isShiftClicked) {
            processOptionOffset -= PROCESS_SLOT_RANGE.size();
        } else {
            processOptionOffset--;
        }

        processOptionOffset = Math.max(processOptionOffset, 0);

        if (processOptionOffset != previousOffset) {
            render();
        }
    }

    private void processScrollRightAction(InventoryClickEvent event, RecipeMenu menu) {
        Set<Process<?>> processOptions = mixedProcessRecipeReader.getAllProcess();
        int maxOffset = Math.max(processOptions.size() - PROCESS_SLOT_RANGE.size(), 0);
        boolean isShiftClicked = event.isShiftClick();
        int previousOffset = processOptionOffset;

        if (isShiftClicked) {
            processOptionOffset += PROCESS_SLOT_RANGE.size();
        } else {
            processOptionOffset++;
        }

        processOptionOffset = Math.min(processOptionOffset, maxOffset);

        if (processOptionOffset != previousOffset) {
            render();
        }
    }

    private void infoAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Info action");
    }

    private void workbenchScrollUpAction(InventoryClickEvent event, RecipeMenu menu) {
        boolean isShiftClicked = event.isShiftClick();
        int previousOffset = workbenchOptionOffset;

        if (isShiftClicked) {
            workbenchOptionOffset -= WORKBENCH_SLOT_RANGE.size();
        } else {
            workbenchOptionOffset--;
        }

        workbenchOptionOffset = Math.max(workbenchOptionOffset, 0);

        if (workbenchOptionOffset != previousOffset) {
            render();
        }
    }

    private void workbenchDownAction(InventoryClickEvent event, RecipeMenu menu) {
        Set<ItemStack> workbenchOptions = mixedProcessRecipeReader.currentProcess().getWorkbenchOptions();
        int maxOffset = Math.max(workbenchOptions.size() - WORKBENCH_SLOT_RANGE.size(), 0);
        boolean isShiftClicked = event.isShiftClick();
        int previousOffset = workbenchOptionOffset;

        if (isShiftClicked) {
            workbenchOptionOffset += WORKBENCH_SLOT_RANGE.size();
        } else {
            workbenchOptionOffset++;
        }

        workbenchOptionOffset = Math.min(workbenchOptionOffset, maxOffset);

        if (workbenchOptionOffset != previousOffset) {
            render();
        }
    }

    private void bookmarkRecipeAction(InventoryClickEvent event, RecipeMenu menu, boolean shouldBookmark) {
        UUID playerUuid = event.getWhoClicked().getUniqueId();
        Recipe currentRecipe = mixedProcessRecipeReader.currentProcessRecipeReader().currentRecipe();
        
        // Check recipe (edge case: recipe without an ID)
        if(!(currentRecipe instanceof Keyed keyedRecipe)) {
            event.getWhoClicked().sendMessage("This recipe cannot be bookmarked");
        } 
        // Normal recipe
        else {
            if (shouldBookmark) {
                Bookmark.addBookmarkAsync(playerUuid, keyedRecipe).join(); // TODO async bookmark add
            } else {
                Bookmark.removeBookmarkAsync(playerUuid, keyedRecipe).join();
            }
        }

        // Regenerate the recipe panel
        regenerateProcessPanel();
        render();

        return;
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
        ProcessRecipeReader<?> processRecipeReader = mixedProcessRecipeReader.currentProcessRecipeReader();
        if (!processRecipeReader.hasNext()) {
            return; // Silently ignore
        }
        processRecipeReader.next();

        // Regenerate the recipe panel
        regenerateProcessPanel();
        render();
    }

    private void previousRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        ProcessRecipeReader<?> processRecipeReader = mixedProcessRecipeReader.currentProcessRecipeReader();
        if (!processRecipeReader.hasPrevious()) {
            return; // Silently ignore
        }
        processRecipeReader.previous();

        // Regenerate the recipe panel
        regenerateProcessPanel();
        render();
    }

    private void forwardRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        RecipeHistory history = getHistory(event.getWhoClicked());
        if (history == null) {
            return; // Silently ignore, the button should not be visible
        }
        MixedProcessRecipeReader forwardMixedProcessRecipeReader = history.goForward();
        if (forwardMixedProcessRecipeReader == null) {
            return; // Silently ignore, the button should not be visible
        }

        RecipeMenu newRecipeMenu = new RecipeMenu(style, recipeIndex, forwardMixedProcessRecipeReader);
        newRecipeMenu.open(event.getWhoClicked(), false);
    }

    private void backwardRecipeAction(InventoryClickEvent event, RecipeMenu menu) {
        RecipeHistory history = getHistory(event.getWhoClicked());
        if (history == null) {
            return; // Silently ignore, the button should not be visible
        }
        MixedProcessRecipeReader backwardMixedProcessReader = history.goBackward();
        if (backwardMixedProcessReader == null) {
            return; // Silently ignore, the button should not be visible
        }

        RecipeMenu newRecipeMenu = new RecipeMenu(style, recipeIndex, backwardMixedProcessReader);
        newRecipeMenu.open(event.getWhoClicked(), false);
    }

    private void moveIngredientsAction(InventoryClickEvent event, RecipeMenu menu) {
        event.getWhoClicked().sendMessage("Move ingredients action");
    }

    private <R extends Recipe> void changeProcessAction(InventoryClickEvent event, RecipeMenu menu, Process<R> process) {
        mixedProcessRecipeReader.setProcess(process);
        regenerateProcessPanel();
        render();
    }

    private void workbenchOptionAction(InventoryClickEvent event, ItemStack clickedWorkbench) {
        
        MixedProcessRecipeReader newMixedProcessRecipeReader = recipeIndex.getByResult(clickedWorkbench);
        if (newMixedProcessRecipeReader == null) {
            return;
        }

        RecipeMenu menu = new RecipeMenu(style, recipeIndex, newMixedProcessRecipeReader);
        menu.open(event.getWhoClicked());
    }

    //#endregion Button actions

    //#region Others

    @Override
    @Nullable
    public InventoryView open(@NotNull HumanEntity humanEntity) {
        return open(humanEntity, true);
    }

    protected InventoryView open(@NotNull HumanEntity humanEntity, boolean addToHistory) {
        lastViewer = humanEntity;
            
        // Add the recipe path to the history
        if (addToHistory) {
            UUID uuid = humanEntity.getUniqueId();
            RecipeHistory recipeHistory = historyMap.computeIfAbsent(uuid, k -> new RecipeHistory());
            recipeHistory.push(mixedProcessRecipeReader);
        }

        // Generate the recipe panel and render
        regenerateProcessPanel();
        render();

        return super.open(humanEntity);
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
        processPanel.getContentPanel(slotType).forEach((slot, item) -> {
            if (item == null || item.isEmpty()) {
                setItem(slot.asMaxChestSlot(), null);
            }
        });
    }

    private void updateNextPreviousRecipeVisibility() {
        isNextRecipeVisible = mixedProcessRecipeReader.currentProcessRecipeReader().hasNext();
        isPreviousRecipeVisible = mixedProcessRecipeReader.currentProcessRecipeReader().hasPrevious();
    }

    private void updateForwardBackwardRecipeVisibility() {
        HumanEntity viewer = lastViewer;
        if (viewer == null) {
            VanillaEnoughItems.LOGGER.warn("Viewer is null, cannot update forward/backward recipe visibility");
            isBackwardRecipeVisible = false;
            isForwardRecipeVisible = false;
            return;
        }

        RecipeHistory recipeHistory = getHistory(viewer);
        if (recipeHistory == null || recipeHistory.getCurrent() == null) {
            VanillaEnoughItems.LOGGER.warn("No history for this UUID, cannot update forward/backward recipe visibility");
            isBackwardRecipeVisible = false;
            isForwardRecipeVisible = false;
            return;
        } 

        isBackwardRecipeVisible = recipeHistory.hasBackward();
        isForwardRecipeVisible = recipeHistory.hasForward();
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

    private int getInitalProcessOptionOffset(MixedProcessRecipeReader mixedProcessRecipeReader) {
        Process<?> currentProcess = mixedProcessRecipeReader.currentProcess();
        NavigableSet<Process<?>> processOptions = mixedProcessRecipeReader.getAllProcess();
        
        int offset = 0;
        for (Process<?> process : processOptions) {
            if (process.equals(currentProcess)) {
                break; // Found the current process, stop iterating
            }
            offset++;
        }

        offset = offset - PROCESS_SLOT_RANGE.size() / 2; // Center the current process in the visible range

        // edge case: current process is next to the start or end of the list
        int maxOffset = Math.max(processOptions.size() - PROCESS_SLOT_RANGE.size(), 0);
        if (offset < 0) {
            offset = 0; // Clamp to the min offset
        } else if (offset > maxOffset) {
            offset = maxOffset; // Clamp to the max offset
        }

        return offset;
    }

    /**
     * Get the quick link string to open the recipe menu with the current item, process and variant.
     * @return the quick link string with something like "/craft minecraft:iron_ingot smelting 2"
     */
    private String getQuickLinkString() {
        Recipe currentRecipe = mixedProcessRecipeReader.currentProcessRecipeReader().currentRecipe();

        if (currentRecipe instanceof Keyed keyedRecipe) {
            String recipeId = keyedRecipe.key().toString();
            return "/" + CraftCommand.NAME + " " + "--id " + recipeId;
        } else {
            return "No quick link available for this recipe type";
        }
    }

    private void setClipBoard(String str) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(str);
        clipboard.setContents(strSel, null);
    }

    //#endregion Others
}