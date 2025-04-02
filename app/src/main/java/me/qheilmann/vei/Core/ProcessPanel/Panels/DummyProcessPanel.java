package me.qheilmann.vei.Core.ProcessPanel.Panels;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanelSlot;
import me.qheilmann.vei.Core.Recipe.Index.ProcessRecipeSet;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.Core.Style.Styles.Style;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class DummyProcessPanel extends ProcessPanel<Recipe> {

    private static final ProcessPanelSlot INFORMATIVE_ITEM_SLOT = new ProcessPanelSlot(3, 2);
    private static final SlotSequence<ProcessPanelSlot> DUMMY = new SlotSequence<>(List.of(INFORMATIVE_ITEM_SLOT));

    private static final Material INFORMATIVE_ITEM_MATERIAL = Material.BARRIER;

    public DummyProcessPanel(@NotNull Style style, @NotNull RecipeIndexService recipeIndex, @NotNull ProcessRecipeSet<Recipe> recipes, int variant) {
        super(style, recipeIndex, recipes, variant);
    }

    public DummyProcessPanel(@NotNull Style style, @NotNull RecipeIndexService recipeIndex, @NotNull ProcessRecipeSet<Recipe> recipes) {
        super(style, recipeIndex, recipes);
    }

    @Override
    @Nullable
    protected Material getWorkbenchMaterial() {
        return null;
    }

    @Override
    public void cycle(EnumSet<SlotType> slotTypes) {
        // TODO implement the cycle method
        VanillaEnoughItems.LOGGER.info("Simulated cycle method called");
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getIngredientSlots() {
        return DUMMY;
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getResultSlots() {
        return DUMMY;
    }

    @Override
    protected @NotNull SlotSequence<ProcessPanelSlot> getConsumableSlots() {
        return DUMMY;
    }

    @Override
    public void render(EnumSet<AttachedButtonType> buttonsVisibility) {
        
        // Dummy process is a special case, it does need to use the super.render() method
        clear();

        int nbOfRecipe = getVariantCount();
        
        GuiItem<RecipeMenu> informativeItem = new GuiItem<>(INFORMATIVE_ITEM_MATERIAL);
        boolean check = informativeItem.editMeta(meta -> {
            String message = (nbOfRecipe == 1 )
            ? "Sorry there is %d recipe for this item without a recipe view".formatted(nbOfRecipe) 
            : "Sorry there are %d recipes for this item without a recipe view".formatted(nbOfRecipe);
            meta.displayName(Component.text(message, NamedTextColor.RED));
        });
        
        if (!check) {
            VanillaEnoughItems.LOGGER.warn("Failed to edit the meta of the informative item");
        }
        
        recipePanelSlots.put(INFORMATIVE_ITEM_SLOT, informativeItem);
    }
}

