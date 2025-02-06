package me.qheilmann.vei.Core.RecipeView;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Core.GUI.GuiItem;
import me.qheilmann.vei.Core.Menu.RecipeMenu;
import me.qheilmann.vei.Menu.RecipeView.RecipeViewContainer;



public abstract class RecipeView<T extends Recipe> {
    
    private T recipe;
    
    public RecipeView(@NotNull T recipe) { // TODO change to WorkbenchRecipeSet with variante
        this(recipe, 0);
    }

    public RecipeView(@NotNull T recipe, int variante) { // TODO change to WorkbenchRecipeSet with variante
        Preconditions.checkNotNull(recipe, "recipe cannot be null");
        this.recipe = recipe;
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

    public abstract void clearRecipeSlot(); // TODO add slot type
    
    public abstract HashMap<RecipeViewSlot, GuiItem<RecipeMenu>> getLastContent(); // TODO add slot type
    
    public abstract void cycle();

    public abstract void attachMenuButton(RecipeViewButtonType buttonType, GuiItem<RecipeMenu> parentButton);

    // TEMP

    // public abstract void setVariante(int variante);

    // public abstract int getVariante();

    public abstract void clearRecipe(); // TODO by clear recipeSlot

    public abstract void refresh();

    public abstract Collection<ItemStack> getResults();

    public abstract Collection<RecipeChoice> getIngredients();

    // TEMPS
    public abstract RecipeViewContainer getRecipeContainer();


}
