package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NullMarked
public class SmeltingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("smelting");

    public SmeltingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        List<Class<? extends Recipe>> valideClass = Arrays.asList(
            FurnaceRecipe.class // Blast Furnace and Smoker recipes are instances of FurnaceRecipe
        );

        return valideClass.stream().anyMatch(c -> c.isInstance(recipe));
    }

    @Override
    public Component displayName() {
        return Component.text("Smelting");
    }

    @Override
    public ItemStack symbol() {
        return new ItemStack(Material.FURNACE);
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench furnace = new Workbench(new ItemStack(Material.FURNACE));
        // All furnace recipe can't be handled in blast furnace or smoker, there is other process for this
        return Set.of(furnace);
    }
}
