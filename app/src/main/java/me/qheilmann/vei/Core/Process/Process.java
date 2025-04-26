package me.qheilmann.vei.Core.Process;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.Panels.UndefinedProcessPanel;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Core.Style.Styles.Style;
import me.qheilmann.vei.Core.Utils.NotNullSet;
import net.kyori.adventure.text.Component;

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
public class Process<R extends Recipe> implements Comparable<Process<?>>, Keyed{

    public static final Process<Recipe> UNDEFINED_PROCESS = UndefinedProcessHelper.PROCESS;

    private final NamespacedKey key;
    private final String processName;
    private final ItemStack processIcon;
    private final NotNullSet<ItemStack> workbenchOptions;
    private final NotNullSet<Class<? extends R>> compatibleRecipeTypes;
    private final TriFunction<Style, RecipeIndexService, ProcessRecipeReader<R>, ProcessPanel<R>> processPanelSupplier;

    /**
     * Provides a custom comparator for ordering processes.
     * The CraftingProcess is prioritized first, followed by all other vanilla processes in lexicographical order, then all other non vanilla processes in lexicographical order and finally the UndefinedProcess.
     * 
     * @return a comparator for ordering processes
     */
    public static Comparator<Process<?>> comparator() {

        Predicate<Process<?>> isCraftingProcess = (Process<?> p) -> p.getKey().equals(VanillaProcesses.CRAFTING_PROCESS_KEY);
        Predicate<Process<?>> isUndefinedProcess = (Process<?> p) -> p.getKey().equals(UNDEFINED_PROCESS.getKey());
        Predicate<Process<?>> isVanillaProcess = (Process<?> p) -> p.getKey().getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE);

        return Comparator.comparing((Process<?> p) -> !isCraftingProcess.test(p))
                    .thenComparing((Process<?> p) -> !isVanillaProcess.test(p))
                    .thenComparing(Comparator.comparing((Process<?> p) -> !isUndefinedProcess.test(p)).reversed())
                    .thenComparing((Process<?> p) -> p.getKey());

