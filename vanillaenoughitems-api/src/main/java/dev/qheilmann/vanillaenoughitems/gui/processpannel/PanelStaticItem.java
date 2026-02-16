package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a static item in a ProcessPanel with an optional click action.
 * This is the API-level type for panel static items, used by ProcessPanel implementations.
 */
@NullMarked
public record PanelStaticItem(ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> clickAction) {}
