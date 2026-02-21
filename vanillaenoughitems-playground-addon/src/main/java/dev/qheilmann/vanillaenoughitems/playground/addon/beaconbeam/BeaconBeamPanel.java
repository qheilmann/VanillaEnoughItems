package dev.qheilmann.vanillaenoughitems.playground.addon.beaconbeam;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * ProcessPanel for DEMO 4: Visual representation of beacon beam recipes.
 * <p>
 * This panel defines how beacon beam recipes are displayed in VEI's recipe GUI.
 * It creates a custom layout showing:
 * - Input item (top left)
 * - Beacon block (center, with decorative base)
 * - Output item (right side)
 * - Instructional tooltip explaining how to use the recipe
 * <p>
 * Panel layout (7 columns × 5 rows):
 * <pre>
 *   col: 0       1       2       3       4       5       6
 * row 0: [background spanning the entire panel area]  
 * row 1: ·     [INPUT]   ·       ·       ·       ·       ·
 * row 2: ·       ·    [BEACON]   ·       ·    [OUTPUT]   ·
 * row 3: ·     [BASE] [BASE]  [BASE]     ·       ·       ·
 * row 4: [prev] ·       ·     [next]     ·       ·       ·
 * </pre>
 * 
 * Note: This demo reuses VEI's campfire panel background to avoid requiring
 * custom art assets. Production addons should provide their own resource pack models.
 */
@NullMarked
public class BeaconBeamPanel implements ProcessPanel {

    // -- Slot positions (column, row) within the panel area --
    private static final ProcessPannelSlot INPUT_SLOT  = new ProcessPannelSlot(1, 1);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot BACKGROUND_SLOT = new ProcessPannelSlot(0, 0);
    private static final ProcessPannelSlot DECORATION_BEACON_SLOT = new ProcessPannelSlot(2, 2);
    private static final Set<ProcessPannelSlot> DECORATION_BEACON_BASE_SLOTS = Set.of(
        new ProcessPannelSlot(1, 3),
        new ProcessPannelSlot(2, 3),
        new ProcessPannelSlot(3, 3)
    );  
        
    private final BeaconBeamRecipe recipe;
    private final Style style;
    private final int cyclicIngredientSeed;

    public BeaconBeamPanel(Recipe recipe, Style style) {
        this.recipe = (BeaconBeamRecipe) recipe;
        this.style = style;
        this.cyclicIngredientSeed = new Random().nextInt();
    }

    @Override
    public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();  // Use default button layout
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient() {
        ItemStack ingredient = recipe.input();
        return Map.of(INPUT_SLOT, new CyclicIngredient(cyclicIngredientSeed, ingredient));
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        ItemStack result = recipe.output();
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(cyclicIngredientSeed, result));
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedOther() {
        return Map.of();
    }

    @Override
    public Map<ProcessPannelSlot, PanelStaticItem> getStaticItems() {
        Map<ProcessPannelSlot, PanelStaticItem> statics = new HashMap<>();
        
        Component info = Component.text("Drop the item over the beacon beam to transform it!", style.colorPrimary()).decoration(TextDecoration.ITALIC, false);

        ItemStack backgroundItem = GuiComponentHelper.createFillerItem(false);
        ItemStack beaconItem = ItemType.BEACON.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.displayName(info);
        });
        ItemStack beaconBaseItem = ItemType.IRON_BLOCK.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.displayName(info.append(Component.text("\n\nRequires an active beacon with at least tier 1", style.colorPrimary()).decoration(TextDecoration.ITALIC, false)));
        });

        if (style.hasResourcePack()) {
            // Note: We use hardcoded VEI models for this demo.
            // In production, addons should provide their own models via a resource pack.
            backgroundItem.editMeta(meta -> meta.setItemModel(VeiKey.namespacedKey("gui/background/panel/campfire"))); // Reuse campfire panel background to not require new art assets for this demo
        }

        statics.put(BACKGROUND_SLOT, new PanelStaticItem(backgroundItem, null));
        statics.put(DECORATION_BEACON_SLOT, new PanelStaticItem(beaconItem, null));
        for (ProcessPannelSlot slot : DECORATION_BEACON_BASE_SLOTS) {
            statics.put(slot, new PanelStaticItem(beaconBaseItem, null));
        }
        
        return Map.copyOf(statics);
    }
}
