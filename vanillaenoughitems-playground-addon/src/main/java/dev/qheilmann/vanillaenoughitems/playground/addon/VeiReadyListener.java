package dev.qheilmann.vanillaenoughitems.playground.addon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import dev.qheilmann.vanillaenoughitems.api.event.VeiReadyEvent;
import dev.qheilmann.vanillaenoughitems.playground.addon.beaconbeam.BeaconBeamRecipe;
import dev.qheilmann.vanillaenoughitems.playground.addon.beaconbeam.BeaconBeamTransform;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.RecipeIndexView;
import net.kyori.adventure.key.Key;

/**
 * Listener for VEI ready event.
 * <p>
 * This listener handles tasks that must occur AFTER VEI has completed its initialization
 */
@NullMarked
public class VeiReadyListener implements Listener {

    private final JavaPlugin plugin;

    public VeiReadyListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called by VEI when it is fully ready and has completed its initialization.
     */
    @EventHandler
    public void onVeiReady(VeiReadyEvent event) {
        plugin.getLogger().info("Received VEI ready event!");
        
        VanillaEnoughItemsAPI api = event.getApi();
        RecipeIndexView recipeIndex = api.recipeIndex();

        // --------------- //
        // DEMO 4 (Part 2) //
        // --------------- //

        // Initialize the game mechanic that handles the actual item transformation
        // Of cours we also need to create the actual recipes system for our new recipe type and register some example recipes
        BeaconBeamTransform beaconBeamTransform = new BeaconBeamTransform();
        plugin.getServer().getPluginManager().registerEvents(beaconBeamTransform, plugin);

        // Create example beacon beam recipes
        BeaconBeamRecipe copperToGold = new BeaconBeamRecipe(
            Key.key(PlaygroundAddonPlugin.NAMESPACE, "copper_to_gold"), 
            ItemStack.of(org.bukkit.Material.COPPER_INGOT), 
            ItemStack.of(org.bukkit.Material.GOLD_INGOT)
        );
        beaconBeamTransform.register(copperToGold);

        BeaconBeamRecipe ironToGold = new BeaconBeamRecipe(
            Key.key(PlaygroundAddonPlugin.NAMESPACE, "iron_to_gold"), 
            ItemStack.of(org.bukkit.Material.IRON_INGOT), 
            ItemStack.of(org.bukkit.Material.GOLD_INGOT)
        );
        beaconBeamTransform.register(ironToGold);

        BeaconBeamRecipe coalToDiamond = new BeaconBeamRecipe(
            Key.key(PlaygroundAddonPlugin.NAMESPACE, "coal_to_diamond"), 
            ItemStack.of(org.bukkit.Material.COAL), 
            ItemStack.of(org.bukkit.Material.DIAMOND)
        );
        beaconBeamTransform.register(coalToDiamond);
      
        // Index our custom recipes in VEI
        // Unlike vanilla recipes that are initialy auto-discovered, custom recipes must be manually indexed.
        // This tells VEI about our recipes so they appear in searches and the recipe GUI.
        recipeIndex.indexRecipe(copperToGold);
        recipeIndex.indexRecipe(ironToGold);
        recipeIndex.indexRecipe(coalToDiamond);
    }
}
