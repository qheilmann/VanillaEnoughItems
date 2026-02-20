package dev.qheilmann.vanillaenoughitems.playground.addon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import dev.qheilmann.vanillaenoughitems.api.event.VeiRegistrationEvent;
import dev.qheilmann.vanillaenoughitems.config.VanillaEnoughItemsConfig;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.playground.addon.campfiresponge.CampfireSpongeOverrideExtractor;
import dev.qheilmann.vanillaenoughitems.playground.addon.campfiresponge.CampfireSpongeOverridePanel;
import dev.qheilmann.vanillaenoughitems.playground.addon.campfiresponge.CampfireSpongeOverrideProcess;
import dev.qheilmann.vanillaenoughitems.playground.addon.smeltingxp.SmeltingXpPanelOverride;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;
import net.kyori.adventure.key.Key;

/**
 * Listener for VEI registration events.
 * Separated from the main plugin class to avoid class loading issues with Paper's classloader.
 */
@NullMarked
public class VeiRegistrationListener implements Listener {

    private final JavaPlugin plugin;

    public VeiRegistrationListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called by VEI before recipe indexation begins.
     * This is the place to register custom processes, extractors, and panel factories.
     */
    @EventHandler
    public void onVeiRegistration(VeiRegistrationEvent event) {
        plugin.getLogger().info("Received VEI registration event! Registering custom extractors and panels...");
        
        VanillaEnoughItemsAPI api = event.getApi();

        RecipeExtractorRegistry extractorRegistry = api.recipeExtractorRegistry();
        ProcessRegistry processRegistry = api.processRegistry();
        ProcessPanelRegistry panelRegistry = api.processPanelRegistry();

        // -------------------------------------------------------------------------------------------------- //
        // DEMO 1: Remove indexation of stonecutting recipes to show how to remove built-in features of VEI //
        // -------------------------------------------------------------------------------------------------- //
        // Unregister the built-in stonecutting extractor
        extractorRegistry.unregisterExtractor(Key.key("stonecutting"));
        
        // Enable debug logging for unhandled recipes to verify that stonecutting recipes are skipped
        // api.config().setDebugUnhandledRecipesWarning(true); // Enabling this will log a warning for every stonecutting recipe

        // Try: /craft --all
        // Result: Stonecutting recipes will be missing from the recipe index (silently skipped during indexation)
        // Note: Recipes without extractors are skipped gracefully - no warnings will be logged


        // ----------------------------------------------------------------------------------------- //
        // DEMO 2: Modify the built-in smelting process panel to show XP and cook time in the GUI  //
        // ----------------------------------------------------------------------------------------- //
        // We register a new ProcessPanelProvider for the existing built-in smelting process.
        // Our new ProcessPanelProvider will override the default one provided by VEI.

        // Step 1: Get the built-in smelting process by its key
        Process smeltingProcess = api.processRegistry().getProcess(Key.key(Key.MINECRAFT_NAMESPACE, "smelting"));
        if (smeltingProcess == null) {
            throw new IllegalStateException("Built-in smelting process not found!");  // Should never happen in this demo
        }
        // Step 2: Replace the default smelting panel with our custom one that shows XP and cook time
        panelRegistry.registerProvider(smeltingProcess, SmeltingXpPanelOverride::new);

        // Try: /craft glass
        // Result: The smelting panel now displays XP and cook time information as extra static items


        // -------------------------------------------------------------------------- //
        // DEMO 3: Override campfire recipe indexation and visualization with sponges //
        // -------------------------------------------------------------------------- //
        // This demo shows the complete workflow of overriding both recipe indexation AND visualization.
        //
        // We will make all campfire recipes appear to use wet sponge as input and sponge as output,
        // completely replacing the actual recipe ingredients and results in VEI's system.
        //
        // Step 1: Register a custom recipe extractor that indexes campfire recipes with dummy ingredients
        extractorRegistry.registerExtractor(new CampfireSpongeOverrideExtractor());
        // This extractor controls recipe indexation and lookup. It affects what shows up in /craft searches
        // and what recipes are found when navigating in the recipe GUI.
        
        // Step 2: Register a custom process and panel provider for the visual display
        CampfireSpongeOverrideProcess campfireProcess = new CampfireSpongeOverrideProcess(api);
        processRegistry.registerProcess(campfireProcess);
        panelRegistry.registerProvider(campfireProcess, CampfireSpongeOverridePanel::new);
        // This panel provider controls only the GUI visualization. It doesn't affect indexation.
        
        // Key Insight: RecipeExtractor and ProcessPanelProvider are independent but often used together.
        // You can use RecipeExtractor alone to change indexation without changing the display,
        // or use ProcessPanelProvider alone to add extra information to the display without changing indexation.
        
        // Try: /craft sponge
        // Result: All campfire recipes now show sponge as ingredient and result, and are indexed under sponge.
        // Note: The actual server recipes remain unchanged - we only modify how VEI sees and displays them.
    }
}
