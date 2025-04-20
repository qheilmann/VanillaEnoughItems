package me.qheilmann.vei;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.qheilmann.vei.Command.CraftCommand;
import me.qheilmann.vei.Command.TestCommand;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Process.VanillaProcesses;
import me.qheilmann.vei.Core.Recipe.Bookmark.Bookmark;
import me.qheilmann.vei.Core.Recipe.Bookmark.Repository.InMemoryBookmarkRepository;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Style.StyleManager;
import me.qheilmann.vei.Listener.InventoryClickListener;
import me.qheilmann.vei.Listener.InventoryDragListener;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Service.CustomItemRegistry;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class VanillaEnoughItems extends JavaPlugin {
    
    public static final String NAME = VanillaEnoughItems.class.getSimpleName();
    public static final String NAMESPACE = "vei";
    public static final ComponentLogger LOGGER = ComponentLogger.logger(NAME);

    private MenuManager menuManager;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));

        LOGGER.info(NAME + " has been loaded!");
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        BaseGui.onEnable(this);

        CustomItemRegistry customItemRegistry = initializeCustomItemRegistry();

        addTemporaryRecipe(customItemRegistry); // TEMP
        customItemRegistry.completeInitialization();
        
        Process.ProcessRegistry.registerProcesses(VanillaProcesses.getAllVanillaProcesses());
        RecipeIndexService recipeIndex = generateRecipeIndex();

        menuManager = new MenuManager(this, StyleManager.DEFAULT_STYLE, recipeIndex);

        // CommandAPI command registration
        new CraftCommand(menuManager, recipeIndex, customItemRegistry).register();
        new TestCommand().register();

        Bookmark.init(new InMemoryBookmarkRepository());

        // Event registration
        getServer().getPluginManager().registerEvents(new InventoryClickListener(menuManager), this);
        getServer().getPluginManager().registerEvents(new InventoryDragListener(this), this);

        LOGGER.info(NAME+ " has been enabled!");
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        BaseGui.onDisable();
        LOGGER.info(NAME + " has been disabled!");
    }

    // TEMP: Remove this method (temporary recipe)
    private void addTemporaryRecipe(CustomItemRegistry customItemRegistry) {
        LOGGER.info("[123_A] Temporary recipe\n");
        
        // Custom recipe 1 (minecraft item with a new recipe)
        NamespacedKey key = new NamespacedKey(NAMESPACE, "second_diamond_swore");
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

        ShapedRecipe secondDiamondSwordRecipe = new ShapedRecipe(key, item);
        secondDiamondSwordRecipe.shape(" A", "AA", " B");
        secondDiamondSwordRecipe.setIngredient('A', Material.DIAMOND);
        secondDiamondSwordRecipe.setIngredient('B', Material.STICK);

        getServer().addRecipe(secondDiamondSwordRecipe, true);

        // Custom recipe 2 (custom item with a new recipe = here juste a rename diamond sword)
        NamespacedKey key2 = new NamespacedKey(NAMESPACE, "warrior_sword");
        ItemStack item2 = new ItemStack(Material.DIAMOND_SWORD);
        item2.editMeta(meta -> meta.displayName(Component.text("Warrior Sword", NamedTextColor.GOLD)));
        item2.editMeta(meta -> meta.lore(List.of(Component.text("A sword for the bravest warriors"))));
        item2.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
        customItemRegistry.registerItem(key2, item2);

        ShapedRecipe warriorSwordRecipe = new ShapedRecipe(key2, item2);
        warriorSwordRecipe.shape("AAA", "AAA", " B ");
        warriorSwordRecipe.setIngredient('A', Material.GOLD_INGOT);
        warriorSwordRecipe.setIngredient('B', Material.STICK);

        getServer().addRecipe(warriorSwordRecipe, true);

        // Retrieve the recipe
        Recipe recipe = getServer().getRecipe(key);
        if (recipe instanceof Keyed keyed) {
            LOGGER.info("Retrieve Recipe by key: " + keyed.key());
        } else {
            LOGGER.info("Retrieve Recipe by key: not found(" + recipe + ")");
        }
    }

    private CustomItemRegistry initializeCustomItemRegistry() {
        CustomItemRegistry customItemRegistry = new CustomItemRegistry();
        customItemRegistry.registerItem(NamespacedKey.fromString("vei:iron_ingot"), createCustomItem(Material.IRON_INGOT, "VEI Ingot", NamedTextColor.RED));
        customItemRegistry.registerItem(NamespacedKey.fromString("ttt:iron_ingot"), createCustomItem(Material.IRON_INGOT, "TTT Ingot", NamedTextColor.GREEN));
        customItemRegistry.registerItem(NamespacedKey.fromString("vei:special_ingot"), createCustomItem(Material.GOLD_INGOT, "VEI Special Ingot", NamedTextColor.GOLD));
        return customItemRegistry;
    }

    private static ItemStack createCustomItem(Material material, String displayName, NamedTextColor color) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> {
            meta.displayName(Component.text(displayName).color(color));
        });
        return item;
    }

    // TODO TEMP: Remove this method (temporary test)
    @SuppressWarnings({"unchecked", "unused"})
    private void testConfig() {
        LOGGER.info("TMP TEST");
        File playerBookmarkFile = new File(getDataFolder(), "playerBookmark.yml");
        if (!playerBookmarkFile.exists()) {
            saveResource(playerBookmarkFile.getName(), false);
        }
        
        // Get all players
        VanillaEnoughItems.LOGGER.info("[All players]");
        FileConfiguration playerBookmarkConfig = YamlConfiguration.loadConfiguration(playerBookmarkFile);
        LOGGER.info("yolo:" + playerBookmarkConfig.get("players").toString());
        
        // Get the first player
        VanillaEnoughItems.LOGGER.info("[First player]");
        List<Map<String, Object>> players = (List<Map<String, Object>>) playerBookmarkConfig.getList("players");
        if (!players.isEmpty()) {
            Map<String, Object> firstPlayer = players.get(0);
            LOGGER.info("player[0]: " + firstPlayer.toString());
        }
        
        // Get the first player with serialized object
        // VanillaEnoughItems.LOGGER.info("[First player (serialized)]");
        // if (players != null && !players.isEmpty()) {
        //     Map<String, Object> firstPlayer = players.get(0);
        //     List<Map<String, Object>> bookmarks = (List<Map<String, Object>>) firstPlayer.get("bookmarks"); 
        //     if (bookmarks != null && !bookmarks.isEmpty()) {
        //         Map<String, Object> firstBookmark = bookmarks.get(0);
                
        //         // Deserialize the first bookmark into a RecipePath object
        //         RecipePath recipePath = RecipePath.deserialize(firstBookmark); // Check case of process (not cast to lowercase, case is important ?)
        //         LOGGER.info("First RecipePath of the first player: " + recipePath);
        //     }
        // }
        
        VanillaEnoughItems.LOGGER.info("[By config section]");
        ConfigurationSection rootSection = playerBookmarkConfig.getConfigurationSection("");
        if (rootSection == null) {
            LOGGER.info("root section is null");
        } else {
            LOGGER.info("root section: " + rootSection.toString());
            for (String key : rootSection.getKeys(false)) {
                LOGGER.info("Key: " + key);
            }
            // Get the player list
            String playerKey = rootSection.getKeys(false).iterator().next();
            List<?> playerSection = rootSection.getList(playerKey);
            LOGGER.info("player section: " + playerKey);
            for (Object config : playerSection) {
                LOGGER.info("fullConfig: " + config);
            }
            // Get the player list map
            List<Map<?, ?>> playerSectionMap = rootSection.getMapList(playerKey);
            LOGGER.info("player section: " + playerKey);
            for (Map<?, ?> config : playerSectionMap) {
                LOGGER.info("fullConfig: " + config);
                Object bookmarks = config.get("bookmarks");
                LOGGER.info("bookmarks: " + bookmarks);
            }

            // Print all keys
            VanillaEnoughItems.LOGGER.info("[Deep keys]");
            for (String key : rootSection.getKeys(true)) {
                LOGGER.info("deepKey: " + key);
            }

            // var a = rootSection.getSerializable("players", RecipePath.class);
        }

        // [Clear getting format]
        VanillaEnoughItems.LOGGER.info("[Clear getting format]");
        List<Map<?, ?>> playersList = playerBookmarkConfig.getMapList("players");
        for (Map<?, ?> player : playersList) {
            LOGGER.info("player: " + player.get("uuid"));

            // Get and convert bookmarks list of the player
            Object bookmarksObj = player.get("bookmarks");
            List<Map<String, Object>> bookmarksList = null;
            if (bookmarksObj instanceof List<?>) {
                bookmarksList = ((List<?>) bookmarksObj).stream()
                    .filter(item -> item instanceof Map<?, ?>)
                    .map(item -> (Map<String, Object>) item)
                    .collect(Collectors.toList());
            }

            // if (bookmarksList != null) {
            //     for (Map<String, Object> bookmarkMap : bookmarksList) {
            //         RecipePath bookmark = RecipePath.deserialize(bookmarkMap);
            //         LOGGER.info("bookmark: " + bookmark.toString());
            //     }
            // }
        }


        // [Set a new bookmark]
        // VanillaEnoughItems.LOGGER.info("[Set a new bookmark]");
        // RecipePath newRecipePath = new RecipePath(new ItemStack(Material.DIAMOND_SWORD), Process.ProcessRegistry.getProcessByName("Crafting"), 0);
        // UUID playerUuid = UUID.fromString("81376bb8-5576-47bc-a2d9-89d98746d3ec"); 
        
        // // Serialize and find the target player
        // Map<String, Object> serializedRecipePath = newRecipePath.serialize();
        // List<Map<?, ?>> rootPlayersList = playerBookmarkConfig.getMapList("players");
        // Map<String, Object> targetPlayer = null;
        // for (Map<?, ?> player : rootPlayersList) {
        //     if (playerUuid.toString().equals(player.get("uuid"))) {
        //         targetPlayer = (Map<String, Object>) player;
        //         break;
        //     }
        // }

        // // Add the new bookmark to the player
        // if (targetPlayer != null) {
        //     List<Map<String, Object>> playerBookmarks = (List<Map<String, Object>>) targetPlayer.get("bookmarks");
        //     if (playerBookmarks != null) {
        //         playerBookmarks.add(serializedRecipePath);
        //     } else {
        //         playerBookmarks = List.of(serializedRecipePath);
        //         targetPlayer.put("bookmarks", playerBookmarks);
        //     }
        // } else {
        //     rootPlayersList.add(Map.of("uuid", playerUuid.toString(), "bookmarks", List.of(serializedRecipePath)));
        // }

        
        // // [Load from RecipeConfigLoader]
        // LOGGER.info("[Load from RecipeConfigLoader]");
        // Map<UUID, Set<RecipePath>> bookmarkSetMap = RecipeConfigLoader.loadRecipes(playerBookmarkConfig);
        // for (Map.Entry<UUID, Set<RecipePath>> entry : bookmarkSetMap.entrySet()) {
        //     LOGGER.info("UUID: " + entry.getKey());
        //     for (RecipePath recipePath : entry.getValue()) {
        //         LOGGER.info("RecipePath: " + recipePath);
        //     }
        // }
        
        // Save the new bookmark
        try {
            playerBookmarkConfig.save(playerBookmarkFile);
        } catch (Exception e) {
            LOGGER.error("Error while saving the playerBookmark config", e);
        }
        
        // TODO END TMP
    }

    private RecipeIndexService generateRecipeIndex() {
        RecipeIndexService recipeIndexService = new RecipeIndexService();
        recipeIndexService.indexRecipes(this);
        return recipeIndexService;
    }
}

