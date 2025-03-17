package me.qheilmann.vei.Core.Process;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import me.qheilmann.vei.Core.ProcessPanel.Panels.CraftingProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.DummyProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.SmeltingProcessPanel;
import net.kyori.adventure.text.Component;

public class VanillaProcesses {
    
    /**
     * A dummy process that is used when no other process is found, it the default process.
     */
    public static Process<?> DUMMY_PROCESS = DummyProcessHelper.getDummyProcess();
    public static String DUMMY_PROCESS_NAME = DummyProcessHelper.PROCESS_NAME;
    public static String CRAFTING_PROCESS_NAME = CraftingProcessHelper.PROCESS_NAME;


    public static LinkedHashSet<Process<?>> getAllVanillaProcesses() {
        
        LinkedHashSet<Process<?>> processes = new LinkedHashSet<>();
        processes.add(CraftingProcessHelper.getCraftingProcess());
        processes.add(SmeltingProcessHelper.getSmeltingProcess());

        // TODO add the following processes
        // BlastingRecipe
        // SmokingRecipe
        // CampfireRecipe
        // SmithingTransformRecipe
        // SmithingTrimRecipe
        // SmithingRecipe
        // StonecuttingRecipe
        
        return processes;
    }

    private static class CraftingProcessHelper {

        private static final String PROCESS_NAME = "Crafting";

        private static Process<CraftingRecipe> getCraftingProcess() {
            return new Process<CraftingRecipe>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (recipeSet, variant) -> new CraftingProcessPanel(recipeSet, variant)
            );
        }

        private static ItemStack generateIcon() {
            ItemStack icon = new ItemStack(Material.CRAFTING_TABLE);
            icon.editMeta(meta -> {
                meta.displayName(Component.text(PROCESS_NAME));
                meta.setMaxStackSize(1);
            });
            return icon;
        }

        private static Collection<ItemStack> getWorkbenchOptions() {
            return Arrays.asList(
                new ItemStack(Material.CRAFTING_TABLE),
                new ItemStack(Material.CRAFTER)
            );
        }
        
        private static Collection<Class<? extends CraftingRecipe>> getRecipeClasses() {
            // TODO can be replace with just CraftingRecipe.class
            return Arrays.asList(
                ShapedRecipe.class,
                ShapelessRecipe.class
            );
        }
    }

    private static class SmeltingProcessHelper {
        private static final String PROCESS_NAME = "Smelting";

        private static Process<FurnaceRecipe> getSmeltingProcess() {
            return new Process<>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (recipeSet, variant) -> new SmeltingProcessPanel(recipeSet, variant)
            );
        }

        private static ItemStack generateIcon() {
            ItemStack icon =
                new ItemStack(Material.FURNACE);
            icon.editMeta(meta -> {
                meta.displayName(Component.text(PROCESS_NAME));
                meta.setMaxStackSize(1);
            });
            return icon;
        }

        private static Collection<ItemStack> getWorkbenchOptions() {
            return Arrays.asList(
                new ItemStack(Material.FURNACE)
            );
        }

        private static Collection<Class<? extends FurnaceRecipe>> getRecipeClasses() {
            return Arrays.asList(
                FurnaceRecipe.class
            );
        }
    }

    private static class DummyProcessHelper {
        private static final String PROCESS_NAME = "Undefined";

        private static Process<Recipe> getDummyProcess() {
            return new Process<>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (recipeSet, variant) -> new DummyProcessPanel(recipeSet, variant)
            );
        }

        private static ItemStack generateIcon() {
            ItemStack icon = new ItemStack(Material.BARRIER);
            icon.editMeta(meta -> {
                meta.displayName(Component.text(PROCESS_NAME));
                meta.setMaxStackSize(1);
            });
            return icon;
        }

        private static Collection<ItemStack> getWorkbenchOptions() {
            return Arrays.asList(); // empty list for the dummy process
        }

        private static Collection<Class<? extends Recipe>> getRecipeClasses() {
            return Arrays.asList(
                Recipe.class
            );
        }
    }
}
