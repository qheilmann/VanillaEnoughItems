package dev.qheilmann.vanillaenoughitems.index.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.index.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.index.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NullMarked
public class CampfireProcess extends AbstractProcess {

    public static final Key KEY = Key.key("campfire_cooking");

    public CampfireProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        List<Class<? extends Recipe>> valideClass = Arrays.asList(
            CampfireRecipe.class
        );

        return valideClass.stream().anyMatch(c -> c.isInstance(recipe));
    }

    @Override
    public Component displayName() {
        return Component.text("Campfire Cooking");
    }

    @Override
    public ItemStack symbol() {
        return new ItemStack(Material.CAMPFIRE);
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench campfire = new Workbench(new ItemStack(Material.CAMPFIRE));
        return Set.of(campfire);
    }
}
