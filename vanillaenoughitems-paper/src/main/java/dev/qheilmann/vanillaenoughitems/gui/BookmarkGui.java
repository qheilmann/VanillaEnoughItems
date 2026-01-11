package dev.qheilmann.vanillaenoughitems.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.bookmark.Bookmark;
import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.helper.GuiComponent;
import dev.qheilmann.vanillaenoughitems.recipe.RecipeContext;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.pack.GuiIcon;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInv;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.Slots;
import net.kyori.adventure.text.Component;

/**
 * Unified GUI for displaying bookmarks (both player and server bookmarks).
 * Shows bookmarks with cycling symbols and allows clicking to open recipes.
 */
@NullMarked
public class BookmarkGui extends FastInv {

    private static final int SIZE = Slots.Generic9x6.SIZE;
    private static final int TICK_INTERVAL = 20; // ticks (1 second)
    private static final int MAX_BOOKMARKS = 45; // 5 rows

    private final RecipeContext context;
    private final Style style;
    private final ItemStack fillerItem;
    private final Collection<Bookmark> bookmarks;
    private final Map<Integer, Bookmark> slotToBookmark = new HashMap<>();
    private @Nullable BukkitTask tickTask;

    /**
     * Create a bookmark GUI with title component (icon will be appended automatically)
     * @param titleComponent the title component (without icon)
     * @param context the recipe context
     * @param bookmarks the bookmarks to display
     */
    public BookmarkGui(Component titleComponent, RecipeContext context, Collection<Bookmark> bookmarks) {
        super(SIZE, createTitle(titleComponent));
        this.context = context;
        this.style = VanillaEnoughItems.config().style();
        this.fillerItem = GuiComponent.createFillerItem(style.hasResourcePack());
        this.bookmarks = bookmarks;

        // Fill with filler items
        setItems(Slots.Generic9x6.all(), fillerItem);
        
        render();
    }

    private void render() {
        slotToBookmark.clear();

        int slot = 0;
        for (Bookmark bookmark : bookmarks) {
            if (slot >= MAX_BOOKMARKS) {
                break;
            }
            renderBookmark(slot, bookmark);
            slot++;
        }

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
        new RecipeGui(player, context, reader).open(player);
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

    /**
     * Create a title for bookmark GUI with resource pack icon.
     * Always appends the BLANK_54 icon to the provided title component.
     * @param titleText the title component to display after the icon
     * @return the formatted title component with icon
     */
    private static Component createTitle(Component titleText) {
        GuiIcon guiIcon = VeiPack.Font.Gui.BLANK_54;
        return guiIcon.iconComponent().append(Component.text(guiIcon.resetSpace())).append(titleText);
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
    }
}
