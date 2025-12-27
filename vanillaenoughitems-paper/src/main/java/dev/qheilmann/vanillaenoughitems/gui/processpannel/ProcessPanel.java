package dev.qheilmann.vanillaenoughitems.gui.processpannel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.utils.VeiKey;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
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


    // Implementation for Undefined Process Panel
    
    public class UndefinedProcessPanel implements ProcessPanel {
        private static final ProcessPannelSlot DESCRIPTION_SLOT = new ProcessPannelSlot(3, 2);

        private final Recipe recipe;
        private final Style style;
        private final RecipeExtractor extractor;
        private final @Nullable Process unhandledProcess;

        public UndefinedProcessPanel(Recipe recipe, Style style, RecipeExtractor extractor, @Nullable Process unhandledProcess) {
            this.recipe = recipe;
            this.style = style;
            this.extractor = extractor;
            this.unhandledProcess = unhandledProcess;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap() {
            return ProcessPannelSlot.defaultSharedButtonMap();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient() {
            return Collections.emptyMap();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
            return Collections.emptyMap();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<ProcessPannelSlot, FastInvItem> getStaticItems() {
            Map<ProcessPannelSlot, FastInvItem> statics = new HashMap<>();

            Key recipeId;
            if (extractor.canHandle(recipe)) {
                recipeId = extractor.extractKey(recipe);
            } else if (recipe instanceof Keyed keyed) {
                recipeId = keyed.key();
            } else {
                int hashcode = recipe.hashCode();
                recipeId = VeiKey.key("unknown_recipe_" + hashcode);
            }

            String processId = (unhandledProcess != null) ? unhandledProcess.key().asString() : "Unknown Process";

            ItemStack descriptionItem = ItemType.BARRIER.createItemStack();
            descriptionItem.editMeta(meta -> {
                meta.displayName(Component.empty()); // [Translation]
                meta.lore(List.of(
                    Component.text("This recipe cannot be displayed.", style.colorPrimary()).decoration(TextDecoration.ITALIC, false), // [Translation]
                    Component.text("Recipe ID: " + recipeId.asString(), style.colorSecondary()).decoration(TextDecoration.ITALIC, false),
                    Component.text("Process ID: " + processId, style.colorSecondary()).decoration(TextDecoration.ITALIC, false)
                ));
            });

            statics.put(DESCRIPTION_SLOT, new FastInvItem(descriptionItem, null));

            return statics;
        }
    }
}