        // Note: negate with '!', is because the boolean comparator place false first and true last, so here we place it first if the predicate is true.
    }

    public Process(@NotNull NamespacedKey key,
                   @NotNull String processName,
                   @NotNull ItemStack processIcon,
                   @NotNull Collection<ItemStack> workbenchOptions,
                   @NotNull Collection<Class<? extends R>> recipeClasses,
                   @NotNull TriFunction<Style, RecipeIndexService, ProcessRecipeReader<R>, ProcessPanel<R>> processPanelSupplier ) {
        this.key = key;
        this.processName = processName;
        this.processIcon = processIcon;
        this.compatibleRecipeTypes = new NotNullSet<>(new LinkedHashSet<>(), recipeClasses);
        this.workbenchOptions = new NotNullSet<>(new LinkedHashSet<>(), workbenchOptions);
        this.processPanelSupplier = processPanelSupplier;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
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
    public ProcessPanel<R> generateProcessPanel(@NotNull Style style, @NotNull RecipeIndexService recipeIndexService, @NotNull ProcessRecipeReader<R> processRecipeReader) {
        return processPanelSupplier.apply(style, recipeIndexService, processRecipeReader);
    }

    /**
     * Gets the set of different recipe classes that are made inside the same process 
     * (e.g., ShapedRecipe.class, ShapelessRecipe.class, SmithingTrimRecipe.class, SmithingTransformRecipe.class).
     * 
     * @return the set of recipe classes.
     */
    @NotNull
    public Set<Class<? extends R>> getCompatibleRecipeTypes() {
        return Collections.unmodifiableSet(compatibleRecipeTypes);
    }

    /**
     * Adds a recipe class to the set of recipe classes.
     * 
     * @param recipeClass the recipe class to add.
     */
    public void addRecipeClass(@NotNull Class<R> recipeClass) {
        this.compatibleRecipeTypes.add(recipeClass);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Process<?> process = (Process<?>) obj;
        return this.equalKey(process.getKey());
    }

    /**
     * True if same process name, case insensitive.
     */
    private boolean equalKey(@Nullable NamespacedKey key) {
        return this.key.equals(key);
    }

    @Override
    public int compareTo(Process<?> that) {
        return comparator().compare(this, that);
    }

    public class ProcessRegistry {
        // Stores all registered processes in the order they were registered.
        private static final NotNullSet<Process<?>> processRegistry = new NotNullSet<>(new LinkedHashSet<>());
        
        static {
            processRegistry.add(UndefinedProcessHelper.PROCESS);
        }

        /**
         * Adds a process to the registry.
         * <p>
         * A process must be added to the registry before indexing its associated recipes.
         * <p>
         * In cases of inheritance (e.g., SmokingRecipe -> CookingRecipe), the child process must be added first (e.g., SmokingProcess before CookingProcess).
         * @param process the process to register.
         * @return true if the process was successfully registered, false if it was already registered.
         */
        public static boolean registerProcess(Process<?> process) {
            return registerBeforeUndefinedProcess(process);
        }

        /**
         * Registers a collection of processes in the order they are provided.
         * <p>
         * A process must be registered before its associated recipes can be indexed.
         * <p>
         * For inheritance scenarios (e.g., SmokingRecipe -> CookingRecipe), ensure child processes are registered first 
         * (e.g., register SmokingProcess before CookingProcess).
         * @param processes the collection of processes to register.
         * @return true if at least one process was successfully registered, false if all were already registered.
         */
        public static boolean registerProcesses(@NotNull Collection<Process<?>> processes) {
            return registerBeforeUndefinedProcess(processes);
        }

        /**
         * Get a process by its process name (e.g., Crafting, Smelting, Smithing).
         * It's case insensitive.
         *
         * @return the process or null if the process does not exist or was not implemented.
         */
        public static @Nullable Process<?> getProcessByKey(@NotNull NamespacedKey key) {
            for (Process<?> process : processRegistry) {
                if (process.equalKey(key)) {
                    return process;
                }
            }

            return null;
        }

        public static @NotNull Process<?> getProcessByRecipe(Recipe recipe) {
            for (Process<?> process : processRegistry) {
                for (Class<? extends Recipe> recipeClass : process.getCompatibleRecipeTypes()) {
                    if (recipeClass.isAssignableFrom(recipe.getClass())) {
                        return process;
                    }
                }
            }

            return Process.UNDEFINED_PROCESS;
        }

        private static boolean registerBeforeUndefinedProcess(Process<?> process) {
            boolean hasUndefinedProcessBeenRemoved = processRegistry.remove(UndefinedProcessHelper.PROCESS);
            boolean res = processRegistry.add(process);
            if (hasUndefinedProcessBeenRemoved) {
                processRegistry.add(UndefinedProcessHelper.PROCESS);
            }
            return res;
        }

        private static boolean registerBeforeUndefinedProcess(Collection<Process<?>> processes) {
            boolean hasUndefinedProcessBeenRemoved = processRegistry.remove(UndefinedProcessHelper.PROCESS);
            boolean res = processRegistry.addAll(processes);
            if (hasUndefinedProcessBeenRemoved) {
                processRegistry.add(UndefinedProcessHelper.PROCESS);
            }
            return res;
        }
    }

    /**
     * A undefined process that is used when no other process is found, it the default process.
     */

    private static class UndefinedProcessHelper {

        private static final String PROCESS_NAME = "Undefined";
        private static final Process<Recipe> PROCESS = getUndefinedProcess();

        private static Process<Recipe> getUndefinedProcess() {
            return new Process<>(
                new NamespacedKey(VanillaEnoughItems.NAMESPACE, "undefined"),
                PROCESS_NAME,
                generateIcon(),
                getWorkbenchOptions(),
                getRecipeClasses(),
                (style, recipeIndex, recipeReader) -> new UndefinedProcessPanel(style, recipeIndex, recipeReader)
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
            return Arrays.asList(); // empty list for the undefined process
        }

        private static Collection<Class<? extends Recipe>> getRecipeClasses() {
            return Arrays.asList(
                Recipe.class
            );
        }
    }
}