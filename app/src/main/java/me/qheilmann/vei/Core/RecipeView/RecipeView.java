package me.qheilmann.vei.Core.RecipeView;

import java.util.EnumSet;
import java.util.HashMap;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;



public abstract class RecipeView<T extends Recipe> {
    
    private T recipe;
    protected HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> recipeViewSlots;
    
    public RecipeView(@NotNull T recipe) { // TODO change to WorkbenchRecipeSet with variante
        this(recipe, 0);
    }

    public RecipeView(@NotNull T recipe, int variante) { // TODO change to WorkbenchRecipeSet with variante
        Preconditions.checkNotNull(recipe, "recipe cannot be null");
        this.recipe = recipe;
        this.recipeViewSlots = new HashMap<>();
        // TODO depending on the recipe type populate the recipeViewSlots
    }

    public void setRecipe(@NotNull T recipe) { // TODO change to WorkbenchRecipeSet with variante
        setRecipe(recipe, 0);
    }

    public void setRecipe(@NotNull T recipe, int variante) { // TODO change to WorkbenchRecipeSet with variante
        Preconditions.checkNotNull(recipe, "recipe cannot be null");
        this.recipe = recipe;
    }

    public T getRecipe() {
        return recipe;
    }

    public abstract HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> getContentView(EnumSet<SlotType> slotTypes);

    public void clear() {
        clear(SlotType.RECIPE);
    }

    public abstract void clear(EnumSet<SlotType> slotTypes);


    
    public abstract void cycle(EnumSet<SlotType> slotTypes);

    public abstract void attachMenuButton(RecipeView.ButtonType buttonType, GuiItem<RecipeMenu> parentButton);

    public enum ButtonType {
        NEXT_RECIPE,
        PREVIOUS_RECIPE,
        FORWARD_RECIPE,
        BACKWARD_RECIPE,
        MOVE_INGREDIENTS
    }

    public enum SlotType {
        INGREDIENTS,
        CONSUMABLES,
        RESULTS,
        BUTTONS,
        WORKBENCH,
        OTHER;
        
        public static EnumSet<SlotType> ALL = EnumSet.allOf(SlotType.class);
        public static EnumSet<SlotType> RECIPE = EnumSet.of(SlotType.INGREDIENTS, SlotType.CONSUMABLES, SlotType.RESULTS);
        // CHANGED, // TODO add changed slot types (how can i save the change the change here and be ablse to track change for other classes modifing the map)
    }

}
