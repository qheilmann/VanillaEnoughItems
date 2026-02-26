package dev.qheilmann.vanillaenoughitems.playground.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Demonstrates opening VEI GUIs from player interactions on a crafting table.
 * <p>
 * All interactions require <b>Shift (sneaking)</b> + clicking a <b>Crafting Table</b>:
 * <ul>
 *   <li><b>Shift + Left-Click</b> — recipe lookup for the held item</li>
 *   <li><b>Shift + Right-Click</b> — usage lookup for the held item</li>
 *   <li><b>Shift + Left-Click with a Book</b> — player bookmarks</li>
 *   <li><b>Shift + Right-Click with a Book</b> — server bookmarks</li>
 * </ul>
 */
public class RecipeLookupListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.CRAFTING_TABLE) return;

        // Prevent the vanilla crafting table GUI from opening
        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack hand = event.getItem();
        boolean isLeftClick = event.getAction().isLeftClick();

        if (hand != null && hand.getType() == Material.BOOK) {
            openBookmarks(player, isLeftClick);
        } else {
            openRecipeLookup(player, hand, isLeftClick);
        }
    }

    private void openBookmarks(Player player, boolean isLeftClick) {
        VanillaEnoughItemsAPI api = VanillaEnoughItemsAPI.get();

        if (isLeftClick) {
            api.openPlayerBookmarkGui(player);   // Personal bookmarks
        } else {
            api.openServerBookmarkGui(player);   // Server-wide bookmarks
        }
    }

    private void openRecipeLookup(Player player, ItemStack hand, boolean isLeftClick) {
        if (hand == null || hand.isEmpty()) {
            player.sendMessage(Component.text("Hold an item to look up recipes or usages!", NamedTextColor.YELLOW));
            return;
        }

        VanillaEnoughItemsAPI api = VanillaEnoughItemsAPI.get();

        // openRecipeGui / openUsageGui return false when no results are found
        if (isLeftClick) {
            if (!api.openRecipeGui(player, hand)) {
                player.sendMessage(Component.text("No recipes found for this item.", NamedTextColor.RED));
            }
        } else {
            if (!api.openUsageGui(player, hand)) {
                player.sendMessage(Component.text("No usages found for this item.", NamedTextColor.RED));
            }
        }
    }
}
