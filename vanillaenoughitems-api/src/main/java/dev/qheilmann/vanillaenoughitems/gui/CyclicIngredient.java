package dev.qheilmann.vanillaenoughitems.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeChoiceHelper;

import java.util.List;
import java.util.Objects;

/**
 * Manages cycling through multiple ItemStack options for a RecipeChoice.
 * Used to animate ingredient slots that accept multiple items.
 */
@NullMarked
public class CyclicIngredient {

    /**
     * Count value for dependent ingredients (always 0 to catch misuse).
     * If tick methods are accidentally called, division by zero will throw ArithmeticException.
     */
    private static final int DEPENDENT_INGREDIENT_COUNT = 0;

    /**
     * Functional interface for producing ItemStacks based on dependencies.
     * The number of ItemStack arguments matches the number of CyclicIngredient dependencies.
     */
    @FunctionalInterface
    public interface DependentProducer {
        /**
         * Produce an ItemStack based on the current items from dependencies.
         * @param dependencyItems the current items from each dependency (one per dependency)
         * @return the produced ItemStack
         */
        ItemStack produce(ItemStack... dependencyItems);
    }

    // Predefined options
    /** Store of all ItemStacks directly (empty for dependent) */
    private final ItemStack[] options;
    /** Total number of options (0 for dependent ingredients to catch misuse) */
    private final int count;
    /** Whether this ingredient is pinned to a specific item (always false for dependent) */
    private boolean pinned = false;
    /** Current index in the options array, (always 0 for dependent) */
    private int currentIndex;
    
    // Dependent options
    /** The dependencies to read from (null for predefined) */
    private final CyclicIngredient @Nullable [] dependencies;
    /** Function to compute result from dependencies (null for predefined) */
    private final @Nullable DependentProducer dependentProducer;
    
    /**
     * Create an CyclicIngredient from a RecipeChoice.
     * @param seed a seed value to set starting position (same seed = same offset for all items)
     * @param choice the recipe choice to create from
     */
    public CyclicIngredient(int seed, RecipeChoice choice) {
        List<ItemStack> items = RecipeChoiceHelper.getItemsFromChoice(choice).stream().toList();
        this.options = items.toArray(new ItemStack[0]);
        this.count = options.length;
        this.currentIndex = Math.abs(seed) % count;

        // Not used
        this.dependencies = null;
        this.dependentProducer = null;
    }

    /**
     * Create an CyclicIngredient from ItemStacks.
     * @param seed a seed value to set starting position (same seed = same offset for all items)
     * @param item the items to display
     */
    public CyclicIngredient(int seed, ItemStack... item) {
        this(seed, new RecipeChoice.ExactChoice(List.of(item)));
    }

    /**
     * Create a dependent CyclicIngredient that computes its result from other CyclicIngredients.
     * The result is computed dynamically by reading the current state of all dependencies.
     * 
     * <p>The {@code dependentProducer} will receive exactly one ItemStack argument for each
     * dependency (obtained via {@link #getCurrentItem()}), in the same order as the dependencies 
     * are provided.</p>
     * 
     * <p><b>State-based computation:</b> The result always reflects the current combination of
     * dependency items. When any dependency cycles or is pinned, the result automatically updates
     * to match.</p>
     * 
     * <p><b>Important ordering requirement:</b> All dependencies must be ticked/updated BEFORE
     * reading the result. The current RecipeGui implementation
     * tick first ingredient, the others and then result.</p>
     * 
     * <p><b>Tick operations:</b> Calling {@link #tickForward()}, {@link #tickBackward()}, or 
     * {@link #setIndex(int)} on dependent ingredients is safe but has no effect (no-op). 
     * The result automatically updates when dependencies change.</p>
     * 
     * <p><b>Limitations:</b> Dependent ingredients do not support {@link #contains(ItemStack)} 
     * or {@link #pin(ItemStack)} operations due to their dynamic nature.</p>
     * 
     * @param dependentProducer function that receives one ItemStack per dependency and produces a result
     * @param dependencies the CyclicIngredients this one depends on (order matters)
     */
    public CyclicIngredient(DependentProducer dependentProducer, CyclicIngredient... dependencies) {
        if (dependencies.length == 0) {
            throw new IllegalArgumentException("Dependent CyclicIngredient must have at least one dependency");
        }
        
        this.dependencies = dependencies.clone();
        this.dependentProducer = dependentProducer;
        
        // Dependent ingredients don't use predefined options
        this.count = DEPENDENT_INGREDIENT_COUNT;
        this.options = new ItemStack[0];
        this.currentIndex = 0;
    }

    /**
     * Get the currently displayed ItemStack.
     * <p>For predefined ingredients: returns the item at currentIndex.</p>
     * <p>For dependent ingredients: computes result from current state of all dependencies.</p>
     * @return the current item
     */
    public ItemStack getCurrentItem() {
        // For dependent ingredients, read current state of dependencies
        CyclicIngredient[] deps = dependencies;
        DependentProducer depProducer = dependentProducer;
        if (deps != null && depProducer != null) {
            // Read the current item from each dependency and pass to producer
            ItemStack[] dependencyItems = new ItemStack[deps.length];
            for (int i = 0; i < deps.length; i++) {
                dependencyItems[i] = deps[i].getCurrentItem();
            }
            
            return depProducer.produce(dependencyItems);
        }
        
        // For predefined ingredients, access the options array at current index
        int normalizedIndex = currentIndex % count;
        return options[normalizedIndex];
    }

