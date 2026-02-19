package dev.qheilmann.vanillaenoughitems.playground.addon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import dev.qheilmann.vanillaenoughitems.api.event.VeiRegistrationEvent;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import net.kyori.adventure.key.Key;

/**
 * A playground plugin demonstrating advanced VanillaEnoughItems API usage:
 * overriding a built-in process panel via {@link VeiRegistrationEvent}.
 * <p>
 * This addon uses {@code load: AFTER} so it enables <b>before</b> VEI.
 * This lets us register event listeners that will catch VEI's registration
 * event when it fires during VEI's own {@code onEnable()}.
 */
@NullMarked
public class PlaygroundAddonPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register listeners BEFORE VEI enables (load: AFTER = our plugin loads first)
        // so we can catch VeiRegistrationEvent when VEI fires it
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Waiting for VEI registration event...");
    }

    /**
     * Called by VEI before recipe indexation begins.
     * This is the place to register custom processes, extractors, and panel factories.
     */
    @EventHandler
    public void onVeiRegistration(VeiRegistrationEvent event) {
        VanillaEnoughItemsAPI api = event.getApi();

        // For this demo, we will only override vanilla recipe things

        RecipeExtractor extractor = api.recipeExtractorRegistry();

        // Look up the built-in smelting process by its key
        Process smeltingProcess = api.processRegistry().getProcess(Key.key(Key.MINECRAFT_NAMESPACE, "smelting"));
        if (smeltingProcess == null) {
            getLogger().warning("Smelting process not found — cannot override panel.");
            return;
        }

        // Replace the default smelting panel with our custom one that shows XP and cook time
        api.processPanelRegistry().registerProvider(smeltingProcess, SmeltingPanelOverride::new);
        getLogger().info("Overridden smelting panel with custom panel (XP + cook time display).");
    }
}
