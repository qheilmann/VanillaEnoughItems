package me.qheilmann.vei.Core.Process;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import me.qheilmann.vei.Core.ProcessPanel.Panels.BlastingProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.CampfireProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.CraftingProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.DummyProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.SmeltingProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.SmithingProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.SmokingProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.StonecuttingProcessPanel;
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
        // We place it in optimized order to gain small performance when indexing the recipes
        processes.add(CraftingProcessHelper.getCraftingProcess());
        processes.add(SmeltingProcessHelper.getSmeltingProcess());
        processes.add(BlastingProcessHelper.getBlastingProcess());
        processes.add(SmokingProcessHelper.getSmokingProcess());
        processes.add(CampfireProcessHelper.getCampfireProcess());
        processes.add(StonecuttingProcessHelper.getStonecuttingProcess());
        processes.add(SmithingProcessHelper.getSmithingProcess());
        
        return processes;
    }

    private static class BlastingProcessHelper {
        private static final String PROCESS_NAME = "Blasting";

        private static Process<BlastingRecipe> getBlastingProcess() {
            return new Process<>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (style, recipeIndex, recipeReader) -> new BlastingProcessPanel(style, recipeIndex, recipeReader)
            );
        }

        private static ItemStack generateIcon() {
            ItemStack icon =
                new ItemStack(Material.BLAST_FURNACE);
            icon.editMeta(meta -> {
                meta.displayName(Component.text(PROCESS_NAME));
                meta.setMaxStackSize(1);
            });
            return icon;
        }

        private static Collection<ItemStack> getWorkbenchOptions() {
            return Arrays.asList(
                new ItemStack(Material.BLAST_FURNACE)
            );
        }

        private static Collection<Class<? extends BlastingRecipe>> getRecipeClasses() {
            return Arrays.asList(
                BlastingRecipe.class
            );
        }
    }

    private static class CampfireProcessHelper {
        private static final String PROCESS_NAME = "Campfire cooking";

        private static Process<CampfireRecipe> getCampfireProcess() {
            return new Process<>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (style, recipeIndex, recipeReader) -> new CampfireProcessPanel(style, recipeIndex, recipeReader)
            );
        }

        private static ItemStack generateIcon() {
            ItemStack icon =
                new ItemStack(Material.CAMPFIRE);
            icon.editMeta(meta -> {
                meta.displayName(Component.text(PROCESS_NAME));
                meta.setMaxStackSize(1);
            });
            return icon;
        }

        private static Collection<ItemStack> getWorkbenchOptions() {
            return Arrays.asList(
                new ItemStack(Material.CAMPFIRE),
                new ItemStack(Material.SOUL_CAMPFIRE)
            );
        }

        private static Collection<Class<? extends CampfireRecipe>> getRecipeClasses() {
            return Arrays.asList(
                CampfireRecipe.class
            );
        }
    }

    private static class CraftingProcessHelper {

        private static final String PROCESS_NAME = "Crafting";

        private static Process<CraftingRecipe> getCraftingProcess() {
            return new Process<CraftingRecipe>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (style, recipeIndex, recipeReader) -> new CraftingProcessPanel(style, recipeIndex, recipeReader)
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
                (style, recipeIndex, recipeReader) -> new SmeltingProcessPanel(style, recipeIndex, recipeReader)
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

    private static class SmithingProcessHelper {
        private static final String PROCESS_NAME = "Smithing Table";

        private static Process<SmithingRecipe> getSmithingProcess() {
            return new Process<>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (style, recipeIndex, recipeReader) -> new SmithingProcessPanel(style, recipeIndex, recipeReader)
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

        private static Collection<Class<? extends SmithingRecipe>> getRecipeClasses() {
            return Arrays.asList(
                SmithingTransformRecipe.class,
                SmithingTrimRecipe.class
            );
        }
    }

    private static class SmokingProcessHelper {
        private static final String PROCESS_NAME = "Smoking";

        private static Process<SmokingRecipe> getSmokingProcess() {
            return new Process<>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (style, recipeIndex, recipeReader) -> new SmokingProcessPanel(style, recipeIndex, recipeReader)
            );
        }

        private static ItemStack generateIcon() {
            ItemStack icon =
                new ItemStack(Material.SMOKER);
            icon.editMeta(meta -> {
                meta.displayName(Component.text(PROCESS_NAME));
                meta.setMaxStackSize(1);
            });
            return icon;
        }

        private static Collection<ItemStack> getWorkbenchOptions() {
            return Arrays.asList(
                new ItemStack(Material.SMOKER)
            );
        }

        private static Collection<Class<? extends SmokingRecipe>> getRecipeClasses() {
            return Arrays.asList(
                SmokingRecipe.class
            );
        }
    }

    private static class StonecuttingProcessHelper {
        private static final String PROCESS_NAME = "Stonecutting";

        private static Process<StonecuttingRecipe> getStonecuttingProcess() {
            return new Process<>(
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (style, recipeIndex, recipeReader) -> new StonecuttingProcessPanel(style, recipeIndex, recipeReader)
            );
        }

        private static ItemStack generateIcon() {
            ItemStack icon =
                new ItemStack(Material.STONECUTTER);
            icon.editMeta(meta -> {
                meta.displayName(Component.text(PROCESS_NAME));
                meta.setMaxStackSize(1);
            });
            return icon;
        }

        private static Collection<ItemStack> getWorkbenchOptions() {
            return Arrays.asList(
                new ItemStack(Material.STONECUTTER)
            );
        }

        private static Collection<Class<? extends StonecuttingRecipe>> getRecipeClasses() {
            return Arrays.asList(
                StonecuttingRecipe.class
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
                (style, recipeIndex, recipeReader) -> new DummyProcessPanel(style, recipeIndex, recipeReader)
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
