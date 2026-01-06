package dev.qheilmann.vanillaenoughitems.metrics;

import java.util.HashMap;
import java.util.Map;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;

/**
 * Handles bStats metrics collection for VanillaEnoughItems
 */
@NullMarked
public final class BStatsMetrics {
    
    private static final int BSTATS_PLUGIN_ID = 28740;

    private BStatsMetrics() {} // Prevent instantiation
    
    /**
     * Initialize and register bStats metrics for the plugin.
     * Should only be called once during plugin initialization.
     * 
     * @param plugin the plugin instance
     * @param recipeIndex the recipe index to collect metrics from
     * @return the initialized Metrics instance
     */
    public static void initialize(JavaPlugin plugin, RecipeIndex recipeIndex) {
        Metrics metrics = new Metrics(plugin, BSTATS_PLUGIN_ID);
        
        registerRecipeCountChart(metrics, recipeIndex);
        registerProcessCountChart(metrics, recipeIndex);
    }
    
    /**
     * Register the recipe count distribution chart (DrilldownPie)
     * Shows recipe count in ranges with exact counts as subcategories
     */
    private static void registerRecipeCountChart(Metrics metrics, RecipeIndex recipeIndex) {
        metrics.addCustomChart(new DrilldownPie("recipe_count", () -> {
            // Format: Map<Category, Map<Subcategory, Count>>
            Map<String, Map<String, Integer>> data = new HashMap<>();
            int totalRecipes = recipeIndex.getAllRecipesById().size();
            
            // Determine the range category
            String category;
            if (totalRecipes < 50) {
                category = "0-30 Very few";
            } else if (totalRecipes < 200) {
                category = "30-200 Few";
            } else if (totalRecipes < 500) {
                category = "200-500 Small";
            } else if (totalRecipes < 1000) {
                category = "500-1000 Medium";
            } else if (totalRecipes < 1800) {
                category = "1000-1800 Large (Vanilla)";
            } else {
                category = "1800+ Very large (Modded)";
            }
            
            Map<String, Integer> subCategory = new HashMap<>();
            subCategory.put(String.valueOf(totalRecipes), 1);
            data.put(category, subCategory);
            
            return data;
        }));
    }
    
    /**
     * Register the process count chart (SimplePie)
     * Shows the number of different recipe processes
     */
    private static void registerProcessCountChart(Metrics metrics, RecipeIndex recipeIndex) {
        metrics.addCustomChart(new SimplePie("process_count", () -> 
            String.valueOf(recipeIndex.getAllRecipesByProcess().size())
        ));
    }
}
