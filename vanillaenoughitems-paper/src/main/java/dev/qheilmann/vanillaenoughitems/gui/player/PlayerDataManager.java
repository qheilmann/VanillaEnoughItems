package dev.qheilmann.vanillaenoughitems.gui.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

/**
 * Manages per-player GUI data and handles player lifecycle events.
 * This class is responsible for:
 * <ul>
 *   <li>Creating PlayerGuiData instances for each player</li>
 *   <li>Cleaning up data when players logout</li>
 *   <li>Providing access to player-specific GUI state</li>
 * </ul>
 * 
 * <p>This is separated from service management (RecipeServices) to follow
 * the Single Responsibility Principle.
 */
@NullMarked
public class PlayerDataManager implements Listener {
    
    private final Map<UUID, PlayerGuiData> playerDataMap = new ConcurrentHashMap<>();

    /**
     * Create a new PlayerDataManager and register it as an event listener
     * @param plugin the plugin instance to register events with
     */
    public PlayerDataManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Get or create player GUI data for the specified player
     * @param playerUuid the player's UUID
     * @return the player's GUI data
     */
    public PlayerGuiData getPlayerData(UUID playerUuid) {
        return playerDataMap.computeIfAbsent(playerUuid, PlayerGuiData::new);
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
