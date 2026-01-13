package dev.qheilmann.vanillaenoughitems.quickaccess;

import java.time.LocalDateTime;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record TimedItemEntry(LocalDateTime timestamp, @Nullable ItemStack itemStack) {
}
