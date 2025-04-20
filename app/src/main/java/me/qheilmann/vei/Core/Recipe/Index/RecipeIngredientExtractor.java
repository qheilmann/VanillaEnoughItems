package me.qheilmann.vei.Core.Recipe.Index;

import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for extracting ingredients from recipes.
 */
// TODO Consider refactoring this class into a polymorphic design using interfaces or abstract classes.
@Experimental
public class RecipeIngredientExtractor {

    /**
     * Extracts all ingredients from the given recipe.
     * <p>
     * Note: This method currently does not support complex recipes, and will return an empty set for them.
     *
     * @param recipe The recipe to extract ingredients from.
     * @return A set of ItemStacks representing the ingredients.
     * @throws IllegalArgumentException if the recipe type is not supported.
     */
    @NotNull
    public static Set<ItemStack> getIngredients(@NotNull Recipe recipe) {
        
        // Not supported yet
        if (recipe instanceof ComplexRecipe) {
            return Set.of();
        }

          else if (recipe instanceof CraftingRecipe shapedRecipe) {
            return extractFromCraftingRecipe(shapedRecipe);
        } else if (recipe instanceof CookingRecipe cookingRecipe) {
            return extractFromCookingRecipe(cookingRecipe);
        } else if (recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
            return extractFromStonecuttingRecipe(stonecuttingRecipe);
        } else if (recipe instanceof SmithingRecipe smithingTransformRecipe) {
            return extractFromSmithingRecipe(smithingTransformRecipe);
        } 

        throw new IllegalArgumentException("Unsupported recipe type: " + recipe.getClass().getName());
    }

    private static Set<ItemStack> extractFromCraftingRecipe(CraftingRecipe recipe) {
        Set<ItemStack> ingredients = new HashSet<>();

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            shapedRecipe.getChoiceMap().values().forEach(choice -> 
                ingredients.addAll(choiceToItemStacks(choice).collect(Collectors.toSet())));
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            shapelessRecipe.getChoiceList().forEach(choice -> 
                ingredients.addAll(choiceToItemStacks(choice).collect(Collectors.toSet())));
        } else {
            throw new IllegalArgumentException("Unsupported recipe type for choice extraction: " + recipe.getClass().getName());
        }

        return ingredients;
    }

    private static Set<ItemStack> extractFromCookingRecipe(CookingRecipe<?> recipe) {
        RecipeChoice recipeChoice = recipe.getInputChoice();
        Stream<ItemStack> inputItems = choiceToItemStacks(recipeChoice);
        return inputItems.collect(Collectors.toSet());
    }

    private static Set<ItemStack> extractFromStonecuttingRecipe(StonecuttingRecipe recipe) {
        RecipeChoice recipeChoice = recipe.getInputChoice();
        Stream<ItemStack> inputItems = choiceToItemStacks(recipeChoice);
        return inputItems.collect(Collectors.toSet());
    }

    private static Set<ItemStack> extractFromSmithingRecipe(SmithingRecipe recipe) {
        Set<ItemStack> ingredients = new HashSet<>();

        RecipeChoice base = recipe.getBase();
        ingredients.addAll(choiceToItemStacks(base).collect(Collectors.toSet()));

        RecipeChoice addition = recipe.getAddition();
        ingredients.addAll(choiceToItemStacks(addition).collect(Collectors.toSet()));
        
        if (recipe instanceof SmithingTransformRecipe smithingTransformRecipe) {
            RecipeChoice template = smithingTransformRecipe.getTemplate();
            ingredients.addAll(choiceToItemStacks(template).collect(Collectors.toSet()));
        } else if (recipe instanceof SmithingTrimRecipe smithingTrimRecipe) {
            RecipeChoice template = smithingTrimRecipe.getTemplate();
            ingredients.addAll(choiceToItemStacks(template).collect(Collectors.toSet()));
        } else {
            throw new IllegalArgumentException("Unsupported Smithing recipe type: " + recipe.getClass().getName());
        }

        return ingredients;
    }

    private static Stream<ItemStack> choiceToItemStacks(RecipeChoice choice) {
        if (choice == null) {
            return Stream.empty();
        }

        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return materialChoice.getChoices().stream()
                .map(material -> new ItemStack(material).asOne())
                .filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR);
        } 
        else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return exactChoice.getChoices().stream()
                .map(itemStack -> itemStack.clone().asOne())
                .filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR);
        }

        throw new IllegalArgumentException("Unsupported RecipeChoice type: " + choice.getClass().getName());
    }
}