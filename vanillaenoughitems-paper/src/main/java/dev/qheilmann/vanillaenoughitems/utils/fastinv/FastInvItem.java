package dev.qheilmann.vanillaenoughitems.utils.fastinv;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record FastInvItem(ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> clickAction) {}