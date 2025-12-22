package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.Map;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Represents a process-specific recipe panel renderer.
 * Implementations are responsible for displaying recipes for a particular process type (e.g., Crafting, Smelting).
 * Panels are stateful and recreated when the recipe changes (such as via nextRecipe()).
 * Note: The panel must support any recipe that the associated process can handle, as determined by {@link Process#canHandleRecipe(Recipe)}.
 */
@NullMarked
public interface ProcessPanel {
       
    /**
     * Get the recipe gui classic button mapped inside the panel slots.
     * @return map of RecipeGuiControlledButton associated there panel-relative slots
     */
    public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap();

    /**
     * Get item slots that should animate by cycling through multiple options (like RecipeChoice).
     * CyclicIngredient are built once per panel instantiation, and lived during the panel lifecycle. (regenerated on recipe change)
     * 
     * @return map of panel-relative slots to ingredient views
     */
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient();

    /**
     * Get result slots that should animate by cycling through multiple options (like RecipeChoice).
     * CyclicIngredient are built once per panel instantiation, and lived during the panel lifecycle. (regenerated on recipe change)
     * <p> On most panels, this will be a single static output slot, but some panels may have multiple result slots. </p>
     * @return map of panel-relative slots to result views
     */
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults();

    /**
     * Get static decorative items that don't change during recipe lifecycle.
     * These can have custom click actions (e.g., show more info, send link to wiki).
     * 
     * @return map of panel-relative slots to static items with optional actions
     */
    public Map<ProcessPannelSlot, FastInvItem> getStaticItems();
}
