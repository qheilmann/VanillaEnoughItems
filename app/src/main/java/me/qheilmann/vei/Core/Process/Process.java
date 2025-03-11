package me.qheilmann.vei.Core.Process;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
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
public abstract class Process<R extends Recipe> {

    private NotNullSet<Class<R>> recipeClasses;
    private NotNullSet<ItemStack> workbenchOptions;

    /**
     * Gets the process name (e.g., Crafting, Smelting, Smithing).
     * 
     * @return the process name.
     */
    @NotNull
    public abstract String getProcessName();

    /**
     * Gets the item stack that represents the process.
     * 
     * @return the item stack.
     */
    @NotNull
    public abstract ItemStack getProcessIcon();

    /**
     * Gets the recipe panel for the process.
     * 
     * @return the recipe panel.
     */
    @NotNull
    public abstract ProcessPanel<R> generateProcessPanel(@NotNull ProcessRecipeSet<R> processRecipeSet, int variant);

    /**
     * Gets the set of different recipe classes that are made inside the same process 
     * (e.g., ShapedRecipe.class, ShapelessRecipe.class, SmithingTrimRecipe.class, SmithingTransformRecipe.class).
     * 
     * @return the set of recipe classes.
     */
    @NotNull
    public NotNullSet<Class<R>> getRecipeClasses() {
        return recipeClasses;
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