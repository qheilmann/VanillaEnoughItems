package dev.qheilmann.vanillaenoughitems.index.process;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/** 
 * A workbench is a block, system, or other element that allows to perform process.
 * For example, a crafting table handle a crafting process to be managed, but a crafter or other elements can also do this, each of which is a workbench.
*/
@NullMarked
public record Workbench(ItemStack symbol) {}
