package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.Map;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

// TODO should realy this be a Abstact class or can it be an interface? or both stage ?
/**
 * Abstract base class for process-specific recipe panel renderers.
 * Each implementation handles rendering a specific type of process (e.g., Crafting, Smelting).
 * Panels are stateful but not change, a new ProcessPanel is regenerated on recipe change like nextRecipe().
 * IMPORTANT: The provided panel should handle any recipe supported by the associated process, {@link Process#canHandleRecipe(Recipe)}
 */
@NullMarked
public abstract class AbstractProcessPanel {
    protected final Recipe recipe;
    protected final RecipeGuiActions actions;
    protected final RecipeGuiContext context;

    private final Map<RecipeGuiSharedButton, ProcessPannelSlot> sharedButtonSlots;
    private final Map<ProcessPannelSlot, CyclicIngredient> tickedIngredientSlots;
    private final Map<ProcessPannelSlot, CyclicIngredient> tickedResultSlots;
    private final Map<ProcessPannelSlot, FastInvItem> staticItems;

    /**
     * Create a ProcessPanel
     * 
     * @param recipe the recipe to render
     * @param actions the action interface for navigation
     * @param context the global GUI context
     */
    public AbstractProcessPanel(Recipe recipe, RecipeGuiActions actions, RecipeGuiContext context) {
        this.recipe = recipe;
        this.actions = actions;
        this.context = context;

        this.sharedButtonSlots = buildRecipeGuiButtonMap();
        this.tickedIngredientSlots = buildTickedIngredient();
        this.tickedResultSlots = buildTickedResult();
        this.staticItems = buildStaticItems();
    }

    // Builders

    /**
     * Map the classic button types inside the panel slots
     * @return map of shared button types to panel-relative slots
     */
    protected abstract Map<RecipeGuiSharedButton, ProcessPannelSlot> buildRecipeGuiButtonMap();

    /**
     * Build the ingredient slots that should animate by cycling through multiple options (like RecipeChoice).
     * CyclicIngredient are built once per panel instantiation, and lived during the panel lifecycle. (regenerated on recipe change)
     * @return map of panel-relative slots to ingredient views
     */
    protected abstract Map<ProcessPannelSlot, CyclicIngredient> buildTickedIngredient();
    
    /**
     * Build the result slots that should animate by cycling through multiple options (like RecipeChoice).
     * CyclicIngredient are built once per panel instantiation, and lived during the panel lifecycle. (regenerated on recipe change)
     * <p> On most panels, this will be a single static output slot, but some panels may have multiple result slots. </p>
     * @return map of panel-relative slots to result views
     */
    protected abstract Map<ProcessPannelSlot, CyclicIngredient> buildTickedResult();

    /**
     * Build static decorative items that don't change during recipe lifecycle.
     * These can have custom click actions (e.g., show more info, send link to wiki).
     * @return map of panel-relative slots to static items with optional actions
     */
    protected abstract Map<ProcessPannelSlot, FastInvItem> buildStaticItems();

    // Getters

    /**
     * Get the recipe gui classic button mapping inside the panel slots.
     * @return map of RecipeGuiControlledButton associated there panel-relative slots
     */
    public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap() {
        return sharedButtonSlots;
    }

    /**
     * Get item slots that should animate by cycling through multiple options (like RecipeChoice).
     * CyclicIngredient are built once per panel instantiation, and lived during the panel lifecycle. (regenerated on recipe change)
     * 
     * @return map of panel-relative slots to ingredient views
     */
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient() {
        return tickedIngredientSlots;
    }

    /**
     * Get result slots that should animate by cycling through multiple options (like RecipeChoice).
     * CyclicIngredient are built once per panel instantiation, and lived during the panel lifecycle. (regenerated on recipe change)
     * <p> On most panels, this will be a single static output slot, but some panels may have multiple result slots. </p>
     * @return map of panel-relative slots to result views
     */
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return tickedResultSlots;
    }

    /**
     * Get static decorative items that don't change during recipe lifecycle.
     * These can have custom click actions (e.g., show more info, send link to wiki).
     * 
     * @return map of panel-relative slots to static items with optional actions
     */
    public Map<ProcessPannelSlot, FastInvItem> getStaticItems() {
        return staticItems;
    }
}
