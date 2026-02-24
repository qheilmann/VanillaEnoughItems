package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class StonecuttingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("stonecutting");

    public StonecuttingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return recipe instanceof StonecuttingRecipe;
    }

    @Override
    public ItemStack symbol() {
        ItemStack item = ItemType.STONECUTTER.createItemStack(meta -> {
            meta.displayName(Component.text("Stonecutting", VanillaEnoughItems.veiConfig().style().colorPrimary()).decoration(TextDecoration.ITALIC, false));
        });

        return item;
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench stonecutter = new Workbench(new ItemStack(Material.STONECUTTER));
        return Set.of(stonecutter);
    }
}
