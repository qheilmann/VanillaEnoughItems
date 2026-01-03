package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class SmithingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("smithing");
    public static final List<Class<? extends Recipe>> VALID_RECIPE_CLASSES = Arrays.asList(
        SmithingTrimRecipe.class, 
        SmithingTransformRecipe.class
    );

    public SmithingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return VALID_RECIPE_CLASSES.stream().anyMatch(c -> c.isInstance(recipe));
    }

    @Override
    public ItemStack symbol() {
        ItemStack item = ItemType.SMITHING_TABLE.createItemStack(meta -> {
            meta.displayName(Component.text("Smithing", VanillaEnoughItems.config().style().colorPrimary()).decoration(TextDecoration.ITALIC, false));
        });

        return item;
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench smithingTable = new Workbench(new ItemStack(Material.SMITHING_TABLE));
        return Set.of(smithingTable);
    }
}
