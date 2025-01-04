package me.qheilmann.vei.Menu.RecipeView;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import me.qheilmann.vei.Menu.RecipeView.SpecifiqueRecipeView.ShapedRecipeView;
import me.qheilmann.vei.foundation.gui.GuiItemService;

public class RecipeViewFactory {
    
    @SuppressWarnings("unchecked")
    public static <T extends Recipe> IRecipeView<T> createRecipeView(T recipe) {
        GuiItemService guiItemService = new GuiItemService();

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return (IRecipeView<T>) new ShapedRecipeView(guiItemService, shapedRecipe);
        }
        else {
            throw new UnsupportedOperationException("Recipe type not supported yet: " + recipe.getClass().getName());
        }
    }
}
