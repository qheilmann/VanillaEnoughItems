package me.qheilmann.vei.Core.Process;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.Utils.NotNullSet;

/**
 * Abstract base class for item creation processes using recipes.
 * A process is a way to create items with or without other items.
 * <ul>
 * <li>A process can have multiple recipe types (e.g., ShapedRecipe,
 * ShapelessRecipe for Crafting, SmithingTrimRecipe, SmithingTransformRecipe
 * for Smithing).</li>
 * <li>It can be used by various workbenches (e.g., Crafting table /
 * Crafter for Crafting).</li>
 * <li>It can be represented by a block, item, or entity.</li>
 * </ul>
 */
public abstract class Process<T extends ProcessPanel<?>> {

    private NotNullSet<Class<Recipe>> recipeClasses;
    private NotNullSet<ItemStack> workbenchOptions;

    /**
     * Gets the process name (e.g., Crafting, Smelting, Smithing).
     * 
     * @return the process name.
     */
    @NotNull
    public abstract String getProcessName();

    /**
     * Gets the recipe panel for the process.
     * 
     * @return the recipe panel.
     */
    @Nullable
    public abstract ProcessPanel<Recipe> getRecipePanel();

    /**
     * Gets the set of different recipe classes that are made inside the same process 
     * (e.g., ShapedRecipe.class, ShapelessRecipe.class, SmithingTrimRecipe.class, SmithingTransformRecipe.class).
     * 
     * @return the set of recipe classes.
     */
    @NotNull
    public NotNullSet<Class<Recipe>> getRecipeClasses() {
        return recipeClasses;
    }

    /**
     * Adds a recipe class to the set of recipe classes.
     * 
     * @param recipeClass the recipe class to add.
     */
    public void addRecipeClass(@NotNull Class<Recipe> recipeClass) {
        this.recipeClasses.add(recipeClass);
    }

    /**
     * Gets the set of blocks, items, or entities represented as an ItemStack that can serve as this process 
     * (e.g., Crafting table / Crafter or Campfire / Soul campfire).
     * 
     * @return the set of process options.
     */
    @NotNull
    public NotNullSet<ItemStack> getWorkbenchOptions() {
        return workbenchOptions;
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

// Process represent un moyen permettant de créer des item d'une manière (example avec d'autre item lors d'un craft)
// OK Un process peut avoir plusieurs recipe pour un item (ex: iron ingot avec un block de fer ou avec 9 lingots de fer)
// OK Un process peut avoir plusieur type de recipe (ex: shaped, shapeless or bien SmithingTrim, SmithingTransform)
// Un process peut être représenter par un block (ex: crafting table, furnace), un item (ex: pinceau), entity (ex: villager, fleche) qui sera ensuite représenté par un ItemStack
// Un process peux avoir plusieurs support sur le quelle ils peuvent être executer (ex: campifire, soulcampfire ou crafting table, crafter)
// Un process peut être associer à un RecpePanel qui permet de visualiser les recipe possible pour un item sur un process