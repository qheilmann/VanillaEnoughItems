package dev.qheilmann.vanillaenoughitems.playground.api;

import org.bukkit.plugin.java.JavaPlugin;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;

/**
 * A minimal playground plugin demonstrating basic VanillaEnoughItems API usage.
 */
public class PlaygroundApiPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        // VEI is already fully ready here — our paper-plugin.yml declares it as a BEFORE dependency,
        // so VEI's onEnable() (including indexation) has already completed.
        VanillaEnoughItemsAPI api = VanillaEnoughItemsAPI.get();

        // Get some basic info from the VEI recipe index
        int recipeCount = api.recipeIndex().getAllRecipesByKey().size();
        getComponentLogger().info("There is {} recipes indexed.", recipeCount);

        // Register demo listener (shift+click crafting table to open VEI GUIs)
        getServer().getPluginManager().registerEvents(new RecipeLookupListener(), this);
    }
}
