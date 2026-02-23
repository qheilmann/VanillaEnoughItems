package dev.qheilmann.vanillaenoughitems.gui.bookmarkgui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.RecipeServices;
import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.bookmark.Bookmark;
import dev.qheilmann.vanillaenoughitems.config.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.helper.GuiComponentHelper;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerGuiData;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGui;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInv;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.Slots;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;

/**
 * Unified GUI for displaying bookmarks (both player and server bookmarks).
 * Shows bookmarks with cycling symbols and allows clicking to open recipes.
 */
@NullMarked
public class BookmarkGui extends FastInv {

    private static final int SIZE = Slots.Generic9x6.SIZE;
    private static final int TICK_INTERVAL = 20; // ticks (1 second)
    private static final int MAX_BOOKMARKS = Slots.Generic9x6.WIDTH * 5;
    
    // Navigation slots (bottom row)
    private static final int PREVIOUS_PAGE_SLOT = Slots.Generic9x6.slot(2, 5);
    private static final int RETURN_BUTTON_SLOT = Slots.Generic9x6.slot(4, 5);
    private static final int NEXT_PAGE_SLOT = Slots.Generic9x6.slot(6, 5);
    
    // Sounds
    private static final Sound UI_CLICK_SOUND = Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.UI, 0.25f, 1.0f);

    private final RecipeServices services;
    private final PlayerGuiData playerData;
    private final Style style;
    private final BookmarkGuiComponent guiComponent;
    private final ItemStack fillerItem;
    private final List<Bookmark> bookmarks;
    private final Map<Integer, Bookmark> slotToBookmark = new HashMap<>();
    private final @Nullable MultiProcessRecipeReader returnToReader;
    private @Nullable BukkitTask tickTask;
    private int pageOffset = 0;

    /**
     * Create a bookmark GUI with title component (icon will be appended automatically)
     * @param titleComponent the title component (without icon)
     * @param services the recipe services
     * @param playerData the current player's GUI data
     * @param bookmarks the bookmarks to display
     * @param returnToReader optional reader to return to when using the return button (can be null)
     */
    public BookmarkGui(Component titleComponent, RecipeServices services, PlayerGuiData playerData, Collection<Bookmark> bookmarks, @Nullable MultiProcessRecipeReader returnToReader) {
        super(SIZE, title(titleComponent, VanillaEnoughItems.veiConfig().style()));
        this.services = services;
        this.playerData = playerData;
        this.style = VanillaEnoughItems.veiConfig().style();
        this.guiComponent = new BookmarkGuiComponent(style);
        this.fillerItem = GuiComponentHelper.createFillerItem(style.hasResourcePack());
        this.bookmarks = new ArrayList<>(bookmarks);
        this.returnToReader = returnToReader;

        // Fill with filler items
        setItems(Slots.Generic9x6.all(), fillerItem);
        
        render();
    }

    private void render() {
        slotToBookmark.clear();

        // Calculate which bookmarks to show based on current page
        int startIndex = pageOffset * MAX_BOOKMARKS;
        int endIndex = Math.min(startIndex + MAX_BOOKMARKS, bookmarks.size());
        
        // Render bookmarks for current page
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            renderBookmark(slot, bookmarks.get(i));
            slot++;
        }
        
        // Clear remaining slots on current page
        while (slot < MAX_BOOKMARKS) {
            setItem(slot, fillerItem);
            slot++;
        }

        // Render navigation buttons
        renderNavigationButtons();

        // Start ticker for cycling symbols
        startTicker();
    }

    private void renderBookmark(int slot, Bookmark bookmark) {
        CyclicIngredient symbol = bookmark.getSymbol();
        slotToBookmark.put(slot, bookmark);
        
        ItemStack bookmarkItem = symbol.getCurrentItem();
        setItem(slot, bookmarkItem, event -> clickAction(event, bookmark));
    }

    private void clickAction(InventoryClickEvent event, Bookmark bookmark) {
        Player player = (Player) event.getWhoClicked();
        MultiProcessRecipeReader reader = bookmark.getReader();
        new RecipeGui(services, playerData, reader).open(player);
    }
    
    private void renderNavigationButtons() {
        // Previous page button
        if (hasPreviousPage()) {
            setItem(PREVIOUS_PAGE_SLOT, guiComponent.createPreviousPageButton(), event -> previousPageAction(event));
        } else {
            setItem(PREVIOUS_PAGE_SLOT, fillerItem);
        }
        
        // Return button
        if (hasReturnReader()) {
            setItem(RETURN_BUTTON_SLOT, guiComponent.createReturnButton(), event -> returnAction(event));
        } else {
            setItem(RETURN_BUTTON_SLOT, fillerItem);
        }
        
        // Next page button
        if (hasNextPage()) {
            setItem(NEXT_PAGE_SLOT, guiComponent.createNextPageButton(), event -> nextPageAction(event));
        } else {
            setItem(NEXT_PAGE_SLOT, fillerItem);
        }
    }
    
    private boolean hasPreviousPage() {
        return pageOffset > 0;
    }
    
    private boolean hasNextPage() {
        return (pageOffset + 1) * MAX_BOOKMARKS < bookmarks.size();
    }

    private boolean hasReturnReader() {
        return returnToReader != null;
    }
    
    private void previousPageAction(InventoryClickEvent event) {
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        pageOffset = Math.max(0, pageOffset - 1);
        render();
    }
    
    private void nextPageAction(InventoryClickEvent event) {
        event.getWhoClicked().playSound(UI_CLICK_SOUND);
        int maxPage = (bookmarks.size() - 1) / MAX_BOOKMARKS;
        pageOffset = Math.min(maxPage, pageOffset + 1);
        render();
    }
    
    private void returnAction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.playSound(UI_CLICK_SOUND);
        if (returnToReader != null) {
            new RecipeGui(services, playerData, returnToReader).open(player);
        }
    }

    private void startTicker() {
        if (tickTask != null) {
            tickTask.cancel();
        }

        tickTask = Bukkit.getScheduler().runTaskTimer(
            VanillaEnoughItems.getPlugin(),
            this::tickSymbols,
            TICK_INTERVAL,
            TICK_INTERVAL
        );
    }

    private void tickSymbols() {
        slotToBookmark.entrySet().forEach(entry -> {
            Bookmark bookmark = entry.getValue();
            CyclicIngredient cyclic = bookmark.getSymbol();
            int slot = entry.getKey();

            // Skip if only one option
            if (!cyclic.hasMultipleOptions()) {
                return;
            }

            cyclic.tickWithDependencies();
            ItemStack currentItem = cyclic.getCurrentItem();
            
            setItem(slot, currentItem, event -> clickAction(event, bookmark));
        });
    }

    private static Component title(Component titleComponent, Style style) {
        if (style.hasResourcePack()) {
            return VeiPack.Font.Gui.BLANK_54.iconComponent().append(titleComponent);
        } else {
            return titleComponent;
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
    }
}
