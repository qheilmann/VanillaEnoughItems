package me.qheilmann.vei.Core.RecipeView;

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
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;



/**
 * Abstract class representing a view for a recipe.
 * 
 * @param <T> The type of recipe this view handles.
 * 
 * To extend this class, you need to:
 * 1. Implement the abstract methods:
 *    - {@link #getContentView(EnumSet<SlotType> slotTypes)}
 *    - {@link #clear(EnumSet<SlotType> slotTypes)}
 *    - {@link #cycle(EnumSet<SlotType> slotTypes)}
 * 2. Optionally override the following methods to customize button slots:
 *    - {@link #getNextRecipeSlot()}
 *    - {@link #getPreviousRecipeSlot()}
 *    - {@link #getForwardRecipeSlot()}
 *    - {@link #getBackwardRecipeSlot()}
 *    - {@link #getMoveIngredientsSlot()}
 *    - {@link #getWorkbenchSlot()}
 *    - {@link #getWorkbenchMaterial()}
 */
public abstract class RecipeView<T extends Recipe> {
    
    private T recipe;
    protected HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> recipeViewSlots;

    private static final RecipeViewSlot DEFAULT_NEXT_RECIPE_SLOT      = new RecipeViewSlot(3, 0);
    private static final RecipeViewSlot DEFAULT_PREVIOUS_RECIPE_SLOT  = new RecipeViewSlot(1, 0);
    private static final RecipeViewSlot DEFAULT_FORWARD_RECIPE_SLOT   = new RecipeViewSlot(3, 4);
    private static final RecipeViewSlot DEFAULT_BACKWARD_RECIPE_SLOT  = new RecipeViewSlot(1, 4);
    private static final RecipeViewSlot DEFAULT_MOVE_INGREDIENTS_SLOT = new RecipeViewSlot(5, 3);
    private static final RecipeViewSlot DEFAULT_WORKBENCH_SLOT        = new RecipeViewSlot(4, 2);
    private static final Material DEFAULT_WORKBENCH_MATERIAL         = Material.CRAFTING_TABLE;
    
    
    public RecipeView(@NotNull T recipe) { // TODO change to WorkbenchRecipeSet with variante
        this(recipe, 0);
    }

    public RecipeView(@NotNull T recipe, int variante) { // TODO change to WorkbenchRecipeSet with variante
        Preconditions.checkNotNull(recipe, "recipe cannot be null");
        this.recipe = recipe;
        this.recipeViewSlots = new HashMap<>();
        // TODO depending on the recipe type populate the recipeViewSlots
    }

    public abstract void cycle(EnumSet<SlotType> slotTypes);
    
    @NotNull
    protected abstract SlotSequence<RecipeViewSlot> getIngredientsSlotRange();

    @NotNull
    protected abstract SlotSequence<RecipeViewSlot> getResultsSlotRange();

    /**
     * Returns the slot for the next recipe button or null if not present.
     * Override this method to change the button's position.
     */
    @Nullable
    protected RecipeViewSlot getNextRecipeSlot() {
        return RecipeView.DEFAULT_NEXT_RECIPE_SLOT;
    }

    /**
     * Returns the slot for the previous recipe button or null if not present.
     * Override this method to change the button's position.
     */
    @Nullable
    protected RecipeViewSlot getPreviousRecipeSlot() {
        return RecipeView.DEFAULT_PREVIOUS_RECIPE_SLOT;
    }

    /**
     * Get the slot for the forward recipe button or null if not present
     * Override this method to change the button's position
     */
    @Nullable
    protected RecipeViewSlot getForwardRecipeSlot() {
        return RecipeView.DEFAULT_FORWARD_RECIPE_SLOT;
    }

    /**
     * Get the slot for the backward recipe button or null if not present
     * Override this method to change the button's position
     */
    @Nullable
    protected RecipeViewSlot getBackwardRecipeSlot() {
        return RecipeView.DEFAULT_BACKWARD_RECIPE_SLOT;
    }

    /**
     * Get the slot for the move ingredients button or null if not present
     * Override this method to change the button's position
     */
    @Nullable
    protected RecipeViewSlot getMoveIngredientsSlot() {
        return RecipeView.DEFAULT_MOVE_INGREDIENTS_SLOT;
    }

    /**
     * Get the slot for the workbench or null if not present
     * Override this method to change the workbench's position
     */
    @Nullable
    protected RecipeViewSlot getWorkbenchSlot() {
        return RecipeView.DEFAULT_WORKBENCH_SLOT;
    }