    /**
     * Advance to the next item in the cycle.
     * <p><b>Note:</b> For pinned or dependent ingredients, this is a no-op</p>
     * @return the new current item
     */
    public ItemStack tickForward() {
        setIndex(currentIndex + 1);
        return getCurrentItem();
    }

    /**
     * Go back to the previous item in the cycle.
     * <p><b>Note:</b> For pinned or dependent ingredients, this is a no-op</p>
     * @return the new current item
     */
    public ItemStack tickBackward() {
        setIndex(currentIndex - 1 + count);
        return getCurrentItem();
    }

    /**
     * Tick this ingredient and all its dependencies recursively.
     * <p>For dependent ingredients: ticks all dependencies first, then returns computed result.</p>
     * <p>For regular ingredients: same as {@link #tickForward()}.</p>
     * <p><b>Use this in contexts where dependencies need to be ticked together.</b></p>
     * <p><b>Do NOT use this in where ingredients are already ticked separately.</b></p>
     * @return the new current item
     */
    public ItemStack tickWithDependencies() {
        // For dependent ingredients, recursively tick all dependencies first
        if (dependencies != null) {
            for (CyclicIngredient dep : dependencies) {
                dep.tickWithDependencies();
            }
            return getCurrentItem();
        }
        
        // For regular ingredients, just tick forward
        return tickForward();
    }

    /**
     * Check if this view has multiple options.
     * <p><b>Note:</b> Dependent ingredients always return true since they are dynamic. </p>
     * @return true if multiple options exist, false otherwise
     */
    public boolean hasMultipleOptions() {
        if (isDependent()) {
            return true;
        }

        return count > 1;
    }

    /**
     * Check if the CyclicIngredient contains a specific ItemStack.
     * <p><b>Note:</b> This method only works for regular (non-dependent) ingredients.
     * For dependent ingredients, this will always return false due to potentially 
     * large option sets that cannot be efficiently checked.</p>
     * 
     * @param item the item to check
     * @return true if the item is in the options, false for dependent ingredients or if not found
     */
    public boolean contains(ItemStack item) {
        // Dependent ingredients don't support containment checks
        if (isDependent()) {
            return false;
        }
        
        for (ItemStack option : options) {
            if (option.isSimilar(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pin the current index to a specific item if it exists in the options.
     * <p><b>Note:</b> This method only works for regular (non-dependent) ingredients.
     * Dependent ingredients cannot be pinned due to their dynamic nature and 
     * potentially large option sets.</p>
     * 
     * @param item the item to pin to
     * @throws UnsupportedOperationException if called on a dependent ingredient
     * @throws IllegalArgumentException if the item is not found in the options
     */
    public void pin(ItemStack item) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSimilar(item)) {
                currentIndex = i;
                pinned = true;
                return;
            }
        }

        throw new IllegalArgumentException("Item not found in CyclicIngredient options");
    }

    /**
     * Set the current index to a specific option, wrapping if necessary.
     * <p>Does nothing if pinned or if this is a dependent ingredient.</p>
     * @param index the index to set
     */
    public void setIndex(int index) {
        if (pinned || isDependent()) {
            return; // No-op for pinned or dependent ingredients
        }

        currentIndex = index % count;
    }

    /**
     * Get the total number of options in this CyclicIngredient
     * @return the option count
     */
    public int getOptionCount() {
        return count;
    }

    /**
     * Check if this CyclicIngredient is dependent on other ingredients
     * @return true if this is a dependent ingredient, false otherwise
     */
    public boolean isDependent() {
        return dependencies != null;
    }
    
    /**
     * Get all ItemStack options for this CyclicIngredient.
     * <p><b>Note:</b> This method only works for regular (non-dependent) ingredients.
     * Dependent ingredients cannot enumerate all possibilities.</p>
     * 
     * @return array of all ItemStack options
     * @throws UnsupportedOperationException if called on a dependent ingredient
     */
    public ItemStack[] getOptions() {
        if (isDependent()) {
            throw new UnsupportedOperationException("Cannot get options from a dependent CyclicIngredient");
        }
        
        return options.clone();
    }

    @Override
    public boolean equals(@SuppressWarnings("null") Object obj) {
        return equals(obj, true);
    }

    /**
     * Compare this CyclicIngredient to another for equality.
     * @param obj the other object to compare
     * @param exact if true, compares pinned state and current index; if false, ignores them
     * @return true if equal based on the specified criteria
     */
    public boolean equals(Object obj, boolean exact) {
        if (this == obj) return true;
        if (!(obj instanceof CyclicIngredient other)) return false;

        // Compare predefined options
        if (this.count != other.count) return false;
        for (int i = 0; i < this.count; i++) {
            if (!this.options[i].equals(other.options[i])) {
                return false;
            }
        }

        // Compare pinned state and current index only if exact
        if (exact) {
            if (this.pinned != other.pinned) return false;
            if (this.currentIndex != other.currentIndex) return false;
        }

        // Compare dependencies for dependent ingredients
        if (this.isDependent() != other.isDependent()) return false;
        if (this.isDependent()) {
            CyclicIngredient[] deps = Objects.requireNonNull(this.dependencies);
            CyclicIngredient[] otherDeps = Objects.requireNonNull(other.dependencies);
            if (deps.length != otherDeps.length) return false;
            for (int i = 0; i < deps.length; i++) {
                if (!deps[i].equals(otherDeps[i], exact)) {
                    return false;
                }
            }
        }

        return true;
    }
}
