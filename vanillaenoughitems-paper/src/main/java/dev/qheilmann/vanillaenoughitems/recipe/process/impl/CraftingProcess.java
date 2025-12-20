package dev.qheilmann.vanillaenoughitems.recipe.process.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NullMarked
public class CraftingProcess extends AbstractProcess {

    public static final Key KEY = Key.key("crafting");
    public static List<Class<? extends Recipe>> validRecipeClasses =  Arrays.asList(ShapedRecipe.class, ShapelessRecipe.class, TransmuteRecipe.class);

    public static boolean canHandle(Recipe recipe) {
        return validRecipeClasses.stream().anyMatch(c -> c.isInstance(recipe));
    }

    public CraftingProcess() {
        super(KEY);
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return canHandle(recipe);
    }

    @Override
    public Component displayName() {
        // [Translation] support (GlobalTranslator)
        // https://docs.papermc.io/paper/dev/component-api/i18n/#globaltranslator
        // return Component.translatable("process.vanillaenoughitems.crafting"); // or vanillaenoughitems.process.crafting // but other plugins mayb add process
        // hmm ItemStack can't use GlobalTranslator directly
        // same on each process and workbench display name
        return Component.text("Crafting");
    }

    @Override
    public ItemStack symbol() {
        return new ItemStack(Material.CRAFTING_TABLE);
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench craftingTable = new Workbench(new ItemStack(Material.CRAFTING_TABLE));
        Workbench crafter = new Workbench(new ItemStack(Material.CRAFTER));

        return Set.of(craftingTable, crafter);
    }
}
