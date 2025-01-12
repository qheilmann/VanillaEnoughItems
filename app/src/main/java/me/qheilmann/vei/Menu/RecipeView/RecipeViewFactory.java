package me.qheilmann.vei.Menu.RecipeView;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.RecipeView.SpecifiqueRecipeView.ShapedRecipeView;

public class RecipeViewFactory {
    
    @SuppressWarnings("unchecked")
    public static <T extends Recipe> IRecipeView<T> createRecipeView(T recipe, IMenu ownerMenu, MenuManager menuManager) {        
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return (IRecipeView<T>) new ShapedRecipeView(ownerMenu, shapedRecipe, menuManager);
        }
        else {
            throw new UnsupportedOperationException("Recipe type not supported yet: " + recipe.getClass().getName());
        }
    }
}
