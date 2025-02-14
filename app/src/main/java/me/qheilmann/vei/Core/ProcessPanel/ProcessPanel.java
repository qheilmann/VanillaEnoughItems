package me.qheilmann.vei.Core.ProcessPanel;

import java.util.EnumSet;
import java.util.HashMap;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.ProcessPanel.Panels.CraftingProcessPanel;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;

/**
 * <h1>RecipeView</h1>
 * Abstract class representing a view for a recipe.
 * <p>
 * A recipe view is a representation of a recipe in a GUI.
 * It is used to display the recipe's ingredients, results, consumables, etc
 * in a GUI. The size of the recipe view is 7x5. In the bottom center is the 
 * recipe menu.
 * <p>
 * To extend this class, you need to implement the abstract methods:
 * <ul>
 *     <li>{@link #getIngredients()}</li>
 *     <li>{@link #getResults()}</li>
 *     <li>{@link #getConsumables()}</li>
 *     <li>{@link #populateCraftingSlots()}</li>
 *     <li>{@link #cycle(EnumSet)}</li>
 * </ul>
 * Optionally override the following methods to customize button slots:
 * <ul>
 *     <li>{@link #getNextRecipeSlot()}</li>
 *     <li>{@link #getPreviousRecipeSlot()}</li>
 *     <li>{@link #getForwardRecipeSlot()}</li>
 *     <li>{@link #getBackwardRecipeSlot()}</li>
 *     <li>{@link #getMoveIngredientsSlot()}</li>
 *     <li>{@link #getWorkbenchSlot()}</li>
 *     <li>{@link #getWorkbenchMaterial()}</li>
 * </ul>
 * <p>
 * @see {@link CraftingProcessPanel}
 * @param <T> The type of recipe this view handles.
 */
public abstract class ProcessPanel<T extends Recipe> {
    
    private T recipe;
    protected HashMap<ProcessPanelSlot, GuiItem<RecipeMenu>> recipeViewSlots;

    private static final ProcessPanelSlot DEFAULT_NEXT_RECIPE_SLOT      = new ProcessPanelSlot(3, 0);
    private static final ProcessPanelSlot DEFAULT_PREVIOUS_RECIPE_SLOT  = new ProcessPanelSlot(1, 0);
    private static final ProcessPanelSlot DEFAULT_FORWARD_RECIPE_SLOT   = new ProcessPanelSlot(3, 4);
    private static final ProcessPanelSlot DEFAULT_BACKWARD_RECIPE_SLOT  = new ProcessPanelSlot(1, 4);
    private static final ProcessPanelSlot DEFAULT_MOVE_INGREDIENTS_SLOT = new ProcessPanelSlot(5, 3);
    private static final Material DEFAULT_WORKBENCH_MATERIAL         = Material.CRAFTING_TABLE;
    
    /**
     * Create a new recipe view for the given recipe.
     * @param recipe The recipe to display.
     */
    public ProcessPanel(@NotNull T recipe) { // TODO change to WorkbenchRecipeSet with variante
        this(recipe, 0);
    }

    /**
     * Create a new recipe view for the given recipe.
     * @param recipe The recipe to display.
     * @param variante The variante index of the recipe to display.
     */
    public ProcessPanel(@NotNull T recipe, int variante) { // TODO change to WorkbenchRecipeSet with variante
        Preconditions.checkNotNull(recipe, "recipe cannot be null");
        this.recipeViewSlots = new HashMap<>();
        setRecipe(recipe);
    }

    /**
     * Cycle all cycle slots of the given types. For example, if there are 
     * multiple ingredients for a recipe, this method
     * will cycle through them.
     * @param slotTypes The slot types to cycle.
     */
    public abstract void cycle(EnumSet<SlotType> slotTypes);
    
    /**
     * Get a SlotSequence of all the slots that contain the recipe's ingredients.
     */
    @NotNull
    protected abstract SlotSequence<ProcessPanelSlot> getIngredients();

    /**
     * Get a SlotSequence of all the slots that contain the recipe's results.
     */
    @NotNull
    protected abstract SlotSequence<ProcessPanelSlot> getResults();

