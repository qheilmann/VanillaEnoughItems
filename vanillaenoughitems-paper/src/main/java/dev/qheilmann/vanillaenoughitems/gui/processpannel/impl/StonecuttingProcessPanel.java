package dev.qheilmann.vanillaenoughitems.gui.processpannel.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiComponent;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.pack.VeiPack;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.PanelStaticItem;

/**
 * Panel for stonecutting recipes.
 */
@NullMarked
public class StonecuttingProcessPanel implements ProcessPanel {
    private static final ProcessPannelSlot INPUT_SLOT = new ProcessPannelSlot(1, 2);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot DECORATION_STONECUTTER_SLOT = new ProcessPannelSlot(3, 2);
    private static final ProcessPannelSlot BACKGROUND_SLOT = new ProcessPannelSlot(0, 0);

    private final Recipe recipe;
    private final Style style;
    private final int seed;

    public StonecuttingProcessPanel(Recipe recipe, Style style) {
        this.recipe = recipe;
        this.style = style;
        this.seed = (int) (Math.random() * Integer.MAX_VALUE);
    }

    private StonecuttingRecipe getStonecuttingRecipe() {
        return (StonecuttingRecipe) recipe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap() {
        Map<RecipeGuiSharedButton, ProcessPannelSlot> shared = new HashMap<>();
        shared.put(RecipeGuiSharedButton.NEXT_RECIPE,      ProcessPannelSlot.DEFAULT_NEXT_RECIPE_SLOT);
        shared.put(RecipeGuiSharedButton.PREVIOUS_RECIPE,  ProcessPannelSlot.DEFAULT_PREVIOUS_RECIPE_SLOT);
        shared.put(RecipeGuiSharedButton.HISTORY_FORWARD,  ProcessPannelSlot.DEFAULT_HISTORY_FORWARD_SLOT);
        shared.put(RecipeGuiSharedButton.HISTORY_BACKWARD, ProcessPannelSlot.DEFAULT_HISTORY_BACKWARD_SLOT);
        // No quick craft for stonecutting
        return Map.copyOf(shared);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient() {
        Map<ProcessPannelSlot, CyclicIngredient> ticked = new HashMap<>();
        ticked.put(INPUT_SLOT, new CyclicIngredient(seed, getStonecuttingRecipe().getInputChoice()));
        return Map.copyOf(ticked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("null")
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(seed, getStonecuttingRecipe().getResult()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedOther() {
        return Map.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ProcessPannelSlot, PanelStaticItem> getStaticItems() {
        Map<ProcessPannelSlot, PanelStaticItem> statics = new HashMap<>();

        ItemStack backgroundItem = RecipeGuiComponent.createFillerItem(false);
        ItemStack stonecutterItem = ItemType.STONECUTTER.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (style.hasResourcePack()) {
            backgroundItem.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Background.Panel.STONECUTTING);
            });
            stonecutterItem.editMeta(meta -> {
                meta.setItemModel(VeiPack.ItemModel.Gui.Decoration.RECIPE_ARROW);
            });
        }
        
        statics.put(BACKGROUND_SLOT, new PanelStaticItem(backgroundItem, null));
        statics.put(DECORATION_STONECUTTER_SLOT, new PanelStaticItem(stonecutterItem, null));

        return statics;
    }
}
