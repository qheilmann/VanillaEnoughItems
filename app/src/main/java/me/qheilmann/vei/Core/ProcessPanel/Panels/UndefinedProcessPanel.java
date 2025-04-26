package me.qheilmann.vei.Core.ProcessPanel.Panels;

import java.util.EnumSet;
import java.util.List;
import java.util.NavigableSet;

import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanel;
import me.qheilmann.vei.Core.ProcessPanel.ProcessPanelSlot;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Core.Slot.Collection.SlotSequence;
import me.qheilmann.vei.Core.Style.Styles.Style;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class UndefinedProcessPanel extends ProcessPanel<Recipe> {

    private static final ProcessPanelSlot INFORMATIVE_ITEM_SLOT = new ProcessPanelSlot(3, 2);
    private static final SlotSequence<ProcessPanelSlot> DUMMY = new SlotSequence<>(List.of(INFORMATIVE_ITEM_SLOT));

    private static final Material INFORMATIVE_ITEM_MATERIAL = Material.BARRIER;

    public UndefinedProcessPanel(@NotNull Style style, @NotNull RecipeIndexService recipeIndex, @NotNull ProcessRecipeReader<Recipe> recipeReader) {
        super(style, recipeIndex, recipeReader);
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
        
        // Undefined process is a special case, it does need to use the super.render() method
        clear();

        NavigableSet<Recipe> recipes = getRecipeVariants();
        int nbOfRecipe = recipes.size();
        
        GuiItem<RecipeMenu> informativeItem = new GuiItem<>(INFORMATIVE_ITEM_MATERIAL);
        boolean check = informativeItem.editMeta(meta -> {
            String message = (nbOfRecipe == 1 )
            ? "Sorry there is %d recipe without a recipe view".formatted(nbOfRecipe) 
            : "Sorry there are %d recipes without a recipe view".formatted(nbOfRecipe);
            meta.displayName(Component.text(message, NamedTextColor.RED));
        });
        
        if (!check) {
            VanillaEnoughItems.LOGGER.warn("Failed to edit the meta of the informative item");
        }

        informativeItem.setAction((event, menu) -> {
            StringBuilder message = new StringBuilder("The item to tell the user about the recipes without a recipe view was clicked\n");
            message.append("The recipes without recipe view are:\n");
            for (Recipe recipe : recipes) {
                if (recipe instanceof Keyed keyedRecipe) {
                    message.append(keyedRecipe.getKey()).append("\n");
                } else {
                    message.append(recipe.getClass().getSimpleName()).append("\n");
                }
            }
            VanillaEnoughItems.LOGGER.info(message.toString());
        });
        
        recipePanelSlots.put(INFORMATIVE_ITEM_SLOT, informativeItem);
    }
}