    /**
     * Get a SlotSequence of all the slots that contain the recipe's consumables.
     */
    @NotNull
    protected abstract SlotSequence<ProcessPanelSlot> getConsumables();

    /**
     * Populate the crafting slots with the recipe's ingredients, results, etc.
     */
    protected abstract void populateCraftingSlots(); 

    /**
     * Returns the slot for the next recipe button.
     * Override this method to change the button's position.
     */
    @NotNull
    protected ProcessPanelSlot getNextRecipeSlot() {
        return ProcessPanel.DEFAULT_NEXT_RECIPE_SLOT;
    }

    /**
     * Returns the slot for the previous recipe button.
     * Override this method to change the button's position.
     */
    @NotNull
    protected ProcessPanelSlot getPreviousRecipeSlot() {
        return ProcessPanel.DEFAULT_PREVIOUS_RECIPE_SLOT;
    }

    /**
     * Get the slot for the forward recipe button.
     * Override this method to change the button's position
     */
    @NotNull
    protected ProcessPanelSlot getForwardRecipeSlot() {
        return ProcessPanel.DEFAULT_FORWARD_RECIPE_SLOT;
    }

    /**
     * Get the slot for the backward recipe button.
     * Override this method to change the button's position
     */
    @NotNull
    protected ProcessPanelSlot getBackwardRecipeSlot() {
        return ProcessPanel.DEFAULT_BACKWARD_RECIPE_SLOT;
    }

    /**
     * Get the slot for the move ingredients button or null if not present
     * Override this method to change the button's position
     */
    @Nullable
    protected ProcessPanelSlot getMoveIngredientsSlot() {
        return ProcessPanel.DEFAULT_MOVE_INGREDIENTS_SLOT;
    }

    /**
     * Get the slot for the workbench or null if not present
     * Override this method to change the workbench's position
     */
    @Nullable
    protected ProcessPanelSlot getWorkbenchSlot() {
        return null;
    }

    /**
     * Get the material for the workbench or null if not present
     * Override this method to change the workbench's material
     */
    @Nullable
    protected Material getWorkbenchMaterial() {
        return ProcessPanel.DEFAULT_WORKBENCH_MATERIAL;
    }

    /**
     * Set the recipe to display in the view.
     * @param recipe The recipe to display.
     */
    public void setRecipe(@NotNull T recipe) { // TODO change to WorkbenchRecipeSet with variante
        setRecipe(recipe, 0);
    }

    /**
     * Set the recipe to display in the view.
     * @param recipe The recipe to display.
     * @param variante The variante index of the recipe to display.
     */
    public void setRecipe(@NotNull T recipe, int variante) { // TODO change to WorkbenchRecipeSet with variante
        Preconditions.checkNotNull(recipe, "recipe cannot be null");
        this.recipe = recipe;
        populateCraftingSlots();
    }

    /**
     * Get the current recipe displayed in the view.
     * @return The recipe displayed in the view.
     */
    public T getRecipe() {
        return recipe;
    }

    /**
     * Attach a button to the view.
     * @param buttonType The type of button to attach.
     * @param parentButton The button to attach.
     */
    public void attachMenuButton(ButtonType buttonType, GuiItem<RecipeMenu> parentButton) {
        switch (buttonType) {
            case NEXT_RECIPE:
                putSlotIfNotNull(getNextRecipeSlot(), parentButton);
                break;
            case PREVIOUS_RECIPE:
                putSlotIfNotNull(getPreviousRecipeSlot(), parentButton);
                break;
            case BACKWARD_RECIPE:
                putSlotIfNotNull(getBackwardRecipeSlot(), parentButton);
                break;
            case FORWARD_RECIPE:
                putSlotIfNotNull(getForwardRecipeSlot(), parentButton);
                break;
            case MOVE_INGREDIENTS:
                putSlotIfNotNull(getMoveIngredientsSlot(), parentButton);
                break;
            default:
                throw new IllegalArgumentException("Unknown button type: " + buttonType);
        }
    }

