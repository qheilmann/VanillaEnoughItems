package dev.qheilmann.vanillaenoughitems.recipe.extraction;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import net.kyori.adventure.key.Key;

/**
 * Extract any recipe with registered IRecipeExtractor
 */
@NullMarked
public class RecipeExtractor {
    
    // Map of registered extractors ordered by insertion order
    LinkedHashSet<IRecipeExtractor<?>> extractors = new LinkedHashSet<>();

    boolean locked = false;

    /**
     * Create a new RecipeExtractor
     */
    public RecipeExtractor() {}

    /**
     * Register a recipe extractor
     * @param extractor the extractor to register
     */
    public void registerExtractor(IRecipeExtractor<?> extractor) {
        if (locked) {
            throw new IllegalStateException("RecipeExtractor is locked, cannot register new extractors");
        }
        this.extractors.add(extractor);
    }

    // TODO actualy extractor is used is in lot of place, and to avoid some issue we lock the registration after all the registration is done
    // but this is not very clean, is it a better way to handle this ?
    // 1. do a sub interface with just the extraction thing so we cast it after finalization
    // 2. have a builder that handle the registration and then build a locked extractor
    // 3. for a temporary time just a lock method that prevent further registration

    /**
     * Lock the extractor to prevent further registrations
     */
    public void lock() {
        this.locked = true;
    }

    /**
     * Check if any registered extractor can handle the given recipe
     * @param recipe the recipe to check
     * @return true if any extractor can handle the recipe, false otherwise
     */
    public boolean canHandle(Recipe recipe) {
        for (IRecipeExtractor<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                return true;
            }
        }
        return false;
    }

    public Key extractKey(Recipe recipe) {
        for (IRecipeExtractor<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                IRecipeExtractor<@NonNull Recipe> typedExtractor = (IRecipeExtractor<@NonNull Recipe>) extractor;
                return typedExtractor.extractKey(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }

    /**
     * Extract the ingredients from the given recipe with the first extractor (ordered by extractor key {@link Key#comparator()}
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the ingredients, or an empty set if no extractor could handle the recipe
     * @throws IllegalArgumentException if no extractor could handle the recipe, first check with {@link #canHandle(Recipe)} if needed
     */
    public Set<ItemStack> extractIngredients(Recipe recipe) {
        for (IRecipeExtractor<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                IRecipeExtractor<@NonNull Recipe> typedExtractor = (IRecipeExtractor<@NonNull Recipe>) extractor;
                return typedExtractor.extractIngredients(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }

    /**
     * Extract the results from the given recipe with the first extractor (ordered by extractor key {@link Key#comparator()}
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing the results
     * @throws IllegalArgumentException if no extractor could handle the recipe, first check with {@link #canHandle(Recipe)} if needed
     */
    public Set<ItemStack> extractResults(Recipe recipe) {
        for (IRecipeExtractor<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                IRecipeExtractor<@NonNull Recipe> typedExtractor = (IRecipeExtractor<@NonNull Recipe>) extractor;
                return typedExtractor.extractResults(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }

    /**
     * Extract other relevant items (e.g. combustion fuel in a furnace recipe) from the given recipe with the first extractor (ordered by extractor key {@link Key#comparator()}
     * @param recipe the recipe to extract from
     * @return a set of ItemStacks representing other relevant items
     * @throws IllegalArgumentException if no extractor could handle the recipe, first check with {@link #canHandle(Recipe)} if needed
     */
    public Set<ItemStack> extractOthers(Recipe recipe) {
        for (IRecipeExtractor<?> extractor : extractors) {
            if (extractor.canHandle(recipe)) {
                @SuppressWarnings("unchecked")
                IRecipeExtractor<@NonNull Recipe> typedExtractor = (IRecipeExtractor<@NonNull Recipe>) extractor;
                return typedExtractor.extractOthers(recipe);
            }
        }
        throw new IllegalArgumentException("No extractor found for recipe: " + recipe.getClass().getSimpleName());
    }
}
