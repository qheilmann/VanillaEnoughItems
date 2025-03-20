package me.qheilmann.vei.Core.Config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Recipe.RecipePath;

import java.util.*;

public class RecipeConfigLoader {

    public static Map<UUID, Set<RecipePath>> loadRecipes(FileConfiguration config) {
        Map<UUID, Set<RecipePath>> recipeMap = new HashMap<>();

        // Iterate through each root entry in the config
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            // Get UUID
            String uuidString = section.getString("uuid");
            if (uuidString == null) continue;

            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                VanillaEnoughItems.LOGGER.error("Invalid UUID inside %s: %s".formatted(config.getName(), uuidString));
                continue;
            }

            // Get bookmarks
            List<RecipePath> bookmarks =
                (List<RecipePath>) section.getList("bookmarks", Collections.emptyList());
            Set<RecipePath> recipePaths = new HashSet<>(bookmarks);

            recipeMap.put(uuid, recipePaths);
        }

        return recipeMap;
    }
}