    /**
     * Get a view of the content for the given slot types.
     * @param slotTypes The slot types to get the content for.
     * @return A map of slot to item instance for the given slot types.
     */
    public HashMap<ProcessPanelSlot, GuiItem<RecipeMenu>> getContentView(EnumSet<SlotType> slotTypes) {
        HashMap<ProcessPanelSlot, GuiItem<RecipeMenu>> contentView = new HashMap<>();
        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (ProcessPanelSlot slot : getIngredients()) {
                contentView.put(slot, recipeViewSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.RESULTS)) {
            for (ProcessPanelSlot slot : getResults()) {
                contentView.put(slot, recipeViewSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.CONSUMABLES)) {
            for (ProcessPanelSlot slot : getConsumables()) {
                contentView.put(slot, recipeViewSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            ProcessPanelSlot workbenchSlot = getWorkbenchSlot();
            if (workbenchSlot != null) {
                contentView.put(getWorkbenchSlot(), recipeViewSlots.get(getWorkbenchSlot()));
            }
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            contentView.put(getNextRecipeSlot(), recipeViewSlots.get(getNextRecipeSlot()));
            contentView.put(getPreviousRecipeSlot(), recipeViewSlots.get(getPreviousRecipeSlot()));
            contentView.put(getForwardRecipeSlot(), recipeViewSlots.get(getForwardRecipeSlot()));
            contentView.put(getBackwardRecipeSlot(), recipeViewSlots.get(getBackwardRecipeSlot()));
            putSlotIfNotNull(getMoveIngredientsSlot(), recipeViewSlots.get(getMoveIngredientsSlot()));
        }

        if (slotTypes.contains(SlotType.PADDING)) {
            // TODO what about padding?
        }

        return contentView;
    }

    /**
     * Clear all items that are part of the recipe
     */
    public void clear() {
        clear(SlotType.RECIPE);
    }

    /**
     * Clear all items for the given slot types.
     * @param slotTypes The slot types to clear.
     */
    public void clear(EnumSet<SlotType> slotTypes) {

        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (ProcessPanelSlot slot : getIngredients()) {
                recipeViewSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.RESULTS)) {
            for (ProcessPanelSlot slot : getResults()) {
                recipeViewSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.CONSUMABLES)) {
            for (ProcessPanelSlot slot : getConsumables()) {
                recipeViewSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            putSlotIfNotNull(getWorkbenchSlot(), new GuiItem<>(ItemStack.empty()));
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            recipeViewSlots.put(getNextRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(getPreviousRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(getForwardRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(getBackwardRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            if (getMoveIngredientsSlot() != null) {
                putSlotIfNotNull(getMoveIngredientsSlot(), new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.PADDING)) {
            // TODO what about padding?
        }
    }

    private void putSlotIfNotNull(ProcessPanelSlot coord, GuiItem<RecipeMenu> parentButton) {
        if(coord != null) {
            recipeViewSlots.put(coord, parentButton);
        }
    }

    /**
     * The type of button that can be attached to the view.
     */
    public enum ButtonType {
        NEXT_RECIPE,
        PREVIOUS_RECIPE,
        FORWARD_RECIPE,
        BACKWARD_RECIPE,
        MOVE_INGREDIENTS
    }

    /**
     * The type of slot that can be inside the recipe panel.
     */
    public enum SlotType {
        INGREDIENTS,
        CONSUMABLES,
        RESULTS,
        BUTTONS,
        WORKBENCH,
        PADDING,
        OTHER;
        
        public static EnumSet<SlotType> ALL = EnumSet.allOf(SlotType.class);

        /**
         * The slot types that are part of a recipe. (INGREDIENTS, CONSUMABLES, RESULTS)
         */
        public static EnumSet<SlotType> RECIPE = EnumSet.of(SlotType.INGREDIENTS, SlotType.CONSUMABLES, SlotType.RESULTS);

        // CHANGED, // TODO add changed slot types (how can i save the change the change here and be ablse to track change for other classes modifing the map)
    }
}
