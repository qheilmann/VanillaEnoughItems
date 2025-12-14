package dev.qheilmann.vanillaenoughitems;

import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import dev.qheilmann.vanillaenoughitems.commands.DebugCommand;
import dev.jorel.commandapi.CommandAPI;

@NullMarked
public class VanillaEnoughItems extends JavaPlugin {
    
    public static final String PLUGIN_NAME = "VanillaEnoughItems";
    public static final String NAMESPACE = "vanillaenoughitems";
    public static final ComponentLogger LOGGER = ComponentLogger.logger(PLUGIN_NAME);

    private boolean failOnload = false;

    @Override
    public void onLoad() {
        try {
            onLoadCommandAPI();
        } catch (Exception e) {
            LOGGER.error("Failed to load " + PLUGIN_NAME + ": " + e.getMessage());
            failOnload = true;
        }
        LOGGER.info(PLUGIN_NAME + " loaded.");
    }

    @Override
    public void onEnable() {
        if (failOnload) {
            LOGGER.error(PLUGIN_NAME + " failed to load correctly, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        CommandAPI.onEnable();
        
        DebugCommand.register();
        
        LOGGER.info(PLUGIN_NAME + " enabled.");
    }

    @Override
    public void onDisable() {
        LOGGER.info(PLUGIN_NAME + " disabled.");
    }

    private void onLoadCommandAPI() {
        CommandAPIPaperConfig commandApiConfig = new CommandAPIPaperConfig(this);
        commandApiConfig.setNamespace(NAMESPACE);
        commandApiConfig.dispatcherFile(new File(getDataFolder(), "command_registration.json"));

        CommandAPI.onLoad(commandApiConfig);
    }
}
