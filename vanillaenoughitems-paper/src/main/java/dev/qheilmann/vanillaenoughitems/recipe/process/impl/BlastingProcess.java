package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

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
    public Component displayName() {
        return Component.text("Blasting");
    }

    @Override
    public ItemStack symbol() {
        return new ItemStack(Material.BLAST_FURNACE);
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench blastFurnace = new Workbench(new ItemStack(Material.BLAST_FURNACE));
        return Set.of(blastFurnace);
    }
}
