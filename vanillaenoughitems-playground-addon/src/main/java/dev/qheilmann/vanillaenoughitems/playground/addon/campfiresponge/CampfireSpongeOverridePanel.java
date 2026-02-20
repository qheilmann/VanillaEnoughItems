package dev.qheilmann.vanillaenoughitems.playground.addon.campfiresponge;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.helper.GuiComponentHelper;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.PanelStaticItem;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGuiSharedButton;
import dev.qheilmann.vanillaenoughitems.utils.VeiKey;

/**
 * Custom campfire panel that replaces the built-in one.
 * For demo purposes, it ignores the actual recipe ingredients and results and shows sponge instead.
 * <p>
 * Demonstrates how an addon can override a built-in {@link ProcessPanel}
 * by registering a custom {@link dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelFactory}
 * for an existing process.
 * <p>
 * Panel layout (7 columns × 5 rows, 0-indexed):
 * <pre>
 *   col: 0       1       2       3       4       5       6
 * row 0: ·     [prev]    ·     [next]    ·       ·       ·
 * row 1: ·       ·     [INPUT]   ·       ·       .       ·
 * row 2: ·       ·     [FIRE]  [ARROW]   ·    [OUTPUT]   ·
 * row 3: ·       ·       .       ·       ·       .       ·
 * row 4: ·     [h.bk]    ·     [h.fw]    ·       .       ·
 * </pre>
 */
@NullMarked
public class CampfireSpongeOverridePanel implements ProcessPanel {

    // -- Slot positions (column, row) within the panel area --
    private static final ProcessPannelSlot INPUT_SLOT  = new ProcessPannelSlot(2, 1);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot DECORATION_FIRE_SLOT = new ProcessPannelSlot(1, 2);
    private static final ProcessPannelSlot DECORATION_PROGRESS_SLOT = new ProcessPannelSlot(3, 2);
    private static final ProcessPannelSlot BACKGROUND_SLOT = new ProcessPannelSlot(0, 0);

    @SuppressWarnings("unused")
    private final CampfireRecipe recipe;
    private final Style style;
    private final int cyclicIngredientSeed;

    public CampfireSpongeOverridePanel(Recipe recipe, Style style) {
        this.recipe = (CampfireRecipe) recipe;
        this.style = style;
        this.cyclicIngredientSeed = new Random().nextInt();
    }

    @Override
    public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();  // Use default button layout
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient() {
        ItemStack ingredient = ItemStack.of(Material.WET_SPONGE);
        return Map.of(INPUT_SLOT, new CyclicIngredient(cyclicIngredientSeed, ingredient));
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        ItemStack result = ItemStack.of(Material.SPONGE);
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(cyclicIngredientSeed, result));
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedOther() {
        return Map.of();
    }

    @Override
    public Map<ProcessPannelSlot, PanelStaticItem> getStaticItems() {
        Map<ProcessPannelSlot, PanelStaticItem> statics = new HashMap<>();
        
        ItemStack backgroundItem = GuiComponentHelper.createFillerItem(false);
        ItemStack progressItem = GuiComponentHelper.createFillerItem(false);
        ItemStack campfireItem = ItemType.CAMPFIRE.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (style.hasResourcePack()) {
            // Note: We use hardcoded VEI models for this demo.
            // In production, addons should provide their own models via a resource pack.
            backgroundItem.editMeta(meta -> meta.setItemModel(VeiKey.namespacedKey("gui/background/panel/campfire")));
            progressItem.editMeta(meta -> meta.setItemModel(VeiKey.namespacedKey("gui/decoration/recipe_progress")));
            campfireItem.editMeta(meta -> meta.setItemModel(VeiKey.namespacedKey("gui/decoration/cooking_flame")));
        }

        statics.put(BACKGROUND_SLOT, new PanelStaticItem(backgroundItem, null));
        statics.put(DECORATION_PROGRESS_SLOT, new PanelStaticItem(progressItem, null));
        statics.put(DECORATION_FIRE_SLOT, new PanelStaticItem(campfireItem, null));
        
        return Map.copyOf(statics);
    }
}
