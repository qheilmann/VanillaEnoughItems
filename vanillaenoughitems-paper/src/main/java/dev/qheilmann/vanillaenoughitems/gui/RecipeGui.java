package dev.qheilmann.vanillaenoughitems.gui;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerGuiData;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.AbstractProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInv;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.Slots;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class RecipeGui extends FastInv implements RecipeGuiActions {

    // GUI size
    private static final int SIZE = FastInv.GENERIC_9X6_SIZE;
    private static final int INGREDIENT_TICK_INTERVAL = 20; // ticks (1 second)
    
    // Slots
    private static final LinkedHashSet<Integer> PROCESSES_SCROLL_RANGE  = Slots.Generic9x6.gridRange(1, 0, 7, 0);
    private static final LinkedHashSet<Integer> WORKBENCHS_SCROLL_RANGE = Slots.Generic9x6.gridRange(0, 1, 0, 5);
    private static final int QUICK_LINK_SLOT            = Slots.Generic9x6.slot(0, 0);
    private static final int PROCESS_SCROLL_LEFT_SLOT   = PROCESSES_SCROLL_RANGE.getFirst();
    private static final int PROCESS_SCROLL_RIGHT_SLOT  = PROCESSES_SCROLL_RANGE.getLast();
    private static final int INFO_SLOT                  = Slots.Generic9x6.slot(8, 0);
    private static final int WORKBENCH_SCROLL_UP_SLOT   = WORKBENCHS_SCROLL_RANGE.getFirst();
    private static final int WORKBENCH_SCROLL_DOWN_SLOT = WORKBENCHS_SCROLL_RANGE.getLast();
    private static final int BOOKMARK_THIS_RECIPE_SLOT  = Slots.Generic9x6.slot(8, 2);
    private static final int BOOKMARK_LIST_SLOT         = Slots.Generic9x6.slot(8, 3);
    private static final int BOOKMARK_SERVER_LIST_SLOT  = Slots.Generic9x6.slot(8, 4);
    
    private static final int MAX_VISIBLE_PROCESSES = PROCESSES_SCROLL_RANGE.size();
    private static final int MAX_SCROLLABLE_PROCESSES = MAX_VISIBLE_PROCESSES - 2; // Account for scroll buttons
    private static final int MAX_VISIBLE_WORKBENCHES = WORKBENCHS_SCROLL_RANGE.size();
    private static final int MAX_SCROLLABLE_WORKBENCHES = MAX_VISIBLE_WORKBENCHES - 2; // Account for scroll buttons

    // Static item
    private static final ItemStack FILLER_ITEM = fillerItem();

    @SuppressWarnings("unused")
    private final Player player;
    private final RecipeGuiContext context;
    private final PlayerGuiData playerData;
    private MultiProcessRecipeReader reader;
    private @Nullable BukkitTask tickTask;

    // Mutable state
    private int processScrollOffset = 0;
    private int workbenchScrollOffset = 0;

    public RecipeGui(Player player, RecipeGuiContext context, MultiProcessRecipeReader initialReader) {
        super(SIZE, Component.text("Recipes"));
        this.player = player;
        this.context = context;
        this.playerData = context.getPlayerData(player.getUniqueId());
        this.reader = initialReader;
        
        // Initial filling
        fillRange(Slots.Generic9x6.all(), FILLER_ITEM);

        // Static buttons
        renderBookmarkListButton(); // maybe make them static ?
        renderBookmarkServerListButton(); // maybe make them static ?
        renderInfoButton(); // maybe make them static ?

        // Dynamic render
        render();
    }

    /**
     * Render the entire GUI based on the current recipe and process.
     * This re-renders all dynamic parts of the GUI.
     * @implNote This method don't erase previous items, it should place {@link #FILLER_ITEM} where a slot is unused.
     */
    public void render() {
        // Recipe dependent buttons
        renderBookmarkButton();
        renderQuickLinkButton();
        renderWorkbenchScrollButtons();
        renderProcessScrollButtons();

        // Process panel content
        renderProcessPanel(generateCurrentPanel());
    }

    /**
     * Changes the current recipe based on the clicked item.
     *
     * @param event The inventory click event.
     * @param resultItem The item that was clicked.
     */
    @Override
    public void changeRecipeAction(InventoryClickEvent event) {
        
        boolean isLeftClick = event.getClick().isLeftClick();
        boolean isRightClick = event.getClick().isRightClick();
        ItemStack clickedItemStack = event.getCurrentItem();
        MultiProcessRecipeReader newMultiRecipeReader = null;

        if (isLeftClick) {
            newMultiRecipeReader = context.getRecipeIndexReader().readerByResult(clickedItemStack);
        } else if (isRightClick) {
            newMultiRecipeReader = context.getRecipeIndexReader().readerByIngredient(clickedItemStack);
        } 
        // Ignore other click types

        // If valide new reader found
        if (newMultiRecipeReader != null) {
            // Only push to history if we're actually navigating to a different recipe view
            playerData.navigationHistory().pushForNavigation(reader, newMultiRecipeReader);
            this.reader = newMultiRecipeReader;
            render();
        }
    }

    public void resultItemAction(InventoryClickEvent event) {

        boolean isLeftClick = event.getClick().isLeftClick();
        boolean isRightClick = event.getClick().isRightClick();
        ItemStack clickedItemStack = event.getCurrentItem();
        
        // Show usage on right click
        if (isRightClick) {
            MultiProcessRecipeReader newMultiRecipeReader = context.getRecipeIndexReader().readerByIngredient(clickedItemStack);
            if (newMultiRecipeReader != null) {
                // Only push to history if we're actually navigating to a different recipe view
                playerData.navigationHistory().pushForNavigation(reader, newMultiRecipeReader);
                this.reader = newMultiRecipeReader;
                render();
            }
        }

        // Quick link on left click
        if (isLeftClick) {
            quickLinkAction().accept(event);
        }

        // Ignore other click types
    }

    @Override
    public Recipe getCurrentRecipe() {
        return reader.getCurrentProcessRecipeReader().getCurrent();
    }

    @Override
    public Process getCurrentProcess() {
        return reader.getCurrentProcess();
    }

    //#region Process Panel

    /**
     * Render the given process panel into the GUI
     * 
     * @param processPanel the panel to render
     */
    private void renderProcessPanel(AbstractProcessPanel processPanel) {
        Map<RecipeGuiSharedButton, ProcessPannelSlot> sharedButtonSlots = processPanel.getRecipeGuiButtonMap();
        
        fillRange(ProcessPannelSlot.all(), FILLER_ITEM);

        // Recipe reader dependent buttons
        renderNextRecipeButton(sharedButtonSlots.get(RecipeGuiSharedButton.NEXT_RECIPE).toSlotIndex());
        renderPreviousRecipeButton(sharedButtonSlots.get(RecipeGuiSharedButton.PREVIOUS_RECIPE).toSlotIndex());
        renderForwardNavigationButton(sharedButtonSlots.get(RecipeGuiSharedButton.HISTORY_FORWARD).toSlotIndex());
        renderBackwardNavigationButton(sharedButtonSlots.get(RecipeGuiSharedButton.HISTORY_BACKWARD).toSlotIndex());
        
        // refact all of this, make sub methodes
        startIngredientTicker(processPanel);

        // Place ticked ingredient slots
        Map<ProcessPannelSlot, CyclicIngredient> tickedSlots = processPanel.getTickedIngredient();
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedSlots.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            CyclicIngredient view = entry.getValue();
            setItem(panelSlot.toSlotIndex(), view.getCurrentItem(), event -> changeRecipeAction(event));
        }

        // Place ticked result slots
        Map<ProcessPannelSlot, CyclicIngredient> tickedResults = processPanel.getTickedResults();
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedResults.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            CyclicIngredient view = entry.getValue();
            setItem(panelSlot.toSlotIndex(), view.getCurrentItem(), event -> resultItemAction(event));
        }
        
        // Place static decorative items
        Map<ProcessPannelSlot, FastInvItem> staticItems = processPanel.getStaticItems();
        for (Map.Entry<ProcessPannelSlot, FastInvItem> entry : staticItems.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            FastInvItem item = entry.getValue();
            setItem(panelSlot.toSlotIndex(), item);
        }
    }

    private AbstractProcessPanel generateCurrentPanel() {
        AbstractProcessPanel newPanel = context.getProcessPanelRegistry().createPanel(
            getCurrentProcess(),
            getCurrentRecipe(),
            this,
            context
        );

        return newPanel;
    }

    private void startIngredientTicker(AbstractProcessPanel processPanel) {
        if (tickTask != null) {
            tickTask.cancel();
        }
        tickTask = Bukkit.getScheduler().runTaskTimer(
            VanillaEnoughItems.getPlugin(VanillaEnoughItems.class),
            () -> tickIngredients(processPanel),
            INGREDIENT_TICK_INTERVAL,
            INGREDIENT_TICK_INTERVAL
        );
    }

    private void tickIngredients(AbstractProcessPanel currentPanel) {
        VanillaEnoughItems.LOGGER.info("Ticking ingredients in RecipeGui time since last tick" + System.currentTimeMillis()); // TEMP

        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : currentPanel.getTickedIngredient().entrySet()) {
            CyclicIngredient cyclic = entry.getValue();
            if (!cyclic.hasMultipleOptions()) {
                continue;
            }

            ProcessPannelSlot panelSlot = entry.getKey();
            cyclic.tickForward();
            ItemStack currentItem = cyclic.getCurrentItem();
            setItem(panelSlot.toSlotIndex(), new FastInvItem(currentItem, event -> changeRecipeAction(event)));
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
    }

    //#endregion Process Panel

    //#region Next/Previous Recipe

    private void renderNextRecipeButton(int slot) {
        if (!hasNextRecipe()) {
            setItem(slot, FILLER_ITEM);
            return;
        }

        // TODO make this style dependent
        ItemStack item = ItemType.OAK_BOAT.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Next Recipe", NamedTextColor.WHITE));
        });

        setItem(slot, item, e -> nextRecipe());
    }

    private void renderPreviousRecipeButton(int slot) {
        if (!hasPreviousRecipe()) {
            setItem(slot, FILLER_ITEM);
            return;
        }

        // TODO make this style dependent
        ItemStack item = ItemType.OAK_BOAT.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Previous Recipe", NamedTextColor.WHITE));
        });

        setItem(slot, item, e -> previousRecipe());
    }

    
    public boolean hasNextRecipe() {
        return !reader.getCurrentProcessRecipeReader().isLast();
    }

    public boolean hasPreviousRecipe() {
        return !reader.getCurrentProcessRecipeReader().isFirst();
    }

    @Override
    public void nextRecipe() {
        if (!hasNextRecipe()) {
            return;
        }
        reader.getCurrentProcessRecipeReader().next();
        render();
    }

    @Override
    public void previousRecipe() {
        if (!hasPreviousRecipe()) {
            return;
        }
        reader.getCurrentProcessRecipeReader().previous();
        render();
    }

    //#endregion Next/Previous Recipe

    //#region Forward/Backward Navigation

    private void renderForwardNavigationButton(int slot) {
        if (!hasForwardNavigation()) {
            setItem(slot, FILLER_ITEM);
            return;
        }

        // TODO make this style dependent
        ItemStack item = new ItemStack(Material.ACACIA_BOAT);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Forward in History", NamedTextColor.WHITE));
        });

        setItem(slot, item, e -> historyForward());
    }

    private void renderBackwardNavigationButton(int slot) {
        if (!hasBackwardNavigation()) {
            setItem(slot, FILLER_ITEM);
            return;
        }

        // TODO make this style dependent
        ItemStack item = new ItemStack(Material.ACACIA_BOAT);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Backward in History", NamedTextColor.WHITE));
        });

        setItem(slot, item, e -> historyBackward());
    }

    private boolean hasForwardNavigation() {
        return playerData.navigationHistory().canGoForward();
    }

    private boolean hasBackwardNavigation() {
        return playerData.navigationHistory().canGoBackward();
    }

    @Override
    public void historyBackward() {
        MultiProcessRecipeReader previousReader = playerData.navigationHistory().goBackward(reader);
        if (previousReader != null) {
            this.reader = previousReader;
            render();
        }
    }

    @Override
    public void historyForward() {
        MultiProcessRecipeReader nextReader = playerData.navigationHistory().goForward(reader);
        if (nextReader != null) {
            this.reader = nextReader;
            render();
        }
    }

    //#endregion Forward/Backward Navigation

    //#region Process Scroll

    private void renderProcessScrollButtons() {        
        NavigableSet<Process> processes = reader.getAllProcesses();

        int numberOfProcesses = processes.size();
        Iterator<Integer> slotIterator = PROCESSES_SCROLL_RANGE.iterator();
        Iterator<Process> processIterator = processes.iterator();

        // There are equal or less processes than visible slots
        if (numberOfProcesses <= MAX_VISIBLE_PROCESSES) {
            if (numberOfProcesses <= MAX_VISIBLE_PROCESSES-1) { // Start at the 2nd slot if there is space
                slotIterator.next();
            }

            slotIterator.forEachRemaining(slot -> {
                if (!processIterator.hasNext()) {
                    setItem(slot, FILLER_ITEM);
                    return;
                }
                Process process = processIterator.next();
                ItemStack symbolItem = process.symbol();
                setItem(slot, symbolItem, e -> changeProcess(process));
            });
        } 

        // There are more processes than visible slots
        else {
            // Scroll Left button
            if (this.processScrollOffset > 0) {
                setItem(PROCESS_SCROLL_LEFT_SLOT, processScrollLeftItem(), processScrollLeftAction());
            } else {
                setItem(PROCESS_SCROLL_LEFT_SLOT, FILLER_ITEM);
            }
            // Scroll Right button
            if (this.processScrollOffset < numberOfProcesses - MAX_SCROLLABLE_PROCESSES) {
                setItem(PROCESS_SCROLL_RIGHT_SLOT, processScrollRightItem(), processScrollRightAction());
            } else {
                setItem(PROCESS_SCROLL_RIGHT_SLOT, FILLER_ITEM);
            }
            // Process items
            slotIterator.next(); // Skip scroll left slot
            for (int i = 0; i < this.processScrollOffset; i++) {
                processIterator.next();
            }
            for (int i = 0; i < MAX_SCROLLABLE_PROCESSES; i++) {
                Process process = processIterator.next();
                int slot = slotIterator.next();
                ItemStack symbolItem = process.symbol();
                setItem(slot, symbolItem, e -> changeProcess(process));
            }
        }
    }

    private void changeProcess(Process process) {
        reader.setCurrentProcess(process);
        render();
    }

    private ItemStack processScrollLeftItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Left", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + this.processScrollOffset + " more to the left", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    private ItemStack processScrollRightItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        int numberOfProcesses = reader.getAllProcesses().size();
        int moreNumber = numberOfProcesses - MAX_SCROLLABLE_PROCESSES - this.processScrollOffset;
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Right", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + moreNumber + " more to the right", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    private Consumer<InventoryClickEvent> processScrollLeftAction() {
        return event -> {
            int newOffset = processScrollOffset - 1;
            this.processScrollOffset = Math.max(0, newOffset);
            renderProcessScrollButtons();
        };
    }

    private Consumer<InventoryClickEvent> processScrollRightAction() {
        return event -> {
            int numberOfProcesses = reader.getAllProcesses().size();
            int newOffset = processScrollOffset + 1;
            this.processScrollOffset = Math.min(numberOfProcesses - MAX_SCROLLABLE_PROCESSES, newOffset);
            renderProcessScrollButtons();
        };
    }

    //#endregion Process Scroll

    //#region Workbench Scroll

    private void renderWorkbenchScrollButtons() {        
        Set<Workbench> workbenches = reader.getCurrentProcess().workbenches();

        int numberOfWorkbenches = workbenches.size();
        Iterator<Integer> slotIterator = WORKBENCHS_SCROLL_RANGE.iterator();
        Iterator<Workbench> workbenchIterator = workbenches.iterator();

        // There are equal or less workbenches than visible slots
        if (numberOfWorkbenches <= MAX_VISIBLE_WORKBENCHES) {
            if (numberOfWorkbenches <= MAX_VISIBLE_WORKBENCHES-1) { // Start at the 2nd slot if there is space
                slotIterator.next();
            }

            slotIterator.forEachRemaining(slot -> {
                if (!workbenchIterator.hasNext()) {
                    setItem(slot, FILLER_ITEM);
                    return;
                }
                Workbench workbench = workbenchIterator.next();
                ItemStack symbolItem = workbench.symbol();
                setItem(slot, symbolItem, event -> changeRecipeAction(event));
            });
        } 

        // There are more workbenches than visible slots
        else {
            // Scroll Up button
            if (workbenchScrollOffset > 0) {
                setItem(WORKBENCH_SCROLL_UP_SLOT, workbenchScrollUpItem(), workbenchScrollUpAction());
            } else {
                setItem(WORKBENCH_SCROLL_UP_SLOT, FILLER_ITEM);
            }
            // Scroll Down button
            if (workbenchScrollOffset < numberOfWorkbenches - MAX_SCROLLABLE_WORKBENCHES) {
                setItem(WORKBENCH_SCROLL_DOWN_SLOT, workbenchScrollDownItem(), workbenchScrollDownAction());
            } else {
                setItem(WORKBENCH_SCROLL_DOWN_SLOT, FILLER_ITEM);
            }
            // Workbench items
            slotIterator.next(); // Skip scroll up slot
            for (int i = 0; i < workbenchScrollOffset; i++) {
                workbenchIterator.next();
            }
            for (int i = 0; i < MAX_SCROLLABLE_WORKBENCHES; i++) {
                Workbench workbench = workbenchIterator.next();
                int slot = slotIterator.next();
                ItemStack symbolItem = workbench.symbol();
                setItem(slot, symbolItem, event -> changeRecipeAction(event));
            }
        }
    }

    private ItemStack workbenchScrollUpItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Up", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + workbenchScrollOffset + " more above", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    private ItemStack workbenchScrollDownItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        int numberOfWorkbenches = reader.getCurrentProcess().workbenches().size();
        int moreNumber = numberOfWorkbenches - MAX_SCROLLABLE_WORKBENCHES - workbenchScrollOffset;
        item.editMeta(meta -> {
            meta.displayName(Component.text("Scroll Down", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("+ " + moreNumber + " more below", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    private Consumer<InventoryClickEvent> workbenchScrollUpAction() {
        return event -> {
            int newOffset = workbenchScrollOffset - 1;
            this.workbenchScrollOffset = Math.max(0, newOffset);
            renderWorkbenchScrollButtons();
        };
    }

    private Consumer<InventoryClickEvent> workbenchScrollDownAction() {
        return event -> {
            int numberOfWorkbenches = reader.getCurrentProcess().workbenches().size();
            int newOffset = workbenchScrollOffset + 1;
            this.workbenchScrollOffset = Math.min(numberOfWorkbenches - MAX_SCROLLABLE_WORKBENCHES, newOffset);
            renderWorkbenchScrollButtons();
        };
    }

    //#endregion Workbench Scroll

    //#region Bookmark toggle

    private void renderBookmarkButton() {
        boolean isBookmarked = isCurrentRecipeBookmarked();
        setItem(BOOKMARK_THIS_RECIPE_SLOT, bookmarkItem(isBookmarked), bookmarkAction());
    }

    public boolean isCurrentRecipeBookmarked() {
        return playerData.bookmarkCollection().isBookmarked(getCurrentRecipe());
    }

    private ItemStack bookmarkItem(boolean isBookmarked) {
        ItemStack item =  isBookmarked ? ItemType.YELLOW_CANDLE.createItemStack() : ItemType.WHITE_CANDLE.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(Component.text(isBookmarked ? "Remove Bookmark" : "Add Bookmark", NamedTextColor.WHITE));
        });
        return item;
    }

    private Consumer<InventoryClickEvent> bookmarkAction() {
        return event -> {
            playerData.bookmarkCollection().toggleBookmark(getCurrentRecipe());
            renderBookmarkButton(); // Re-render only the bookmark button to update icon
        };
    }

    //#endregion Bookmark

    //#region Bookmark List

    private void renderBookmarkListButton() {
        setItem(BOOKMARK_LIST_SLOT, bookmarkListItem(), bookmarkListAction());
    }

    private ItemStack bookmarkListItem() {
        ItemStack item = new ItemStack(Material.BOOKSHELF);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Bookmark List", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("View your bookmarked recipes", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    private Consumer<InventoryClickEvent> bookmarkListAction() {
        return event -> {
            // Open bookmark list GUI
            RecipeGuiContext context = this.context;
            Player player = (Player) event.getWhoClicked();
            PlayerGuiData playerData = context.getPlayerData(player.getUniqueId());
            Set<Key> bookmarks = playerData.bookmarkCollection().getBookmarkedKeys();

            player.closeInventory();

            TextComponent.Builder text = Component.text();
            text.append(Component.text("All your Bookmarked Recipes:", NamedTextColor.GOLD).style(Style.style(TextDecoration.BOLD))).append(Component.newline());

            for (Key key : bookmarks) {
                text.append(Component.text("- ")).append(
                    Component.text(key.asMinimalString(), NamedTextColor.AQUA)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to view recipe")))
                        .clickEvent(ClickEvent.runCommand(getQuickLinkCmd()))
                ).append(Component.newline());
            }

            player.sendMessage(text.build());
        };
    }

    //#endregion Bookmark List

    //#region Bookmark Server List

    private void renderBookmarkServerListButton() {
        setItem(BOOKMARK_SERVER_LIST_SLOT, bookmarkServerListItem(), bookmarkServerListAction());
    }

    private ItemStack bookmarkServerListItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Server Bookmark List", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("View server-wide bookmarked recipes", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("Not implemented yet", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
            ));
        });
        // Maybe the toggle is just if there is no bookmarks on server don't show it
        return item;
    }

    private Consumer<InventoryClickEvent> bookmarkServerListAction() {
        return event -> {
            HumanEntity human = event.getWhoClicked();
            human.sendMessage(Component.text("Server bookmarked recipes feature is not implemented yet.", NamedTextColor.RED));
        };
    }

    //#endregion Bookmark Server List

    //#region QuickLink

    private void renderQuickLinkButton() {
        setItem(QUICK_LINK_SLOT, quickLinkItem(), quickLinkAction());
    }

    private ItemStack quickLinkItem() {

        ItemStack item = new ItemStack(Material.PAPER);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Quick Link Command", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text(getQuickLinkCmd(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("Click to show in chat", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    private Consumer<InventoryClickEvent> quickLinkAction() {
        return event -> {
            String quickLink = getQuickLinkCmd();
            Component message = Component.text()
                .color(NamedTextColor.GRAY)
                .append(Component.text("Quick link "))
                .append(Component
                    .text(quickLink)
                    .decorate(TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to sugest")))
                    .clickEvent(ClickEvent.suggestCommand(quickLink))
                )
                .build();

            event.getWhoClicked().sendMessage(message);
        };
    } 

    private String getQuickLinkCmd() {
        Recipe currentRecipe = getCurrentRecipe();
        RecipeExtractor extractor = context.getRecipeIndexReader().getAssociatedRecipeExtractor();

        if (!extractor.canHandle(currentRecipe)) {
            return "No quick link available for this recipe";
        }

        Key key = extractor.extractKey(currentRecipe);
        return "/vei --id " + key.asMinimalString();
    }
    //#endregion QuickLink

    //#region Info

    private void renderInfoButton() {
        setItem(INFO_SLOT, infoItem(), infoAction());
    }

    private ItemStack infoItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        item.editMeta(meta -> {
            meta.displayName(Component.text(VanillaEnoughItems.PLUGIN_NAME + " Info", NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("Not implemented yet", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
            ));
        });
        // maybe change to settings ?
        return item;
    }

    private Consumer<InventoryClickEvent> infoAction() {
        return event -> {
            HumanEntity human = event.getWhoClicked();
            human.sendMessage(Component.text(VanillaEnoughItems.PLUGIN_NAME + " info feature is not implemented yet.", NamedTextColor.RED));
        };
    }

    //#endregion Info

    //#region Helpers

    private static ItemStack fillerItem() {
        ItemStack item = ItemType.LIGHT_GRAY_STAINED_GLASS_PANE.createItemStack();

        item.editMeta(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });
        return item;
    }

    private void fillRange(Set<Integer> slots, ItemStack item) {
        for (int slot : slots) {
            setItem(slot, item);
        }
    }

    //#endregion Helpers
}
