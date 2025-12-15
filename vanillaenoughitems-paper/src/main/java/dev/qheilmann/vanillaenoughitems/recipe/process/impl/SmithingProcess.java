package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NullMarked
public class SmithingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("smithing");

    public SmithingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        List<Class<? extends Recipe>> valideClass = Arrays.asList(
            SmithingTransformRecipe.class,
            SmithingTrimRecipe.class
        );

        return valideClass.stream().anyMatch(c -> c.isInstance(recipe));
    }

    @Override
    public Component displayName() {
        return Component.text("Smithing");
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
