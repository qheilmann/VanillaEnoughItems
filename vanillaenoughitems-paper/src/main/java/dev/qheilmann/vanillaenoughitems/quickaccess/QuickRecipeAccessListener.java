package dev.qheilmann.vanillaenoughitems.quickaccess;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.RecipeServices;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerDataManager;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGui;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;

@NullMarked
public class QuickRecipeAccessListener implements Listener {

    private static final Duration WINDOW_DURATION = Duration.ofMillis(500);
    private static final int OUTSIDE_HOTBAR_SLOT = -1;
    private static final int HOTBAR_SLOT_1 = 0;
    private static final int HOTBAR_SLOT_2 = 1;

    // Maps to track the last interaction times for hotbar slots
    private final Map<UUID, TimedItemEntry> hotbar1InteractionMap = new HashMap<>();
    private final Map<UUID, TimedItemEntry> hotbar2InteractionMap = new HashMap<>();

    private final JavaPlugin plugin;
    private final RecipeServices recipeServices;
    private final PlayerDataManager playerDataManager;

    public QuickRecipeAccessListener(JavaPlugin plugin, RecipeServices services, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.recipeServices = services;
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onPlayerInventoryChange(final InventoryClickEvent event) {

        int hotbarSlot = event.getHotbarButton();
        if(hotbarSlot == OUTSIDE_HOTBAR_SLOT) {
            return;
            // Note: unfortunately, there is no way to detect hotbar changes when player is inside the creative inventory
        }

        Player player = (Player) event.getWhoClicked();
        TimedItemEntry newEntry = new TimedItemEntry(LocalDateTime.now(), event.getCurrentItem());

        // First hotbar slot
        if (hotbarSlot == HOTBAR_SLOT_1) {
            firstHotbarCheck(player, newEntry);
        }

        // Second hotbar slot
        if (hotbarSlot == HOTBAR_SLOT_2) {
            secondHotbarCheck(player, newEntry);
        }
    }

    private void firstHotbarCheck(Player player, TimedItemEntry newEntry) {
        UUID playerUUID = player.getUniqueId();
        TimedItemEntry oldEntry = hotbar1InteractionMap.put(playerUUID, newEntry);
        if (!isDoublePress(oldEntry, newEntry)) {
            return;
        }

        // Double press detected
        hotbar1InteractionMap.remove(playerUUID); // avoid triple press
        firstHotbarAction(player, oldEntry.itemStack());
    }

    private void secondHotbarCheck(Player player, TimedItemEntry newEntry) {
        UUID playerUUID = player.getUniqueId();
        TimedItemEntry oldEntry = hotbar2InteractionMap.put(playerUUID, newEntry);
        if (!isDoublePress(oldEntry, newEntry)) {
            return;
        }

        // Double press detected
        hotbar2InteractionMap.remove(playerUUID); // avoid triple press
        secondHotbarAction(player, oldEntry.itemStack());
    }

    private boolean isDoublePress(@Nullable TimedItemEntry firstEntry, @Nullable TimedItemEntry secondEntry) {
        if (firstEntry == null || secondEntry == null) {
            return false;
        }

        Duration timeDifference = Duration.between(firstEntry.timestamp(), secondEntry.timestamp());
        return timeDifference.compareTo(WINDOW_DURATION) <= 0;
    }

    private void firstHotbarAction(Player player, @Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }

        MultiProcessRecipeReader reader = recipeServices.recipeIndex().readerByResult(itemStack);
        if (reader == null) {
            return;
        }
        createAndOpenGui(player, reader);
    }

    private void secondHotbarAction(Player player, @Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }

        MultiProcessRecipeReader reader = recipeServices.recipeIndex().readerByIngredient(itemStack);
        if (reader == null) {
            return;
        }
        createAndOpenGui(player, reader);
    }

    /**
     * Create, render and open the recipe GUI for the player.
     *
     * @param player  the player to open the GUI for
     * @param reader  the MultiProcessRecipeReader containing the recipes to display
     */
    private void createAndOpenGui(Player player, MultiProcessRecipeReader reader) {
        RecipeGui gui = new RecipeGui(recipeServices, playerDataManager.getPlayerData(player.getUniqueId()), reader);
        gui.render();

        // Open the GUI on the next tick to avoid cancellation issues
        plugin.getServer().getScheduler().runTask(plugin, () -> gui.open(player));
    }
}

// Witch item open?
// Before   | after 1Swap   | after 2Swap   | Comment                   | triger on item
// item1    | item2         | item1         | Double item               | item1
// item     | nothing       | item          | Single item (second void) | item
// nothing  | item1         | nothing       | Single slot (start void)  | nothing
// nothing  | nothing       | nothing       | Nothing                   | nothing
// item     | item          | item          | prevent swap (same item)  | item
// Conclusion: always trigger before the first swap (which is equivalent to after the second swap)
