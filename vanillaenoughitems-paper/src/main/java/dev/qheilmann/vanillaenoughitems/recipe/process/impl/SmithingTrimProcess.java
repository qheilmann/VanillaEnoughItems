package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NullMarked
public class SmithingTrimProcess extends AbstractProcess {

    public static final Key KEY = Key.key("smithing_trim");

    public SmithingTrimProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return recipe instanceof SmithingTrimRecipe;
    }

    @Override
    public Component displayName() {
        return Component.text("Smithing Trim");
    }

    @Override
    public ItemStack symbol() {
        return new ItemStack(Material.SMITHING_TABLE);
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench smithingTable = new Workbench(new ItemStack(Material.SMITHING_TABLE));
        return Set.of(smithingTable);
    }
}
