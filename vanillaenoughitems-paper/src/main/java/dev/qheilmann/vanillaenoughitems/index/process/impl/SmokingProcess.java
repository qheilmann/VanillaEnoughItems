package dev.qheilmann.vanillaenoughitems.index.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmokingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.index.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.index.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NullMarked
public class SmokingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("smoking");

    public SmokingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        List<Class<? extends Recipe>> valideClass = Arrays.asList(
            SmokingRecipe.class
        );

        return valideClass.stream().anyMatch(c -> c.isInstance(recipe));
    }

    @Override
    public Component displayName() {
        return Component.text("Smoking");
    }

    @Override
    public ItemStack symbol() {
        return new ItemStack(Material.SMOKER);
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench smoker = new Workbench(new ItemStack(Material.SMOKER));
        return Set.of(smoker);
    }
}
