package me.qheilmann.vei.Menu.RecipeView;

import java.util.Collection;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public interface IRecipeView<T extends Recipe> {
    void setRecipe(@NotNull T recipe);
    void clearRecipe();
    void refreshView();
    RecipeViewContainer getRecipeContainer();
    @NotNull Collection<ItemStack> getResults();
    @NotNull Collection<RecipeChoice> getIngredients();
}
