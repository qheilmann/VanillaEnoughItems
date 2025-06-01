package me.qheilmann.vei.Core.PlayerInventory.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import me.qheilmann.vei.Menu.MenuManager;

public class PlayerInventoryListener implements Listener
{

    private MenuManager menuManager;
    private RecipeIndexService recipeIndexService;
    private Plugin plugin;

    private Map<UUID, TimedItemEntry> hotbar1InteractionMap = new HashMap<>();
    private Map<UUID, TimedItemEntry> hotbar2InteractionMap = new HashMap<>();

    public PlayerInventoryListener(RecipeIndexService recipeIndex, MenuManager menuManager, Plugin plugin) {
        this.recipeIndexService = recipeIndex;
        this.menuManager = menuManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInventoryClick(final InventoryClickEvent event) {

        // Should we restrict this to only the player inventory, let's sat every inventory
        // Inventory clickedInventory = event.getClickedInventory();
        // boolean isPlayerInventory = (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER);
        // if (!isPlayerInventory) {
        //     return;
        // }

        int hotbarButtonSlot = event.getHotbarButton();
        boolean isHotbar = hotbarButtonSlot != -1;
        if (!isHotbar) {
            return;
        }

        UUID playerUUID = event.getWhoClicked().getUniqueId();
        TimedItemEntry newEntry = new TimedItemEntry(LocalDateTime.now(), event.getCurrentItem());

        // First hotbar slot
        if (hotbarButtonSlot == 0) {
            ItemStack oldItem = hotbar1InteractionMap.containsKey(playerUUID) ? hotbar1InteractionMap.get(playerUUID).getItemStack() : null;
            if (addAndcheckDoubleHotbarEntry(playerUUID, newEntry, hotbar1InteractionMap)) {
                firstHotbarSlotAction(event, oldItem);
            }
        }
        // Second hotbar slot
        else if (hotbarButtonSlot == 1) {
            ItemStack oldItem = hotbar2InteractionMap.containsKey(playerUUID) ? hotbar2InteractionMap.get(playerUUID).getItemStack() : null;
            if (addAndcheckDoubleHotbarEntry(playerUUID, newEntry, hotbar2InteractionMap)) {
                secondHotbarSlotAction(event, oldItem);
            }
        }
    }

    public boolean addAndcheckDoubleHotbarEntry(UUID playerUUID, TimedItemEntry entry, final Map<UUID, TimedItemEntry> history) {

        // new entry
        if (!history.containsKey(playerUUID))
        {
            history.put(playerUUID, entry);
            return false;
        }

        // check double entry
        TimedItemEntry oldEntry = history.get(playerUUID);
        history.put(playerUUID, entry);

        Duration maxDuration = Duration.ofMillis(500);
        Duration timeDiff = java.time.Duration.between(oldEntry.getTimestamp(), entry.getTimestamp());

        if (timeDiff.compareTo(maxDuration) <= 0) {
            // to avoid triple entries we remove the entry
            history.remove(playerUUID);
            return true;
        } else {
            return false;
        }
    }

    public void firstHotbarSlotAction(final InventoryClickEvent event, ItemStack withItem) {
        if (withItem == null || withItem.getType().isAir()) {
            return;
        }

        MixedProcessRecipeReader recipeReader = recipeIndexService.getByResult(withItem);

        if (recipeReader == null) {
            return; // No recipe found for the item
        }

        if (! (event.getWhoClicked() instanceof Player player)) {
            return; // Not a player, do nothing
        }

        Bukkit.getScheduler().runTask(plugin, () -> menuManager.openRecipeMenu(player, recipeReader));
    }

    public void secondHotbarSlotAction(final InventoryClickEvent event, ItemStack withItem) {
        if (withItem == null || withItem.getType().isAir()) {
            return;
        }

        MixedProcessRecipeReader recipeReader = recipeIndexService.getByIngredient(withItem);

        if (recipeReader == null) {
            return; // No recipe found for the item
        }

        if (! (event.getWhoClicked() instanceof Player player)) {
            return; // Not a player, do nothing
        }

        Bukkit.getScheduler().runTask(plugin, () -> menuManager.openRecipeMenu(player, recipeReader));
    }

    class TimedItemEntry {
        private final LocalDateTime timestamp;
        private final ItemStack itemStack;

        public TimedItemEntry(LocalDateTime timestamp, ItemStack itemStack) {
            this.timestamp = timestamp;
            this.itemStack = itemStack;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }
}

// Witch item trigger the event?
// Before   | after 1Swap   | after 2Swap   | Comment                  | triger on item
// item1    | item2         | item1         | Double slot              | item1
// item     | nothing       | item          | Single slot              | item
// nothing  | item1         | nothing       | Single slot (start void) | nothing
// nothing  | nothing       | nothing       | Nothing                  | nothing
// item     | item          | item          | prevent swap             | item
// Conclusion: always trigger before the first swap (which is equivalent to after the second swap)
