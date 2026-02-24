package dev.qheilmann.vanillaenoughitems.playground.addon;

import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

/**
 * A playground plugin demonstrating advanced VanillaEnoughItems API usage:
 * overriding a built-in process panel via {@link VeiRegistrationEvent}.
 * <p>
 * This addon uses {@code load: AFTER} so it enables <b>before</b> VEI.
 * This lets us register event listeners that will catch VEI's registration
 * event when it fires during VEI's own {@code onEnable()}.
 */
@NullMarked
public class PlaygroundAddonPlugin extends JavaPlugin {

    public static final String NAMESPACE = "vei-playground-addon";

    @Override
    public void onEnable() {
        // Register listeners BEFORE VEI enables (load: AFTER = our plugin loads first)
        // so we can catch VeiRegistrationEvent when VEI fires it
        getServer().getPluginManager().registerEvents(new VeiRegistrationListener(this), this);
        getServer().getPluginManager().registerEvents(new VeiReadyListener(this), this); // Only needed for demo 4 (new recipe type)
        getLogger().info("Waiting for VEI registration and ready events...");
    }
}
