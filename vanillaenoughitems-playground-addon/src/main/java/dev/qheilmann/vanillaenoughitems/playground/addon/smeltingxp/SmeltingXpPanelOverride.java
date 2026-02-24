package dev.qheilmann.vanillaenoughitems.playground.addon.smeltingxp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.config.Style;
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
 * Custom smelting panel that replaces the built-in one.
 * Adds XP and cook time information as extra static items.
 * <p>
 * Demonstrates how an addon can override a built-in {@link ProcessPanel}
 * by registering a custom {@link dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelFactory}
 * for an existing process.
 * <p>
 * Panel layout (7 columns × 5 rows, 0-indexed):
 * <pre>
 *   col: 0       1       2       3       4       5       6
 * row 0: ·     [prev]    ·     [next]    ·       ·       ·
 * row 1: ·     [INPUT]   .       ·       ·      [XP]     ·
 * row 2: ·     [FIRE]    .     [ARROW]   ·    [OUTPUT]   ·
 * row 3: ·     [FUEL]    .       ·       ·     [TIME]    ·
 * row 4: ·     [h.bk]    ·     [h.fw]    ·       .       ·
 * </pre>
 */
@NullMarked
public class SmeltingXpPanelOverride implements ProcessPanel {

    // -- Slot positions (column, row) within the panel area --
    private static final ProcessPannelSlot INPUT_SLOT  = new ProcessPannelSlot(1, 1);
    private static final ProcessPannelSlot FUEL_SLOT   = new ProcessPannelSlot(1, 3);
    private static final ProcessPannelSlot OUTPUT_SLOT = new ProcessPannelSlot(5, 2);
    private static final ProcessPannelSlot XP_SLOT     = new ProcessPannelSlot(5, 1);  // Custom: XP info
    private static final ProcessPannelSlot COOK_TIME_SLOT = new ProcessPannelSlot(5, 3);  // Custom: Cook time info

    private static final ProcessPannelSlot DECORATION_FIRE_SLOT = new ProcessPannelSlot(1, 2);
    private static final ProcessPannelSlot DECORATION_PROGRESS_SLOT = new ProcessPannelSlot(3, 2);
    private static final ProcessPannelSlot BACKGROUND_SLOT = new ProcessPannelSlot(0, 0);

    // Custom fuel items for this custom smelting panel
    private static final ItemStack[] CUSTOM_FUELS = {
        new ItemStack(Material.LAVA_BUCKET),
        new ItemStack(Material.BLAZE_ROD),
    };

    private final FurnaceRecipe recipe;
    private final Style style;
    private final int seed;

    public SmeltingXpPanelOverride(Recipe recipe, Style style) {
        this.recipe = (FurnaceRecipe) recipe;
        this.style = style;
        this.seed = new Random().nextInt();
    }

    @Override
    public Map<RecipeGuiSharedButton, ProcessPannelSlot> getRecipeGuiButtonMap() {
        return ProcessPannelSlot.defaultSharedButtonMap();  // Use default button layout
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedIngredient() {
        return Map.of(INPUT_SLOT, new CyclicIngredient(seed, recipe.getInputChoice()));
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedResults() {
        return Map.of(OUTPUT_SLOT, new CyclicIngredient(seed, recipe.getResult()));
    }

    @Override
    public Map<ProcessPannelSlot, CyclicIngredient> getTickedOther() {
        return Map.of(FUEL_SLOT, new CyclicIngredient(seed, CUSTOM_FUELS));
    }

    @Override
    public Map<ProcessPannelSlot, PanelStaticItem> getStaticItems() {
        Map<ProcessPannelSlot, PanelStaticItem> statics = new HashMap<>();

        ItemStack backgroundItem = GuiComponentHelper.createFillerItem(false);
        ItemStack progressItem = GuiComponentHelper.createFillerItem(false);
        ItemStack furnaceItem = ItemType.FURNACE.createItemStack(meta -> {
            meta.setMaxStackSize(1);
            meta.setHideTooltip(true);
        });

        if (style.hasResourcePack()) {
            // Note: We use hardcoded VEI models for this demo.
            // In production, addons should provide their own models via a resource pack.
            backgroundItem.editMeta(meta -> meta.setItemModel(VeiKey.namespacedKey("gui/background/panel/smelting")));
            progressItem.editMeta(meta -> meta.setItemModel(VeiKey.namespacedKey("gui/decoration/recipe_progress")));
            furnaceItem.editMeta(meta -> meta.setItemModel(VeiKey.namespacedKey("gui/decoration/cooking_flame")));
        }

        statics.put(BACKGROUND_SLOT, new PanelStaticItem(backgroundItem, null));
        statics.put(DECORATION_PROGRESS_SLOT, new PanelStaticItem(progressItem, null));
        statics.put(DECORATION_FIRE_SLOT, new PanelStaticItem(furnaceItem, null));

        // XP info — shows experience gained from this recipe
        statics.put(XP_SLOT, new PanelStaticItem(createXpInfoItem(), null));

        // Cook time info — shows how long this recipe takes to cook
        statics.put(COOK_TIME_SLOT, new PanelStaticItem(createCookTimeItem(), null));
        
        return Map.copyOf(statics);
    }

    // -- Helpers --

    private ItemStack createXpInfoItem() {
        float xp = recipe.getExperience();
        ItemStack item = ItemType.EXPERIENCE_BOTTLE.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Experience", style.colorPrimary())
                .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text(xp + " XP", style.colorSecondary())
                    .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }

    private ItemStack createCookTimeItem() {
        int ticks = recipe.getCookingTime();
        double seconds = ticks / 20.0;
        ItemStack item = ItemType.CLOCK.createItemStack();
        item.editMeta(meta -> {
            meta.displayName(Component.text("Cook Time", style.colorPrimary())
                .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                Component.text(seconds + "s (" + ticks + " ticks)", style.colorSecondary())
                    .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return item;
    }
}