    /**
     * Get the material for the workbench or null if not present
     * Override this method to change the workbench's material
     */
    @Nullable
    protected Material getWorkbenchMaterial() {
        return RecipeView.DEFAULT_WORKBENCH_MATERIAL;
    }

    public void setRecipe(@NotNull T recipe) { // TODO change to WorkbenchRecipeSet with variante
        setRecipe(recipe, 0);
    }

    public void setRecipe(@NotNull T recipe, int variante) { // TODO change to WorkbenchRecipeSet with variante
        Preconditions.checkNotNull(recipe, "recipe cannot be null");
        this.recipe = recipe;
    }

    public T getRecipe() {
        return recipe;
    }

    public void clear() {
        clear(SlotType.RECIPE);
    }

    public void attachMenuButton(ButtonType buttonType, GuiItem<RecipeMenu> parentButton) {
        switch (buttonType) {
            case NEXT_RECIPE:
                attachSlotIfCoordNotNull(getNextRecipeSlot(), parentButton);
                break;
            case PREVIOUS_RECIPE:
                attachSlotIfCoordNotNull(getPreviousRecipeSlot(), parentButton);
                break;
            case BACKWARD_RECIPE:
                attachSlotIfCoordNotNull(getBackwardRecipeSlot(), parentButton);
                break;
            case FORWARD_RECIPE:
                attachSlotIfCoordNotNull(getForwardRecipeSlot(), parentButton);
                break;
            case MOVE_INGREDIENTS:
                attachSlotIfCoordNotNull(getMoveIngredientsSlot(), parentButton);
                break;
            default:
                throw new IllegalArgumentException("Unknown button type: " + buttonType);
        }
    }

    public HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> getContentView(EnumSet<SlotType> slotTypes) {
        HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> contentView = new HashMap<>();
        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (RecipeViewSlot slot : getIngredientsSlotRange()) {
                contentView.put(slot, recipeViewSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.RESULTS)) {
            for (RecipeViewSlot slot : getResultsSlotRange()) {
                contentView.put(slot, recipeViewSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            contentView.put(getWorkbenchSlot(), recipeViewSlots.get(getWorkbenchSlot()));
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            contentView.put(getNextRecipeSlot(), recipeViewSlots.get(getNextRecipeSlot()));
            contentView.put(getPreviousRecipeSlot(), recipeViewSlots.get(getPreviousRecipeSlot()));
            contentView.put(getForwardRecipeSlot(), recipeViewSlots.get(getForwardRecipeSlot()));
            contentView.put(getBackwardRecipeSlot(), recipeViewSlots.get(getBackwardRecipeSlot()));
            contentView.put(getMoveIngredientsSlot(), recipeViewSlots.get(getMoveIngredientsSlot()));
        }

        if (slotTypes.contains(SlotType.PADDING)) {
            // TODO what about padding?
        }

        return contentView;
    }

    public void clear(EnumSet<SlotType> slotTypes) {

        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (RecipeViewSlot slot : getIngredientsSlotRange()) {
                recipeViewSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        // No consumables in shaped recipes

        if (slotTypes.contains(SlotType.RESULTS)) {
            for (RecipeViewSlot slot : getResultsSlotRange()) {
                recipeViewSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            recipeViewSlots.put(getWorkbenchSlot(), new GuiItem<>(ItemStack.empty()));
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            recipeViewSlots.put(getNextRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(getPreviousRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(getForwardRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(getBackwardRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipeViewSlots.put(getMoveIngredientsSlot(), new GuiItem<>(ItemStack.empty()));
        }

        if (slotTypes.contains(SlotType.PADDING)) {
            // TODO what about padding?
        }
    }

    private void attachSlotIfCoordNotNull(RecipeViewSlot coord, GuiItem<RecipeMenu> parentButton) {
        if(coord != null) {
            recipeViewSlots.put(coord, parentButton);
        }
    }

    public enum ButtonType {
        NEXT_RECIPE,
        PREVIOUS_RECIPE,
        FORWARD_RECIPE,
        BACKWARD_RECIPE,
        MOVE_INGREDIENTS
    }

    public enum SlotType {
        INGREDIENTS,
        CONSUMABLES,
        RESULTS,
        BUTTONS,
        WORKBENCH,
        PADDING,
        OTHER;
        
        public static EnumSet<SlotType> ALL = EnumSet.allOf(SlotType.class);
        public static EnumSet<SlotType> RECIPE = EnumSet.of(SlotType.INGREDIENTS, SlotType.CONSUMABLES, SlotType.RESULTS);
        // CHANGED, // TODO add changed slot types (how can i save the change the change here and be ablse to track change for other classes modifing the map)
    }
}
