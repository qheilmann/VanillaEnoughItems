package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.IngredientView;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiActions;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.gui.SharedButtonType;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;

/**
 * Abstract base class for process-specific recipe panel renderers.
 * Each implementation handles rendering a specific type of process (e.g., Crafting, Smelting).
 * Panels are stateful but regenerated on recipe change like nextRecipe().
 */
@NullMarked
public abstract class AbstractProcessPanel {
    protected final Recipe recipe;
    protected final RecipeGuiActions actions;
    protected final RecipeGuiContext context;
    protected final List<IngredientView> ingredientViews = new ArrayList<>();

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
    }

    /**
     * Render the recipe into the panel area.
     * Must return shared buttons unmodified at appropriate slots.
     * 
     * @param sharedButtons pre-built buttons that must be included in the result
     * @return map of panel-relative slots to items
     */
    public abstract Map<ProcessPannelSlot, FastInvItem> renderRecipe(Map<SharedButtonType, FastInvItem> sharedButtons);

    /**
     * Tick all ingredient views and return updated slots.
     * Called periodically to animate RecipeChoice cycling.
     * 
     * @return map of panel-relative slots to updated item stacks, or empty if no changes
     */
    public Map<ProcessPannelSlot, ItemStack> tickIngredients() {
        // Default implementation: no animation
        return Map.of();
    }

    /**
     * Get the recipe this panel is rendering
     * @return the recipe
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Get all ingredient views managed by this panel
     * @return list of ingredient views
     */
    protected List<IngredientView> getIngredientViews() {
        return ingredientViews;
    }
}

