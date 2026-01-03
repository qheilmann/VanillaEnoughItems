package dev.qheilmann.vanillaenoughitems.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerGuiData;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.pack.GuiIcon;
import dev.qheilmann.vanillaenoughitems.recipe.RecipeContext;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInv;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.Slots;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
    @SuppressWarnings("unused")
    private final Player player;
    private final RecipeContext context;
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

    public RecipeGui(Player player, RecipeContext context, MultiProcessRecipeReader initialReader) {
        super(SIZE, title(VanillaEnoughItems.config().style()));
        this.player = player;
        this.context = context;
        this.playerData = context.getPlayerData(player.getUniqueId());
        this.style = VanillaEnoughItems.config().style();
        this.reader = initialReader;
        this.guiComponent = new RecipeGuiComponent(style);
        this.fillerItem = guiComponent.createFillerItem();
        
        // Initial filling
        fillRange(Slots.Generic9x6.all(), fillerItem);

        // Static buttons
        renderBookmarkListButton();
        renderBookmarkServerListButton();
        renderInfoButton();

        // Dynamic render
        render();
    }

    private static Component title(Style style) {
        if (style.hasResourcePack()) {
            GuiIcon guiIcon = VeiPack.Font.Gui.BLANK_54;
            return guiIcon.iconComponent().append(Component.text(guiIcon.resetSpace() + "Recipe"));
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
    public void changeRecipeAction(InventoryClickEvent event, ItemStack recipeItem) {
        
        boolean isLeftClick = event.getClick().isLeftClick();
        boolean isRightClick = event.getClick().isRightClick();

        MultiProcessRecipeReader newMultiRecipeReader = null;

        if (isLeftClick) {
            newMultiRecipeReader = context.getRecipeIndex().readerByResult(recipeItem);
        } else if (isRightClick) {
            newMultiRecipeReader = context.getRecipeIndex().readerByUsage(recipeItem);
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

    public void resultItemAction(InventoryClickEvent event, ItemStack recipeItem) {

        boolean isLeftClick = event.getClick().isLeftClick();
        boolean isRightClick = event.getClick().isRightClick();
        
        // Show usage on right click
        if (isRightClick) {
            MultiProcessRecipeReader newMultiRecipeReader = context.getRecipeIndex().readerByUsage(recipeItem);
            if (newMultiRecipeReader != null) {
                // Only push to history if we're actually navigating to a different recipe view
                playerData.navigationHistory().pushForNavigation(reader, newMultiRecipeReader);
                this.reader = newMultiRecipeReader;
                render();
            }
        }

        // Quick link on left click
        if (isLeftClick) {
            quickLinkAction(event);
        }

        // Ignore other click types
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
        
        fillRange(ProcessPannelSlot.all(), fillerItem);

        // Recipe reader dependent buttons
        renderSharedIfPresent(this::renderNextRecipeButton, sharedButtonSlots.get(RecipeGuiSharedButton.NEXT_RECIPE));
        renderSharedIfPresent(this::renderPreviousRecipeButton, sharedButtonSlots.get(RecipeGuiSharedButton.PREVIOUS_RECIPE));
        renderSharedIfPresent(this::renderForwardNavigationButton, sharedButtonSlots.get(RecipeGuiSharedButton.HISTORY_FORWARD));
        renderSharedIfPresent(this::renderBackwardNavigationButton, sharedButtonSlots.get(RecipeGuiSharedButton.HISTORY_BACKWARD));
        renderSharedIfPresent(this::renderQuickCraftButton, sharedButtonSlots.get(RecipeGuiSharedButton.QUICK_CRAFT));
        
        // refact all of this, make sub methodes
        Map<ProcessPannelSlot, CyclicIngredient> tickedIngredient = processPanel.getTickedIngredient();
        Map<ProcessPannelSlot, CyclicIngredient> tickedResults = processPanel.getTickedResults();
        
        Map<ProcessPannelSlot, CyclicIngredient> allTicked = new HashMap<>(tickedIngredient);
        allTicked.putAll(tickedResults);

        Runnable ingredientTicker = () -> tickIngredients(tickedIngredient);
        Runnable resultTicker = () -> tickResults(tickedResults);
        Runnable allTicker = () -> {
            ingredientTicker.run();
            resultTicker.run();
        };

        startTicker(allTicker);

        // Place ticked ingredient slots
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedIngredient.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            CyclicIngredient view = entry.getValue();
            placeIngredient(panelSlot, view);
        }

        // Place ticked result slots
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedResults.entrySet()) {
            ProcessPannelSlot panelSlot = entry.getKey();
            CyclicIngredient view = entry.getValue();
            placeResult(panelSlot, view);
        }
        
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
        ProcessPanel newPanel = context.getProcessPanelRegistry().createPanel(
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
    }

    private void tickIngredients(Map<ProcessPannelSlot, CyclicIngredient> tickedSlots) {
        for (Map.Entry<ProcessPannelSlot, CyclicIngredient> entry : tickedSlots.entrySet()) {
            CyclicIngredient cyclic = entry.getValue();
            if (!cyclic.hasMultipleOptions()) {
                continue;
            }

            ProcessPannelSlot panelSlot = entry.getKey();
            cyclic.tickForward();
            placeIngredient(panelSlot, cyclic);
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

    private void placeIngredient(ProcessPannelSlot panelSlot, CyclicIngredient ingredient) {
        ItemStack item = ingredient.getCurrentItem();
        ItemStack showItem = item.clone();

        List<Component> lore = getLore(showItem);
        lore.add(Component.text("INGREDIENT"));

        if (!showItem.isEmpty()) {
            showItem.lore(lore);
        }
        
        // TODO add tag for common recipeChoices?

        setItem(panelSlot.toSlotIndex(), showItem, event -> changeRecipeAction(event, item));
    }

    private void placeResult(ProcessPannelSlot panelSlot, CyclicIngredient ingredient) {
        ItemStack item = ingredient.getCurrentItem();
        ItemStack showItem = item.clone();
        
        Key recipeKey = getCurrentRecipeKey();
        if (recipeKey != null) {
            List<Component> lore = getLore(showItem);
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

        setItem(panelSlot.toSlotIndex(), showItem, event -> resultItemAction(event, item));
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
            render();
        }
    }

    public void historyBackward() {
        MultiProcessRecipeReader previousReader = playerData.navigationHistory().goBackward(reader);
        if (previousReader != null) {
            this.reader = previousReader;
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
        workbenchScrollOffset = 0; // Reset scroll offset when changing process (to show from the beginning on the new process)
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
        Set<Workbench> workbenches = reader.getCurrentProcess().workbenches();

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
                setItem(slot, symbolItem, event -> changeRecipeAction(event, symbolItem));
            });
        }

        // There are more workbenches than visible slots
        else {
            // Scroll Up button
            if (workbenchScrollOffset > 0) {
                setItem(WORKBENCH_SCROLL_UP_SLOT, workbenchScrollUpItem(workbenchScrollOffset), event -> workbenchScrollUpAction(event));
            } else {
                setItem(WORKBENCH_SCROLL_UP_SLOT, guiComponent.createWorkbenchNonScrollButton(numberOfWorkbenches, false)); // filler item with catalyst background
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
                setItem(slot, symbolItem, event -> changeRecipeAction(event, symbolItem));
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

    //#endregion Workbench Scroll

    //#region Bookmark toggle

    private void renderBookmarkButton() {
        boolean isBookmarked = isCurrentRecipeBookmarked();
        setItem(BOOKMARK_THIS_RECIPE_SLOT, guiComponent.createBookmarkButton(isBookmarked), event -> bookmarkAction(event));
    }

    public boolean isCurrentRecipeBookmarked() {
        return playerData.bookmarkCollection().isBookmarked(getCurrentRecipe());
    }

    private void bookmarkAction(InventoryClickEvent event) {
        playerData.bookmarkCollection().toggleBookmark(getCurrentRecipe());
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        renderBookmarkButton(); // Re-render only the bookmark button to update icon
    }

    //#endregion Bookmark

    //#region Bookmark List

    private void renderBookmarkListButton() {
        setItem(BOOKMARK_LIST_SLOT, guiComponent.createBookmarkListButton(), event -> bookmarkListAction(event));
    }

    private void bookmarkListAction(InventoryClickEvent event) {
        // Open bookmark list GUI
        RecipeContext context = this.context;
        HumanEntity humanEntity = event.getWhoClicked();
        PlayerGuiData playerData = context.getPlayerData(humanEntity.getUniqueId());
        Set<Key> bookmarks = playerData.bookmarkCollection().getBookmarkedKeys();

        humanEntity.playSound(UI_CLICK_SOUND);
        humanEntity.closeInventory();

        TextComponent.Builder text = Component.text();
        text.append(Component.text("All your Bookmarked Recipes:", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)).append(Component.newline());

        for (Key key : bookmarks) {
            text.append(Component.text("- ")).append(
                Component.text(key.asMinimalString(), NamedTextColor.AQUA)
                    .decorate(TextDecoration.UNDERLINED)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to view recipe")))
                    .clickEvent(ClickEvent.runCommand(getQuickLinkCmd()))
            ).append(Component.newline());
        }

        humanEntity.sendMessage(text.build());
    }

    //#endregion Bookmark List

    //#region Bookmark Server List

    private void renderBookmarkServerListButton() {
        setItem(BOOKMARK_SERVER_LIST_SLOT, guiComponent.createBookmarkServerListButton(), event -> bookmarkServerListAction(event));
    }

    private void bookmarkServerListAction(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.playSound(UI_CLICK_SOUND);
        humanEntity.sendMessage(Component.text("Server bookmarked recipes feature is not implemented yet.", NamedTextColor.RED));
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

    private void fillRange(Set<Integer> slots, ItemStack item) {
        for (int slot : slots) {
            setItem(slot, item);
        }
    }

    /**
     * Extracts the unique key of the given recipe using the associated recipe extractor.
     *
     * @param recipe The recipe to extract the key from.
     * @return The unique key of the recipe, or null if the extractor cannot handle the recipe.
     */
    @Nullable
    private Key getCurrentRecipeKey() {
        Recipe recipe = getCurrentRecipe();
        RecipeExtractor extractor = context.getRecipeIndex().getAssociatedRecipeExtractor();

        if (!extractor.canHandle(recipe)) {
            return null;
        }

        return extractor.extractKey(recipe);
    }

    //#endregion Helpers
}
