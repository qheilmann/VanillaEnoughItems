package dev.qheilmann.vanillaenoughitems.recipe.process;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/** 
 * A workbench is a block, system, or other element that allows to perform process.
 * For example, a crafting table handle a crafting process to be managed, but a crafter or other elements can also do this, each of which is a workbench.
 * 
 * @param symbol the item that represents the workbench in the GUI (should be a single item, i.e. amount of 1)
*/
@NullMarked
public record Workbench(ItemStack symbol) {}
