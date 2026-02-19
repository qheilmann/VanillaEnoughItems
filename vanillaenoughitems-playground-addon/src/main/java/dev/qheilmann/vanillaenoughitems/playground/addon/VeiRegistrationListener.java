package dev.qheilmann.vanillaenoughitems.playground.addon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import dev.qheilmann.vanillaenoughitems.api.event.VeiRegistrationEvent;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
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

        // For this demo, we will only override vanilla recipe things

        // Unregister the built-in stonecutting extractor
        // WARNING: This will print lot of warning if enabled, because the stonecutting recipe will still be there, but indexation will fail)
        // try -> /craft --all     
        // Then see that stonecutting recipes are missing
        api.recipeExtractorRegistry().unregisterExtractor(Key.key(Key.MINECRAFT_NAMESPACE, "stonecutting"));
        
        // Override the built-in smoking extractor with our custom one
        // This will index spong as result and ingredient for all campfire recipes
        // WARNING: This will not change the actual recipes viewed in the process panels, it only changes what gets indexed.
        // try -> /craft minecraft:sponge usage
        // Then see that all Smooking recipes use sponge technically, but are rendered with their normal ingredients and results in the process panel
        // (because the panel uses the recipe directly, not the index)
        api.recipeExtractorRegistry().registerExtractor(new SmookingExtractorSpongeOverride());

        // TODO add Process type
        



        // Look up the built-in smelting process by its key
        Process smeltingProcess = api.processRegistry().getProcess(Key.key(Key.MINECRAFT_NAMESPACE, "smelting"));
        if (smeltingProcess == null) {
            plugin.getLogger().warning("Smelting process not found — cannot override panel.");
            return;
        }

        // Replace the default smelting panel with our custom one that shows XP and cook time
        api.processPanelRegistry().registerProvider(smeltingProcess, SmeltingPanelOverride::new);
        plugin.getLogger().info("Overridden smelting panel with custom panel (XP + cook time display).");
    }
}
