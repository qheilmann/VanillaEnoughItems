package me.qheilmann.vei.Core.Config;

import org.bukkit.configuration.file.FileConfiguration;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Recipe.RecipePath;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeConfigLoader {

    public static Map<UUID, Set<RecipePath>> loadRecipes(FileConfiguration config) {
        Map<UUID, Set<RecipePath>> bookmarkSetMap = new HashMap<>();
        
        // Check if the config is empty
        if (!config.contains("players") || !config.isList("players")) {
            return bookmarkSetMap;
        }

        // Get all players
        List<Map<?, ?>> playersList = config.getMapList("players");

        // Iterate through each player
        for (Map<?, ?> player : playersList) {
            // Get UUID
            String uuidString = (String) player.get("uuid");
            if (uuidString == null) continue;

            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                VanillaEnoughItems.LOGGER.error("Invalid UUID inside %s: %s".formatted(config.getName(), uuidString));
                continue;
            }

            // Get or create bookmark set for the player
            Set<RecipePath> bookmarkSet = bookmarkSetMap.get(uuid);
            if (bookmarkSet == null) {
                bookmarkSet = new HashSet<>();
                bookmarkSetMap.put(uuid, bookmarkSet);
            }

            // Get and convert bookmarks list of the player
            Object bookmarksObj = player.get("bookmarks");
            List<Map<String, Object>> bookmarksList = null;
            if (bookmarksObj instanceof List<?>) {
                bookmarksList = ((List<?>) bookmarksObj).stream()
                    .filter(item -> item instanceof Map<?, ?>)
                    .map(item -> (Map<String, Object>) item)
                    .collect(Collectors.toList());
            }

            // Add each bookmark to the bookmark set
            if (bookmarksList != null) {
                for (Map<String, Object> bookmarkMap : bookmarksList) {
                    RecipePath bookmark = RecipePath.deserialize(bookmarkMap);
                    bookmarkSet.add(bookmark);
                }
            }
        }

        return bookmarkSetMap;
    }
}