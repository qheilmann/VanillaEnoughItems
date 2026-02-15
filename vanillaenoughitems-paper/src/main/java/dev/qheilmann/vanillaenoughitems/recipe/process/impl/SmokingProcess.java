package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmokingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class SmokingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("smoking");

    public SmokingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return recipe instanceof SmokingRecipe;
    }

    @Override
    public ItemStack symbol() {
        ItemStack item = ItemType.SMOKER.createItemStack(meta -> {
            meta.displayName(Component.text("Smoking", VanillaEnoughItems.config().style().colorPrimary()).decoration(TextDecoration.ITALIC, false));
        });

        return item;
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench smoker = new Workbench(new ItemStack(Material.SMOKER));
        return Set.of(smoker);
    }
}