// Manager
// Handler
// Event
// Listener
// Factory
// Service
// API
// Core
// Menu
// Utils
// Config


// Java
// add version with date for minor changes
// ajouter des tests unitaires
// test https://github.com/sladkoff/minecraft-prometheus-exporter?tab=readme-ov-file > Minecraft > Prometheus > Grafana
// json file inside ressource folder / plugin folder for custom recipes (other than API)
// Replace concat string inside precondition with internal formatting like this Preconditions.checkArgument(y >= 0 && y < rowCount, "y must be between 0 and %d, current value: %d", rowCount, y);
// we can use @implSpec and @throws IndexOutOfBoundsException {@inheritDoc}

// fast
// replace precondition with Preconditions.checkArgument(0 != 0, "%s cannot be null", RecipeMenu.class);
// TODO check si quand je crée un itemStack avec un autre item stack (copy ctor), s'il garde les pcd, displayname, lore, etc
// TODO when the Gui item is remove by a player click (not canceled), the GuiListener throw an warning, (fix it ?, check with debug prrint) < ShadowInventory


// [Command]
// /craft " " (server craft list) or (player if server not existe)
// /craft player_bookmark (player craft list) pb
// /craft server_bookmark (server craft list) sb
// /craft itemStack

// [Addon de craft possibles]
// Shears sur un blocs pour le changé (+ actual recipe like carved pumpkin)
// Rayon beacon
// Cauldron

// [Extra process]
// drop view (like the drop item when we break a block (eg sapling) https://jd.papermc.io/paper/1.21.4/org/bukkit/block/Block.html#getDrops ...)
// workbench usage (eg campfire we can see all the recipe that can be done with the campfire, see JEI all usage, composter, fuel etc)
// enchantment table (see all the enchantment that can be done with the item)

// [Extra recipe information]
// if the item is not consumed (like bucket in cake, add a lore to indicate that the item is not consumed)
// inside the workbench item (eg: furnace) we can see the time to be smelted the xp generated and other information

// [Other ideas]
// Idea: look how work JEI addon, modepack for creating new reicpe, and how they are implemented in the game by JEI
// maybe we can make in sort of simply reuse jei addon and add it to our plugin on the same way