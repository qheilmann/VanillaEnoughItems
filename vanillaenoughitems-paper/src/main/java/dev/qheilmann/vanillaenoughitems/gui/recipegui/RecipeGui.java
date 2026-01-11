package dev.qheilmann.vanillaenoughitems.gui.recipegui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.RecipeServices;
import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.bookmark.Bookmark;
import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.bookmarkgui.BookmarkGui;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerGuiData;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.pack.GuiIcon;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.index.Grouping;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInv;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.Slots;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class RecipeGui extends FastInv {

    // GUI size
    private static final int SIZE = FastInv.GENERIC_9X6_SIZE;
    private static final int TICK_INTERVAL = 20; // ticks (1 second)
    
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
    
    private static final int MAX_VISIBLE_PROCESSES = PROCESSES_SCROLL_RANGE.size() - 1; // Never fill the first slot
    private static final int MAX_SCROLLABLE_PROCESSES = PROCESSES_SCROLL_RANGE.size() - 2; // Account for scroll buttons
    private static final int MAX_VISIBLE_WORKBENCHES = WORKBENCHS_SCROLL_RANGE.size() - 1; // Never fill the first slot
    private static final int MAX_SCROLLABLE_WORKBENCHES = WORKBENCHS_SCROLL_RANGE.size() - 2; // Account for scroll buttons

    // Sounds
    private static final Sound UI_CLICK_SOUND = Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.UI, 0.25f, 1.0f);

    // Instance
    private final RecipeServices services;
    private final PlayerGuiData playerData;
    private final Style style;
    private final RecipeGuiComponent guiComponent;
    private final ItemStack fillerItem;
    // Mutable state
    private MultiProcessRecipeReader reader;
    private @Nullable BukkitTask tickTask;
    /** Scroll offset for process tabs (in case of more than one page) */
    private int processScrollOffset = 0;
    /** Scroll offset for workbench tabs (in case of more than one page) */
    private int workbenchScrollOffset = 0;

    public RecipeGui(RecipeServices services, PlayerGuiData playerData, MultiProcessRecipeReader reader) {
        super(SIZE, title(VanillaEnoughItems.config().style()));
        this.services = services;
        this.playerData = playerData;
        this.style = VanillaEnoughItems.config().style();
        this.reader = reader;
        this.guiComponent = new RecipeGuiComponent(style);
        this.fillerItem = guiComponent.createFillerItem();
        
        // Inform navigation history a new viewing session
        playerData.navigationHistory().startViewing(reader);
        
        // Initial filling
        setItems(Slots.Generic9x6.all(), fillerItem);

        // Static buttons
        renderBookmarkListButton();
        renderBookmarkServerListButton();
        // renderInfoButton(); // TODO info button not implemented yet

        // Dynamic render
        render();
    }

    private static Component title(Style style) {
        if (style.hasResourcePack()) {
            return VeiPack.Font.Gui.BLANK_54.iconComponent().append(Component.text("Recipe"));
        } else {
            return Component.text("Recipe");
        }
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
    public void nonResultChangeRecipeAction(InventoryClickEvent event, ItemStack recipeItem) {
        
        boolean isLeftClick = event.getClick().isLeftClick();
        boolean isRightClick = event.getClick().isRightClick();

        MultiProcessRecipeReader newMultiRecipeReader = null;

        if (isLeftClick) {
            newMultiRecipeReader = services.recipeIndex().readerByResult(recipeItem);
        } else if (isRightClick) {
            if (reader.getGrouping().equals(new Grouping.ByIngredient(recipeItem))) {
                return; // No action if usage clicking on the same usage reader
            }
            newMultiRecipeReader = services.recipeIndex().readerByIngredient(recipeItem);
        } 
        // Ignore other click types

        openNewReader(newMultiRecipeReader);
    }

    public void resultChangeRecipeAction(InventoryClickEvent event, ItemStack recipeItem) {
        
        boolean isLeftClick = event.getClick().isLeftClick();
        boolean isRightClick = event.getClick().isRightClick();

        MultiProcessRecipeReader newMultiRecipeReader = null;

        if (isLeftClick) {
            if (reader.getGrouping().equals(new Grouping.ByResult(recipeItem))) {
                return; // No action if recipe clicking on the same result reader
            }
            newMultiRecipeReader = services.recipeIndex().readerByResult(recipeItem);
        } else if (isRightClick) {
            newMultiRecipeReader = services.recipeIndex().readerByIngredient(recipeItem);
        }
        // Ignore other click types

        // If valide new reader found
        openNewReader(newMultiRecipeReader);
    }

    public Recipe getCurrentRecipe() {
        return reader.getCurrentProcessRecipeReader().getCurrent();
    }

    public Process getCurrentProcess() {
        return reader.getCurrentProcess();
    }

    //#region Process Panel

    /**
     * Render the given process panel into the GUI
     * 
     * @param processPanel the panel to render
     */
    private void renderProcessPanel(ProcessPanel processPanel) {
        Map<RecipeGuiSharedButton, ProcessPannelSlot> sharedButtonSlots = processPanel.getRecipeGuiButtonMap();
        
        setItems(ProcessPannelSlot.all(), fillerItem);

        // Recipe reader dependent buttons
        renderSharedIfPresent(this::renderNextRecipeButton, sharedButtonSlots.get(RecipeGuiSharedButton.NEXT_RECIPE));
        renderSharedIfPresent(this::renderPreviousRecipeButton, sharedButtonSlots.get(RecipeGuiSharedButton.PREVIOUS_RECIPE));
        renderSharedIfPresent(this::renderForwardNavigationButton, sharedButtonSlots.get(RecipeGuiSharedButton.HISTORY_FORWARD));
        renderSharedIfPresent(this::renderBackwardNavigationButton, sharedButtonSlots.get(RecipeGuiSharedButton.HISTORY_BACKWARD));
        // renderSharedIfPresent(this::renderQuickCraftButton, sharedButtonSlots.get(RecipeGuiSharedButton.QUICK_CRAFT)); // TODO quick craft not implemented yet
        
        // Separate ticked items from pinned items based on grouping
        Map<ProcessPannelSlot, CyclicIngredient> tickedIngredient = processPanel.getTickedIngredient();
        Map<ProcessPannelSlot, CyclicIngredient> tickedResults = processPanel.getTickedResults();
        Map<ProcessPannelSlot, CyclicIngredient> tickedOther = processPanel.getTickedOther();
        
        // Filter out pinned items from ticker
        Map<ProcessPannelSlot, CyclicIngredient> tickedIngredientFiltered = new HashMap<>();
        Map<ProcessPannelSlot, CyclicIngredient> tickedResultsFiltered = new HashMap<>();
        
        // Place ingredient slots - filter out pinned items from ticker
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedIngredient.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            CyclicIngredient cyclic = entry.getValue();
            
            ItemStack pinnedItem = getPinnedIngredient();
            if (pinnedItem != null && cyclic.contains(pinnedItem)) {
                // Pin this ingredient - set to pinned item and don't add to ticker
                cyclic.pin(pinnedItem);
                placeNonResult(panelSlot, cyclic);
            } else {
                // Normal cycling behavior
                tickedIngredientFiltered.put(panelSlot, cyclic);
                placeNonResult(panelSlot, cyclic);
            }
        }
        
        // Place "other" slots
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedOther.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            CyclicIngredient cyclic = entry.getValue();
            
            // Always use normal cycling behavior (never pin)
            placeNonResult(panelSlot, cyclic);
        }

        // Place result slots - filter out pinned items from ticker
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedResults.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            CyclicIngredient cyclic = entry.getValue();
            
            ItemStack pinnedItem = getPinnedResult();
            if (pinnedItem != null && cyclic.contains(pinnedItem)) {
                // Pin this result - set to pinned item and don't add to ticker
                cyclic.pin(pinnedItem);
                placeResult(panelSlot, cyclic);
            } else {
                // Normal cycling behavior
                tickedResultsFiltered.put(panelSlot, cyclic);
                placeResult(panelSlot, cyclic);
            }
        }
        
        // Start ticker
        Runnable ingredientTicker = () -> tickNonResult(tickedIngredientFiltered);
        Runnable otherTicker = () -> tickNonResult(tickedOther);
        Runnable resultTicker = () -> tickResults(tickedResultsFiltered);

        startTicker(() -> {
            ingredientTicker.run();
            otherTicker.run();
            resultTicker.run();
        });
        
        // Place static decorative items
        Map<ProcessPannelSlot, FastInvItem> staticItems = processPanel.getStaticItems();
        for (Map.Entry<ProcessPannelSlot, FastInvItem> entry : staticItems.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            FastInvItem item = entry.getValue();
            setItem(panelSlot.toSlotIndex(), item);
        }
    }

    private void renderSharedIfPresent(Consumer<Integer> runnable, ProcessPannelSlot processPannelSlot) {
        if (processPannelSlot != null) {
            if (runnable != null) {
                runnable.accept(processPannelSlot.toSlotIndex());
            }
        }
    }

    private ProcessPanel generateCurrentPanel() {
        ProcessPanel newPanel = services.processPanelRegistry().createPanel(
            getCurrentProcess(),
            getCurrentRecipe(),
            style
        );

        return newPanel;
    }

    //#region Ingredient / Result Ticker

    private void startTicker(Runnable ticker) {
        if (tickTask != null) {
            tickTask.cancel();
        }

        tickTask = Bukkit.getScheduler().runTaskTimer(
            VanillaEnoughItems.getPlugin(),
            ticker,
            TICK_INTERVAL,
            TICK_INTERVAL
        );
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        // Save current reader to navigation history
        playerData.navigationHistory().stopViewing(reader);
    }

    private void tickNonResult(Map<ProcessPannelSlot, CyclicIngredient> tickedSlots) {
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedSlots.entrySet()) {
            CyclicIngredient cyclic = entry.getValue();
            if (!cyclic.hasMultipleOptions()) {
                continue;
            }

            ProcessPannelSlot panelSlot = entry.getKey();
            cyclic.tickForward();
            placeNonResult(panelSlot, cyclic);
        }
    }

    private void tickResults(Map<ProcessPannelSlot, CyclicIngredient> tickedSlots) {
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedSlots.entrySet()) {
            CyclicIngredient cyclic = entry.getValue();
            if (!cyclic.hasMultipleOptions()) {
                continue;
            }

            ProcessPannelSlot panelSlot = entry.getKey();
            cyclic.tickForward();
            placeResult(panelSlot, cyclic);
        }
    }

    private void placeNonResult(ProcessPannelSlot panelSlot, CyclicIngredient ingredient) {
        ItemStack item = ingredient.getCurrentItem();
        ItemStack showItem = item.clone();

        List<Component> lore = getLore(showItem);
        lore.add(Component.empty()); // empty line
        
        // Add tag lore for items that exactly match tags
        lore.addAll(formatTagLore(ingredient));

        if (!showItem.isEmpty()) {
            showItem.lore(lore);
        }

        setItem(panelSlot.toSlotIndex(), showItem, event -> nonResultChangeRecipeAction(event, item));
    }

    private void placeResult(ProcessPannelSlot panelSlot, CyclicIngredient ingredient) {
        ItemStack item = ingredient.getCurrentItem();
        ItemStack showItem = item.clone();
        
        Key recipeKey = getCurrentRecipeKey();
        if (recipeKey != null) {
            List<Component> lore = getLore(showItem);
            lore.add(Component.empty()); // empty line
            
            // Add tag lore for items that exactly match tags
            lore.addAll(formatTagLore(ingredient));
            
            // Recipe by (if custom)
            if (!recipeKey.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
                
                // TODO make a registry for custom namespace -> plugin name mapping
                String namespace = recipeKey.namespace();
                namespace = namespace.replaceAll("_", " "); // replace underscores with spaces
                namespace = namespace.substring(0, 1).toUpperCase() + namespace.substring(1); // capitalize first letter

                Component customRecipeComp = Component.text().color(style.colorPrimaryVariant()).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Recipe by: "))
                    .append(Component.text(namespace, style.colorSecondary()))
                    .build();
                lore.add(customRecipeComp);
            }

            // Recipe ID
            Component recipeKeyComp = Component.text().color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Recipe ID: "))
                .append(Component.text(recipeKey.asString()))
                .build();
            lore.add(recipeKeyComp);

            if (!showItem.isEmpty()) {
                showItem.lore(lore);
            }
        }

        setItem(panelSlot.toSlotIndex(), showItem, event -> resultChangeRecipeAction(event, item));
    }

    private List<Component> getLore(ItemStack item) {
        List<Component> lore = new ArrayList<>();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                lore = meta.lore();
            }
        }
        return lore;
    }

    //#endregion Ingredient / Result Ticker

    //#endregion Process Panel

    //#region Next/Previous Recipe

    private void renderNextRecipeButton(int slot) {
        if (!hasNextRecipe()) {
            setItem(slot, fillerItem);
            return;
        }

        setItem(slot, guiComponent.createNextRecipeButton(), event -> nextRecipeAction(event));
    }

    private void renderPreviousRecipeButton(int slot) {
        if (!hasPreviousRecipe()) {
            setItem(slot, fillerItem);
            return;
        }

        setItem(slot, guiComponent.createPreviousRecipeButton(), event -> previousRecipeAction(event));
    }

    private void nextRecipeAction(InventoryClickEvent event) {
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        nextRecipe();
    }

    private void previousRecipeAction(InventoryClickEvent event) {
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        previousRecipe();
    }

    public void nextRecipe() {
        if (!hasNextRecipe()) {
            return;
        }
        reader.getCurrentProcessRecipeReader().next();
        render();
    }

    public void previousRecipe() {
        if (!hasPreviousRecipe()) {
            return;
        }
        reader.getCurrentProcessRecipeReader().previous();
        render();
    }

    public boolean hasNextRecipe() {
        return !reader.getCurrentProcessRecipeReader().isLast();
    }

    public boolean hasPreviousRecipe() {
        return !reader.getCurrentProcessRecipeReader().isFirst();
    }

    //#endregion Next/Previous Recipe

    //#region Forward/Backward Navigation

    private void renderForwardNavigationButton(int slot) {
        if (!hasForwardNavigation()) {
            setItem(slot, fillerItem);
            return;
        }

        setItem(slot, guiComponent.createForwardNavigationButton(), event -> historyForwardAction(event));
    }

    private void renderBackwardNavigationButton(int slot) {
        if (!hasBackwardNavigation()) {
            setItem(slot, fillerItem);
            return;
        }

        setItem(slot, guiComponent.createBackwardNavigationButton(), event -> historyBackwardAction(event));
    }

    private void historyForwardAction(InventoryClickEvent event) {
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        historyForward();
    }
    
    private void historyBackwardAction(InventoryClickEvent event) {
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        historyBackward();
    }

    public void historyForward() {
        MultiProcessRecipeReader nextReader = playerData.navigationHistory().goForward(reader);
        if (nextReader != null) {
            this.reader = nextReader;
            processScrollOffset = 0; // Reset process scroll on navigation
            workbenchScrollOffset = 0; // Reset workbench scroll on navigation
            render();
        }
    }

    public void historyBackward() {
        MultiProcessRecipeReader previousReader = playerData.navigationHistory().goBackward(reader);
        if (previousReader != null) {
            this.reader = previousReader;
            processScrollOffset = 0; // Reset process scroll on navigation
            workbenchScrollOffset = 0; // Reset workbench scroll on navigation
            render();
        }
    }

    public boolean hasForwardNavigation() {
        return playerData.navigationHistory().canGoForward();
    }

    public boolean hasBackwardNavigation() {
        return playerData.navigationHistory().canGoBackward();
    }

    //#endregion Forward/Backward Navigation

    //#region Quick Craft

    @SuppressWarnings("unused")
    private void renderQuickCraftButton(int slot) {
        setItem(slot, guiComponent.createQuickCraftButton(), event -> quickCraftAction(event));
    }

    private void quickCraftAction(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.playSound(UI_CLICK_SOUND);
        humanEntity.sendMessage(Component.text("Quick Craft is not yet implemented!", NamedTextColor.RED));
    }

    //#endregion Quick Craft

    //#region Process Scroll

    private void renderProcessScrollButtons() {        
        NavigableSet<Process> processes = reader.getAllProcesses();

        int numberOfProcesses = processes.size();
        Iterator<Integer> slotIterator = PROCESSES_SCROLL_RANGE.iterator();
        Iterator<Process> processIterator = processes.iterator();
        int currentProcessIndex = getCurrentProcessIndex();

        // There are equal or less processes than visible slots
        if (numberOfProcesses <= MAX_VISIBLE_PROCESSES) {
            // Never fill the first slot with process
            setItem(slotIterator.next(), guiComponent.createProcessNonScrollButton(numberOfProcesses, currentProcessIndex, true));

            slotIterator.forEachRemaining(slot -> {
                if (!processIterator.hasNext()) {
                    setItem(slot, fillerItem);
                    return;
                }
                placeProcessTab(slot, processIterator.next());
            });
        }

        // There are more processes than visible slots
        else {
            // Scroll Left button
            if (this.processScrollOffset > 0) {
                setItem(PROCESS_SCROLL_LEFT_SLOT, processScrollLeftItem(this.processScrollOffset, currentProcessIndex), event -> processScrollLeftAction(event));
            } else {
                setItem(PROCESS_SCROLL_LEFT_SLOT, guiComponent.createProcessNonScrollButton(numberOfProcesses, currentProcessIndex, false));
            }
            // Scroll Right button
            if (this.processScrollOffset < numberOfProcesses - MAX_SCROLLABLE_PROCESSES) {
                setItem(PROCESS_SCROLL_RIGHT_SLOT, processScrollRightItem(this.processScrollOffset), event -> processScrollRightAction(event));
            } else {
                setItem(PROCESS_SCROLL_RIGHT_SLOT, fillerItem);
            }
            // Process items
            slotIterator.next(); // Skip scroll left slot
            for (int i = 0; i < this.processScrollOffset; i++) {
                processIterator.next();
            }
            for (int i = 0; i < MAX_SCROLLABLE_PROCESSES; i++) {
                placeProcessTab(slotIterator.next(), processIterator.next());
            }
        }
    }

    /**
     * Gets the index of the current process relative to the visible process tabs.
     * 
     * @return The relative index of the current process.
     */
    private int getCurrentProcessIndex() {
        NavigableSet<Process> processes = reader.getAllProcesses();
        int absoluteIndex = 0;
        for (Process process : processes) {
            if (Process.COMPARATOR.compare(process, getCurrentProcess()) == 0) {
                break;
            }
            absoluteIndex++;
        }

        int relativeIndex = absoluteIndex - this.processScrollOffset;
        return relativeIndex;
    }

    private void placeProcessTab(int slot, Process process) {
        ItemStack symbolItem = process.symbol();

        Comparator<Process> comparator = Process.COMPARATOR;
        if(comparator.compare(process, getCurrentProcess()) == 0) {
            setItem(slot, symbolItem); // no action on clicking the current process
        } else {
            setItem(slot, symbolItem, event -> changeProcessAction(event, process));
        }
    }

    private void changeProcessAction(InventoryClickEvent event, Process process) {
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        changeProcess(process);
    }

    private void changeProcess(Process process) {
        workbenchScrollOffset = 0; // Reset workbench scroll offset on process change
        reader.setCurrentProcess(process);
        render();
    }

    private ItemStack processScrollLeftItem(int processScrollOffset, int currentProcessIndex) {
        return guiComponent.createProcessScrollLeftButton(processScrollOffset, currentProcessIndex);
    }

    private ItemStack processScrollRightItem(int processScrollOffset) {
        int numberOfProcesses = reader.getAllProcesses().size();
        int moreNumber = numberOfProcesses - MAX_SCROLLABLE_PROCESSES - processScrollOffset;
        return guiComponent.createProcessScrollRightButton(moreNumber);
    }

    private void processScrollLeftAction(InventoryClickEvent event) {
        int newOffset = processScrollOffset - 1;
        this.processScrollOffset = Math.max(0, newOffset);
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        renderProcessScrollButtons();
    }

    private void processScrollRightAction(InventoryClickEvent event) {
        int numberOfProcesses = reader.getAllProcesses().size();
        int newOffset = processScrollOffset + 1;
        this.processScrollOffset = Math.min(numberOfProcesses - MAX_SCROLLABLE_PROCESSES, newOffset);
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        renderProcessScrollButtons();
    }

    //#endregion Process Scroll

    //#region Workbench Scroll

    private void renderWorkbenchScrollButtons() {      
        Process process = reader.getCurrentProcess();  
        Set<Workbench> workbenches = process.workbenches();

        int numberOfWorkbenches = workbenches.size();
        Iterator<Integer> slotIterator = WORKBENCHS_SCROLL_RANGE.iterator();
        Iterator<Workbench> workbenchIterator = workbenches.iterator();

        // There are equal or less workbenches than visible slots
        if (numberOfWorkbenches <= MAX_VISIBLE_WORKBENCHES) {
            // Never fill the first slot with catalyst
            setItem(slotIterator.next(),  guiComponent.createWorkbenchNonScrollButton(numberOfWorkbenches, true));

            // Fill all workbenches
            slotIterator.forEachRemaining(slot -> {
                if (!workbenchIterator.hasNext()) {
                    setItem(slot, fillerItem);
                    return;
                }
                Workbench workbench = workbenchIterator.next();
                ItemStack symbolItem = workbench.symbol();
                setItem(slot, symbolItem, event -> catalysteAction(event, process, symbolItem));
            });
        }

        // There are more workbenches than visible slots
        else {
            // Scroll Up button
            if (workbenchScrollOffset > 0) {
                setItem(WORKBENCH_SCROLL_UP_SLOT, workbenchScrollUpItem(workbenchScrollOffset), event -> workbenchScrollUpAction(event));
            } else {
                setItem(WORKBENCH_SCROLL_UP_SLOT, guiComponent.createWorkbenchNonScrollButton(numberOfWorkbenches, false), null); // filler item with catalyst background
            }
            // Scroll Down button
            if (workbenchScrollOffset < numberOfWorkbenches - MAX_SCROLLABLE_WORKBENCHES) {
                setItem(WORKBENCH_SCROLL_DOWN_SLOT, workbenchScrollDownItem(workbenchScrollOffset), event -> workbenchScrollDownAction(event));
            } else {
                setItem(WORKBENCH_SCROLL_DOWN_SLOT, fillerItem);
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
                setItem(slot, symbolItem, event -> catalysteAction(event, process, symbolItem));
            }
        }
    }

    private ItemStack workbenchScrollUpItem(int workbenchScrollOffset) {
        return guiComponent.createWorkbenchScrollUpButton(workbenchScrollOffset);
    }

    private ItemStack workbenchScrollDownItem(int workbenchScrollOffset) {
        int numberOfWorkbenches = reader.getCurrentProcess().workbenches().size();
        int moreNumber = numberOfWorkbenches - MAX_SCROLLABLE_WORKBENCHES - workbenchScrollOffset;
        return guiComponent.createWorkbenchScrollDownButton(moreNumber);
    }

    private void workbenchScrollUpAction(InventoryClickEvent event) {
        int newOffset = workbenchScrollOffset - 1;
        this.workbenchScrollOffset = Math.max(0, newOffset);
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        renderWorkbenchScrollButtons();
    }

    private void workbenchScrollDownAction(InventoryClickEvent event) {
        int numberOfWorkbenches = reader.getCurrentProcess().workbenches().size();
        int newOffset = workbenchScrollOffset + 1;
        this.workbenchScrollOffset = Math.min(numberOfWorkbenches - MAX_SCROLLABLE_WORKBENCHES, newOffset);
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        renderWorkbenchScrollButtons();
    }

    public void catalysteAction(InventoryClickEvent event, Process process, ItemStack catalyste) {
        
        boolean isLeftClick = event.getClick().isLeftClick();
        boolean isRightClick = event.getClick().isRightClick();

        MultiProcessRecipeReader newMultiRecipeReader = null;

        if (isLeftClick) {
            newMultiRecipeReader = services.recipeIndex().readerByResult(catalyste);
        } else if (isRightClick) {
            if (reader.getGrouping().equals(new Grouping.ByProcess(process))) {
                return; // No action if process clicking on the same process reader
            }
            // Actually this should more be process reader + all usage of catalyste like in other recipe + other case (like fuel for crafting table catalyste)
            newMultiRecipeReader = services.recipeIndex().readerByProcess(process);
        }
        // Ignore other click types

        openNewReader(newMultiRecipeReader);
    }

    //#endregion Workbench Scroll

    //#region Bookmark toggle

    private void renderBookmarkButton() {
        boolean isBookmarked = isCurrentRecipeBookmarked();
        setItem(BOOKMARK_THIS_RECIPE_SLOT, guiComponent.createBookmarkButton(isBookmarked), event -> bookmarkAction(event));
    }

    public boolean isCurrentRecipeBookmarked() {
        Key key = getCurrentRecipeKey();
        if (key == null) {
            return false;
        }

        Bookmark tempBookmark = Bookmark.fromKey(key, services.recipeIndex(), services.processPanelRegistry());
        if (tempBookmark == null) {
            return false;
        }
        
        return playerData.containsBookmark(tempBookmark);
    }

    private void bookmarkAction(InventoryClickEvent event) {
        Key key = getCurrentRecipeKey();
        if (key == null) {
            return;
        }
        Bookmark bookmark = Bookmark.fromKey(key, services.recipeIndex(), services.processPanelRegistry());
        if (bookmark == null) {
            return;
        }
        
        playerData.toggleBookmark(bookmark);
        
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        renderBookmarkButton(); // Re-render only the bookmark button to update icon
    }

    //#endregion Bookmark

    //#region Bookmark List

    private void renderBookmarkListButton() {
        setItem(BOOKMARK_LIST_SLOT, guiComponent.createBookmarkListButton(), event -> bookmarkListAction(event));
    }

    private void bookmarkListAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.playSound(UI_CLICK_SOUND);
        
        BookmarkGui bookmarkGui = new BookmarkGui(Component.text("Player Bookmarks"), services, playerData, playerData.getBookmarks());
        bookmarkGui.open(player);
    }

    //#endregion Bookmark List

    //#region Bookmark Server List

    private void renderBookmarkServerListButton() {
        setItem(BOOKMARK_SERVER_LIST_SLOT, guiComponent.createBookmarkServerListButton(), event -> bookmarkServerListAction(event));
    }

    private void bookmarkServerListAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.playSound(UI_CLICK_SOUND);
        
        BookmarkGui bookmarkGui = new BookmarkGui(Component.text("Server Bookmarks"), services, playerData, services.serverBookmarkRegistry().getBookmarks());
        bookmarkGui.open(player);
    }

    //#endregion Bookmark Server List

    //#region QuickLink

    private void renderQuickLinkButton() {
        setItem(QUICK_LINK_SLOT, guiComponent.createQuickLinkButton(), event -> quickLinkAction(event));
    }

    private void quickLinkAction(InventoryClickEvent event) {
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

        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.sendMessage(message);
        humanEntity.playSound(UI_CLICK_SOUND);
    } 

    private String getQuickLinkCmd() {

        Key recipeKey = getCurrentRecipeKey();
        if (recipeKey == null) {
            return "No quick link available for this recipe";
        }

        return "/vei --id " + recipeKey.asMinimalString();
    }
    //#endregion QuickLink

    //#region Info

    @SuppressWarnings("unused")
    private void renderInfoButton() {
        setItem(INFO_SLOT, guiComponent.createInfoButton(), event -> infoAction(event));
    }

    private void infoAction(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.playSound(UI_CLICK_SOUND);
        humanEntity.sendMessage(Component.text(VanillaEnoughItems.PLUGIN_NAME + " info feature is not implemented yet.", NamedTextColor.RED));
    }

    //#endregion Info

    //#region Helpers

    /**
     * Extracts the unique key of the given recipe using the associated recipe extractor.
     *
     * @param recipe The recipe to extract the key from.
     * @return The unique key of the recipe, or null if the extractor cannot handle the recipe.
     */
    @Nullable
    private Key getCurrentRecipeKey() {
        Recipe recipe = getCurrentRecipe();
        RecipeExtractor extractor = services.recipeIndex().getAssociatedRecipeExtractor();

        if (!extractor.canHandle(recipe)) {
            return null;
        }

        return extractor.extractKey(recipe);
    }

    /**
     * Gets the (asOne) ingredient that should be pinned based on grouping.
     * @return the pinned ingredient, or null if no pinning
     */
    @Nullable
    private ItemStack getPinnedIngredient() {
        return switch (reader.getGrouping()) {
            case Grouping.ByIngredient g -> g.ingredient();
            default -> null;
        };
    }

    /**
     * Gets the (asOne) result that should be pinned based on grouping.
     * @return the pinned result, or null if no pinning
     */
    @Nullable
    private ItemStack getPinnedResult() {
        return switch (reader.getGrouping()) {
            case Grouping.ByResult g -> g.result();
            default -> null;
        };
    }
    
    /**
     * Format tag lore for a CyclicIngredient.
     * Returns tags that exactly match the items in the cyclic.
     * 
     * @param cyclic the cyclic ingredient
     * @return list of formatted tag components, or empty if no exact matches
     */
    private List<Component> formatTagLore(CyclicIngredient cyclic) {
        // Dependent ingredients cannot enumerate their options
        // Also ignore unique ingredients
        if (cyclic.isDependent() || cyclic.hasMultipleOptions() == false) {
            return List.of();
        }
        
        // Get all item types from the cyclic's options
        ItemStack[] options = cyclic.getOptions();
        Set<ItemType> itemTypes = new HashSet<>();
        for (ItemStack option : options) {
            if (!option.isEmpty()) {
                itemTypes.add(option.getType().asItemType());
            }
        }
        
        // Find tags that exactly match this set
        Set<TagKey<ItemType>> matchingTags = services.tagIndex().getTagsExactlyMatching(itemTypes);
        
        if (matchingTags.isEmpty()) {
            return List.of();
        }
        
        // Build lore components
        List<Component> tagLore = new ArrayList<>();
        String title;
        if (matchingTags.size() >= 2) {
            title = "Accepts Tags:";
        } else {
            title = "Accepts Tag:";
        }
        tagLore.add(Component.text(title, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        for (TagKey<ItemType> tagKey : matchingTags) {
            Component tagComponent = Component.text("#" + tagKey.key().asString(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
            tagLore.add(tagComponent);
        }
        
        return tagLore;
    }

    /**
     * Opens a new MultiProcessRecipeReader, pushing the current one to navigation history.
     * 
     * @param newMultiRecipeReader the new reader to open or null to ignore
     */
    private void openNewReader(@Nullable MultiProcessRecipeReader newMultiRecipeReader) {
        if (newMultiRecipeReader != null) {
            // Only push to history if we're actually navigating to a different recipe view
            playerData.navigationHistory().pushForNavigation(reader, newMultiRecipeReader);
            this.reader = newMultiRecipeReader;
            processScrollOffset = 0; // Reset process scroll on recipe change
            workbenchScrollOffset = 0; // Reset workbench scroll on recipe change
            render();
        }
    }

    //#endregion Helpers
}
