package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class BlastingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("blasting");

    public BlastingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return recipe instanceof BlastingRecipe;
    }

    @Override
    public ItemStack symbol() {
        ItemStack item = ItemType.BLAST_FURNACE.createItemStack(meta -> {
            meta.displayName(Component.text("Blasting", VanillaEnoughItems.config().style().colorPrimary()).decoration(TextDecoration.ITALIC, false));
        });

        return item;
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench blastFurnace = new Workbench(new ItemStack(Material.BLAST_FURNACE));
        return Set.of(blastFurnace);
    }
}
