package dev.qheilmann.vanillaenoughitems.playground.api;

import org.bukkit.plugin.java.JavaPlugin;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import net.kyori.adventure.key.Key;

/**
 * A minimal playground plugin demonstrating basic VanillaEnoughItems API usage.
 */
public class PlaygroundApiPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        // VEI is already fully ready here — our paper-plugin.yml declares it as a BEFORE dependency,
        // so VEI's onEnable() (including indexation) has already completed.
        VanillaEnoughItemsAPI api = VanillaEnoughItemsAPI.get();

        // ------------------------------------------------ //
        // DEMO 1: Read basic info from the recipe index    //
        // ------------------------------------------------ //
        int recipeCount = api.recipeIndex().getAllRecipesByKey().size();
        getComponentLogger().info("There is {} recipes indexed.", recipeCount);
        // Try: Check console on server start
        // Result: Logs the total number of recipes indexed by VEI


        // ------------------------------------------------ //
        // DEMO 2: Deindex a recipe from the recipe index   //
        // ------------------------------------------------ //
        api.recipeIndex().deindexRecipe(Key.key("minecraft:oak_planks"));
        // Try: /craft oak_planks
        // Result: The oak planks crafting recipe will be missing from VEI's index


        // ------------------------------------------------ //
        // DEMO 3: Open VEI GUIs from player interaction    //
        // ------------------------------------------------ //
        // Shift + Left-Click  crafting table -> recipe lookup for held item
        // Shift + Right-Click crafting table -> usage lookup for held item
        // Shift + Left-Click  with Book on crafting table -> player bookmarks
        // Shift + Right-Click with Book on crafting table -> server bookmarks
        getServer().getPluginManager().registerEvents(new RecipeLookupListener(), this);
    }
}
