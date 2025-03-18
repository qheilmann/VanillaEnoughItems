package me.qheilmann.vei.Core.Process;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nullable;

import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
import me.qheilmann.vei.Core.Style.Styles.Style;
import me.qheilmann.vei.Core.Utils.NotNullSet;

/**
 * Abstract class for item creation using recipes in a certain way 
 * (e.g., Crafting, Smelting, Smithing). This way is represented by a process.
 * <ul>
 * <li>A process can have multiple recipe types (e.g., ShapedRecipe,
 * ShapelessRecipe for Crafting or SmithingTrimRecipe, SmithingTransformRecipe
 * for Smithing).</li>
 * <li>It can be used by various workbenches (e.g., Crafting table /
 * Crafter for Crafting).</li>
 * <li>It can be represented by a block, item, or entity.</li>
 * </ul>
 */
public class Process<R extends Recipe> {

    private static final NotNullSet<Process<?>> processRegistry = new NotNullSet<>(new LinkedHashSet<>());

    private final String processName;
    private final ItemStack processIcon;
    private final NotNullSet<ItemStack> workbenchOptions;
    private final NotNullSet<Class<? extends R>> recipeClasses;
    private final TriFunction<Style, ProcessRecipeSet<R>, Integer, ProcessPanel<R>> processPanelSupplier;
    
    public static void registerProcesse(Process<?> process) {
        processRegistry.add(process);
    }

    public static void registerProcesses(Collection<Process<?>> processes) {
        processRegistry.addAll(processes);
    }

    /**
     * Get a process by its process name (e.g., Crafting, Smelting, Smithing).
     *
     * @return the process or null if the process does not exist or was not implemented.
     */
    public static @Nullable Process<?> getProcessByName(@NotNull String processName) {
        for (Process<?> process : processRegistry) {
            if (process.getProcessName().toLowerCase().equals(processName)) {
                return process;
            }
        }

        return null;
    }

    public static @NotNull Process<?> getProcesseByRecipe(Recipe recipe) {
        for (Process<?> process : processRegistry) {
            for (Class<? extends Recipe> recipeClass : process.getRecipeClasses()) {
                if (recipeClass.isAssignableFrom(recipe.getClass())) {
                    return process;
                }
            }
        }

        return VanillaProcesses.DUMMY_PROCESS;
    }

    public Process(@NotNull String processName,
                    @NotNull ItemStack processIcon,
                    @NotNull Collection<ItemStack> workbenchOptions,
                    @NotNull Collection<Class<? extends R>> recipeClasses,
                    @NotNull TriFunction<Style, ProcessRecipeSet<R>, Integer, ProcessPanel<R>> processPanelSupplier ) {
        this.processName = processName;
        this.processIcon = processIcon;
        this.recipeClasses = new NotNullSet<>(new LinkedHashSet<>(), recipeClasses);
        this.workbenchOptions = new NotNullSet<>(new LinkedHashSet<>(), workbenchOptions);
        this.processPanelSupplier = processPanelSupplier;
    }

    /**
     * Gets the process name (e.g., Crafting, Smelting, Smithing).
     * 
     * @return the process name.
     */
    @NotNull
    public String getProcessName() {
        return processName;
    }

    /**
     * Gets the item stack that represents the process.
     * 
     * @return the item stack.
     */
    @NotNull
    public ItemStack getProcessIcon() {
        return processIcon;
    }

    /**
     * Gets the recipe panel for the process.
     * 
     * @return the recipe panel.
     */
    @NotNull
    public ProcessPanel<R> generateProcessPanel(@NotNull Style style, @NotNull ProcessRecipeSet<R> processRecipeSet, int variant) {
        return processPanelSupplier.apply(style, processRecipeSet, variant);
    }

    /**
     * Gets the set of different recipe classes that are made inside the same process 
     * (e.g., ShapedRecipe.class, ShapelessRecipe.class, SmithingTrimRecipe.class, SmithingTransformRecipe.class).
     * 
     * @return the set of recipe classes.
     */
    @NotNull
    public Set<Class<? extends R>> getRecipeClasses() {
        return Collections.unmodifiableSet(recipeClasses);
    }

    /**
     * Adds a recipe class to the set of recipe classes.
     * 
     * @param recipeClass the recipe class to add.
     */
    public void addRecipeClass(@NotNull Class<R> recipeClass) {
        this.recipeClasses.add(recipeClass);
    }

    /**
     * Gets the set of blocks, items, or entities represented as an ItemStack that can serve as this process 
     * (e.g., Crafting table / Crafter or Campfire / Soul campfire).
     * 
     * @return the set of process options.
     */
    @NotNull
    public Set<ItemStack> getWorkbenchOptions() {
        return Collections.unmodifiableSet(workbenchOptions);
    }

    /**
     * Adds a process option to the set of process options.
     * 
     * @param workbenchOption the process option to add.
     */
    public void addWorkbenchOption(@NotNull ItemStack workbenchOption) {
        this.workbenchOptions.add(workbenchOption);
    }
}

// TODO process can be a singleton or completly static

// Process represent un moyen permettant de créer des item d'une manière (example avec d'autre item lors d'un craft)
// OK Un process peut avoir plusieurs recipe pour un item (ex: iron ingot avec un block de fer ou avec 9 lingots de fer)
// OK Un process peut avoir plusieur type de recipe (ex: shaped, shapeless or bien SmithingTrim, SmithingTransform)
// Un process peut être représenter par un block (ex: crafting table, furnace), un item (ex: pinceau), entity (ex: villager, fleche) qui sera ensuite représenté par un ItemStack
// Un process peux avoir plusieurs support sur le quelle ils peuvent être executer (ex: campifire, soulcampfire ou crafting table, crafter)
// Un process peut être associer à un RecpePanel qui permet de visualiser les recipe possible pour un item sur un process