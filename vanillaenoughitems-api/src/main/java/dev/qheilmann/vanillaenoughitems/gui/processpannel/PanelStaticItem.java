package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a static item in a ProcessPanel with an optional click action.
 * This is the API-level type for panel static items, used by ProcessPanel implementations.
 * 
 * @param itemStack the item to display (should be a single item, i.e. amount of 1)
 * @param clickAction optional action to perform when the item is clicked in the GUI
 */
@NullMarked
public record PanelStaticItem(ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> clickAction) {}
