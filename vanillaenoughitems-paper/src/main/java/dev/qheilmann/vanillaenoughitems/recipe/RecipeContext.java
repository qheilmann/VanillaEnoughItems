package dev.qheilmann.vanillaenoughitems.recipe;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.bookmark.ServerBookmarkRegistry;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerGuiData;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import dev.qheilmann.vanillaenoughitems.recipe.index.TagIndex;

/**
 * Global context for the Recipe system.
 * Holds shared services like RecipeIndex, ProcessPanelRegistry, and ServerBookmarkRegistry,
 * and manages per-player data.
 */
@NullMarked
public class RecipeContext implements Listener {
    private final RecipeIndex recipeIndex;
    private final ProcessPanelRegistry processPanelRegistry;
    private final TagIndex tagIndex;
    private final ServerBookmarkRegistry serverBookmarkRegistry;
    private final Map<UUID, PlayerGuiData> playerDataMap = new ConcurrentHashMap<>();

    public RecipeContext(JavaPlugin plugin, RecipeIndex recipeIndex, ProcessPanelRegistry processPanelRegistry, TagIndex tagIndex, ServerBookmarkRegistry serverBookmarkRegistry) {
        this.recipeIndex = recipeIndex;
        this.processPanelRegistry = processPanelRegistry;
        this.tagIndex = tagIndex;
        this.serverBookmarkRegistry = serverBookmarkRegistry;
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
     * Get the tag index
     * @return the tag index
     */
    public TagIndex getTagIndex() {
        return tagIndex;
    }

    /**
     * Get the server bookmark registry
     * @return the server bookmark registry
     */
    public ServerBookmarkRegistry getServerBookmarkRegistry() {
        return serverBookmarkRegistry;
    }

    /**
     * Get or create player GUI data for the specified player
     * @param playerUuid the player's UUID
     * @return the player's GUI data
     */
    public PlayerGuiData getPlayerData(UUID playerUuid) {
        return playerDataMap.computeIfAbsent(playerUuid, uuid -> new PlayerGuiData(uuid, recipeIndex));
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
