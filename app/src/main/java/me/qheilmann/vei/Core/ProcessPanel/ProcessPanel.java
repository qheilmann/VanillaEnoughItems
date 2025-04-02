package me.qheilmann.vei.Core.ProcessPanel;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.ProcessPanel.Panels.CraftingProcessPanel;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.Core.Style.Styles.Style;

/**
 * <h1>ProcessPanel</h1>
 * Abstract class representing a panel for a recipe.
 * <p>
 * A process panel is a representation of a recipe in a GUI.
 * It is used to display the recipe's ingredients, results, consumables, etc.
 * in a GUI. The size of the process panel is 7x5. In the bottom center is the 
 * recipe menu.
 * <p>
 * To extend this class, you need to implement the abstract methods:
 * <ul>
 *     <li>{@link #getIngredientSlots()}</li>
 *     <li>{@link #getResultSlots()}</li>
 *     <li>{@link #getConsumableSlots()}</li>
 *     <li>{@link #render(EnumSet)}</li>
 *     <li>{@link #cycle(EnumSet)}</li>
 * </ul>
 * Optionally override the following methods to add more customization:
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
 * @param <R> The type of recipe this panel handles.
 */
public abstract class ProcessPanel<R extends Recipe> { // TODO maybe need to depend from a Process class instead of Recipe

    private static final ProcessPanelSlot DEFAULT_NEXT_RECIPE_SLOT      = new ProcessPanelSlot(3, 0);
    private static final ProcessPanelSlot DEFAULT_PREVIOUS_RECIPE_SLOT  = new ProcessPanelSlot(1, 0);
    private static final ProcessPanelSlot DEFAULT_FORWARD_RECIPE_SLOT   = new ProcessPanelSlot(3, 4);
    private static final ProcessPanelSlot DEFAULT_BACKWARD_RECIPE_SLOT  = new ProcessPanelSlot(1, 4);
    private static final ProcessPanelSlot DEFAULT_MOVE_INGREDIENTS_SLOT = new ProcessPanelSlot(5, 3);
    private static final Material DEFAULT_WORKBENCH_MATERIAL            = Material.CRAFTING_TABLE;
    
    protected HashMap<ProcessPanelSlot, GuiItem<RecipeMenu>> recipePanelSlots;
    
    private final Style style;
    private final RecipeIndexService recipeIndex;
    private final ProcessRecipeReader<R> recipeReader;

    protected Map<AttachedButtonType, GuiItem<RecipeMenu>> attachedButtons = new HashMap<>();

