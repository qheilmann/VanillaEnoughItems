package dev.qheilmann.vanillaenoughitems.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.gui.player.PlayerGuiData;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;

/**
 * Global context for the Recipe GUI system.
 * Holds shared services like RecipeIndex and ProcessPanelRegistry,
 * and manages per-player data.
 */
@NullMarked
public class RecipeGuiContext implements Listener {
    private final RecipeIndex recipeIndex;
    private final ProcessPanelRegistry processPanelRegistry;
    private final Map<UUID, PlayerGuiData> playerDataMap = new ConcurrentHashMap<>();

    public RecipeGuiContext(JavaPlugin plugin, RecipeIndex recipeIndex, ProcessPanelRegistry processPanelRegistry) {
        this.recipeIndex = recipeIndex;
        this.processPanelRegistry = processPanelRegistry;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Get the global recipe index
     * @return the recipe index
     */
    public RecipeIndex getRecipeIndex() {
        return recipeIndex;
    }

    /**
     * Get the process panel registry
     * @return the panel registry
     */
    public ProcessPanelRegistry getProcessPanelRegistry() {
        return processPanelRegistry;
    }

    /**
     * Get or create player GUI data for the specified player
     * @param playerUuid the player's UUID
     * @return the player's GUI data
     */
    public PlayerGuiData getPlayerData(UUID playerUuid) {
        return playerDataMap.computeIfAbsent(playerUuid, uuid -> new PlayerGuiData(uuid, recipeIndex.getAssociatedRecipeExtractor()));
    }

    /**
     * Remove and clear player data when they logout
     * @param playerUuid the player's UUID
     */
    public void removePlayerData(UUID playerUuid) {
        PlayerGuiData data = playerDataMap.remove(playerUuid);
        if (data != null) {
            data.clear();
        }
    }

    /**
     * Event handler to clean up player data on logout
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayerData(event.getPlayer().getUniqueId());
    }

    /**
     * Get the number of players with cached data
     * @return player count
     */
    public int getPlayerDataCount() {
        return playerDataMap.size();
    }

    /**
     * Clear all player data (typically on plugin disable)
     */
    public void clearAllPlayerData() {
        playerDataMap.values().forEach(PlayerGuiData::clear);
        playerDataMap.clear();
    }
}
