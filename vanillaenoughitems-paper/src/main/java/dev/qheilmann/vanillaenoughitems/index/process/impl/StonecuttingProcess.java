package dev.qheilmann.vanillaenoughitems.index.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.index.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.index.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NullMarked
public class StonecuttingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("stonecutting");

    public StonecuttingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        List<Class<? extends Recipe>> valideClass = Arrays.asList(
            StonecuttingRecipe.class
        );

        return valideClass.stream().anyMatch(c -> c.isInstance(recipe));
    }

    @Override
    public Component displayName() {
        return Component.text("Stonecutting");
    }

    @Override
    public ItemStack symbol() {
        return new ItemStack(Material.STONECUTTER);
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench stonecutter = new Workbench(new ItemStack(Material.STONECUTTER));
        return Set.of(stonecutter);
    }
}