    /**
     * Create a new recipe panel for the given recipe.
     * @param style The style of the panel.
     * @param recipeIndex The recipe index service.
     * @param recipeReader The recipe reader for the recipe.
     */
    public ProcessPanel(@NotNull Style style, @NotNull RecipeIndexService recipeIndex, @NotNull ProcessRecipeReader<R> recipeReader) {
        Preconditions.checkNotNull(style, "recipe cannot be null");
        Preconditions.checkNotNull(recipeIndex, "recipeIndex cannot be null");
        Preconditions.checkNotNull(recipeReader, "recipeReader cannot be null");

        this.recipePanelSlots = new HashMap<>();
        this.style = style;
        this.recipeIndex = recipeIndex;
        this.recipeReader = recipeReader;
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
    protected abstract SlotSequence<ProcessPanelSlot> getIngredientSlots();

    /**
     * Get a SlotSequence of all the slots that contain the recipe's results.
     */
    @NotNull
    protected abstract SlotSequence<ProcessPanelSlot> getResultSlots();

    /**
     * Get a SlotSequence of all the slots that contain the recipe's consumables.
     */
    @NotNull
    protected abstract SlotSequence<ProcessPanelSlot> getConsumableSlots();

    /**
     * Populate the recipe panel with the recipe's ingredients, results, consumables, etc.
     * @param visibleButtonTypes A set containing the types of each button that should be visible.
     */
    public void render(EnumSet<AttachedButtonType> buttonsVisibility) {
        clear();
        renderAttachedButtons(buttonsVisibility);
    }

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

    public int getVariantCount() {
        return recipeReader.getAllRecipes().size();
    }

    /**
     * Get the current recipe selected in the panel.
     * @return The recipe selected in the panel.
     */
    @NotNull
    public R getCurrentRecipe() {
        return recipeReader.currentRecipe();
    }

    /**
     * Attach a button to the panel.
     * @param buttonType The type of button to attach.
     * @param attachedButton The button to attach.
     */
    public void attachPannelButton(AttachedButtonType buttonType, GuiItem<RecipeMenu> attachedButton) {
        attachedButtons.put(buttonType, attachedButton);
    }

    protected void putAttachedButtons(AttachedButtonType buttonType, GuiItem<RecipeMenu> attachedButton) {
        ProcessPanelSlot slot = null;
        switch (buttonType) {
            case NEXT_RECIPE:
                slot = getNextRecipeSlot();
                break;
            case PREVIOUS_RECIPE:
                slot = getPreviousRecipeSlot();
                break;
            case FORWARD_RECIPE:
                slot = getForwardRecipeSlot();
                break;
            case BACKWARD_RECIPE:
                slot = getBackwardRecipeSlot();
                break;
            case MOVE_INGREDIENTS:
                slot = getMoveIngredientsSlot();
                break;
            default:
                throw new IllegalArgumentException("Unimplemented button type: " + buttonType);
        }
        recipePanelSlots.put(slot, attachedButton);
    }

    protected void renderAttachedButtons(EnumSet<AttachedButtonType> buttonsVisibility) {
        for (AttachedButtonType buttonType : buttonsVisibility) {
            GuiItem<RecipeMenu> attachedItem = attachedButtons.get(buttonType);
            if (attachedItem == null) continue;
            
            putAttachedButtons(buttonType, attachedItem);
        }
    }

    /**
     * Get a panel of the content for the given slot types.
     * @param slotTypes The slot types to get the content for.
     * @return A map of slot to item instance for the given slot types.
     */
    public Map<ProcessPanelSlot, GuiItem<RecipeMenu>> getContentPanel(EnumSet<SlotType> slotTypes) {

        if (slotTypes.equals(SlotType.ALL)) {
            return recipePanelSlots;
        }

        HashMap<ProcessPanelSlot, GuiItem<RecipeMenu>> contentPanel = new HashMap<>();
        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (ProcessPanelSlot slot : getIngredientSlots()) {
                contentPanel.put(slot, recipePanelSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.RESULTS)) {
            for (ProcessPanelSlot slot : getResultSlots()) {
                contentPanel.put(slot, recipePanelSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.CONSUMABLES)) {
            for (ProcessPanelSlot slot : getConsumableSlots()) {
                contentPanel.put(slot, recipePanelSlots.get(slot));
            }
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            ProcessPanelSlot workbenchSlot = getWorkbenchSlot();
            if (workbenchSlot != null) {
                contentPanel.put(getWorkbenchSlot(), recipePanelSlots.get(getWorkbenchSlot()));
            }
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            contentPanel.put(getNextRecipeSlot(), recipePanelSlots.get(getNextRecipeSlot()));
            contentPanel.put(getPreviousRecipeSlot(), recipePanelSlots.get(getPreviousRecipeSlot()));
            contentPanel.put(getForwardRecipeSlot(), recipePanelSlots.get(getForwardRecipeSlot()));
            contentPanel.put(getBackwardRecipeSlot(), recipePanelSlots.get(getBackwardRecipeSlot()));
            putSlotIfNotNull(getMoveIngredientsSlot(), recipePanelSlots.get(getMoveIngredientsSlot()));
        }

        if (slotTypes.contains(SlotType.PADDING)) {
            // TODO what about padding?
        }

        return contentPanel;
    }

    /**
     * Clear all items that are part of the recipe
     */
    public void clearRecipe() { // Rename to clear Recipe + make an empty to clear all
        clear(SlotType.RECIPE);
    }

    /**
     * Clear all items in the panel.
     */
    public void clear() {
        clear(SlotType.ALL);
    }

    /**
     * Clear all items for the given slot types.
     * @param slotTypes The slot types to clear.
     */
    public void clear(EnumSet<SlotType> slotTypes) {

        if (slotTypes.equals(SlotType.ALL)) {
            recipePanelSlots.clear();
            return;
        }

        if (slotTypes.contains(SlotType.INGREDIENTS)) {
            for (ProcessPanelSlot slot : getIngredientSlots()) {
                recipePanelSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.RESULTS)) {
            for (ProcessPanelSlot slot : getResultSlots()) {
                recipePanelSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.CONSUMABLES)) {
            for (ProcessPanelSlot slot : getConsumableSlots()) {
                recipePanelSlots.put(slot, new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.WORKBENCH)) {
            putSlotIfNotNull(getWorkbenchSlot(), new GuiItem<>(ItemStack.empty()));
        }

        if (slotTypes.contains(SlotType.BUTTONS)) {
            recipePanelSlots.put(getNextRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipePanelSlots.put(getPreviousRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipePanelSlots.put(getForwardRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            recipePanelSlots.put(getBackwardRecipeSlot(), new GuiItem<>(ItemStack.empty()));
            if (getMoveIngredientsSlot() != null) {
                putSlotIfNotNull(getMoveIngredientsSlot(), new GuiItem<>(ItemStack.empty()));
            }
        }

        if (slotTypes.contains(SlotType.PADDING)) {
            // TODO what about padding?
        }
    }

    /**
     * Creates a GuiItem that opens the recipe inside the recipeMenu for the given item.
     * @param processRecipeSet The item to show and open the recipe for.
     * @return The new GuiItem instance.
     */
    protected GuiItem<RecipeMenu> buildNewRecipeGuiItem(ItemStack item) {
        GuiItem<RecipeMenu> guiItem = new GuiItem<>(item);
        guiItem.setAction((event, menu) -> {
            MixedProcessRecipeReader recipeReader = recipeIndex.getByResult(item);
            if (recipeReader == null) {
                return;
            }

            RecipeMenu recipeMenu = new RecipeMenu(style, recipeIndex, recipeReader);
            recipeMenu.open(event.getWhoClicked());
        });

        return guiItem;
    }

    private void putSlotIfNotNull(ProcessPanelSlot coord, GuiItem<RecipeMenu> parentButton) {
        if(coord != null) {
            recipePanelSlots.put(coord, parentButton);
        }
    }

    /**
     * The type of button that can be attached to the panel.
     */
    public enum AttachedButtonType {
        NEXT_RECIPE,
        PREVIOUS_RECIPE,
        FORWARD_RECIPE,
        BACKWARD_RECIPE,
        MOVE_INGREDIENTS;

        public static EnumSet<AttachedButtonType> ALL = EnumSet.allOf(AttachedButtonType.class);
        public static EnumSet<AttachedButtonType> NONE = EnumSet.noneOf(AttachedButtonType.class);
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
        public static EnumSet<SlotType> NONE = EnumSet.noneOf(SlotType.class);

        /**
         * The slot types that are part of a recipe. (INGREDIENTS, CONSUMABLES, RESULTS)
         */
        public static EnumSet<SlotType> RECIPE = EnumSet.of(SlotType.INGREDIENTS, SlotType.CONSUMABLES, SlotType.RESULTS);

        // CHANGED, // TODO add changed slot types (how can i save the change the change here and be ablse to track change for other classes modifing the map)
    }
}
