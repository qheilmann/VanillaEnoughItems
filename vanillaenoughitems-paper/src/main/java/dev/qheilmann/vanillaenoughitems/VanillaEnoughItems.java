package dev.qheilmann.vanillaenoughitems;

import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;

import dev.qheilmann.vanillaenoughitems.api.event.VeiReadyEvent;
import dev.qheilmann.vanillaenoughitems.api.event.VeiRegistrationEvent;
import dev.qheilmann.vanillaenoughitems.bookmark.Bookmark;
import dev.qheilmann.vanillaenoughitems.bookmark.BookmarkImpl;
import dev.qheilmann.vanillaenoughitems.bookmark.ServerBookmarkRegistry;
import dev.qheilmann.vanillaenoughitems.bookmark.ServerBookmarkRegistryImpl;
import dev.qheilmann.vanillaenoughitems.commands.CraftCommand;
import dev.qheilmann.vanillaenoughitems.commands.DebugVei;
import dev.qheilmann.vanillaenoughitems.config.VanillaEnoughItemsConfig;
import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.bookmarkgui.BookmarkGui;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerDataManager;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerGuiData;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelFactory;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistryImpl;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGui;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.BlastingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.CampfireProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.CraftingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.SmeltingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.SmithingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.SmokingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.StonecuttingProcessPanel;
import dev.qheilmann.vanillaenoughitems.metrics.BStatsMetrics;
import dev.qheilmann.vanillaenoughitems.quickaccess.QuickRecipeAccessListener;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorRegistryImpl;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.BlastingRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.CampfireRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.FurnaceRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.ShapedRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.ShapelessRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.SmithingTransformRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.SmithingTrimRecipeRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.SmokingRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.StonecuttingRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.TransmuteRecipeExtractor;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import dev.qheilmann.vanillaenoughitems.recipe.index.TagIndex;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.RecipeIndexView;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistryImpl;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.BlastingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.CampfireProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.CraftingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmeltingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmithingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmokingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.StonecuttingProcess;
import dev.qheilmann.vanillaenoughitems.utils.VeiKey;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

@NullMarked
@SuppressWarnings("java:S6539") // Monster class allowed, it's the main plugin class
public class VanillaEnoughItems extends JavaPlugin implements VanillaEnoughItemsAPI {

    public static final String PLUGIN_NAME = "VanillaEnoughItems";
    public static final String NAMESPACE = "vanillaenoughitems";
    public static final ComponentLogger LOGGER = ComponentLogger.logger(PLUGIN_NAME);

    @Nullable
    private static VanillaEnoughItemsConfig config;

    private boolean failOnload = false;
    @SuppressWarnings("null")
    private PlayerDataManager playerDataManager;

    // API registry fields
    @SuppressWarnings("null")
    private ProcessRegistryImpl processRegistry;
    @SuppressWarnings("null")
    private RecipeExtractorRegistryImpl recipeExtractorRegistry;
    @SuppressWarnings("null")
    private ProcessPanelRegistryImpl processPanelRegistry;
    @SuppressWarnings("null")
    private ServerBookmarkRegistryImpl serverBookmarkRegistry;
    @SuppressWarnings("null")
    private RecipeIndex recipeIndex;
    @SuppressWarnings("null")
    private TagIndex tagIndex;
    
    // Convenience container
    @SuppressWarnings("null")
    private RecipeServices recipeServices;

    //#region Plugin Lifecycle

    @Override
    @SuppressWarnings("java:S2696") // Static fields set in onLoad() is intentional - Bukkit lifecycle guarantees single initialization
    public void onLoad() {
        try {
            onLoadCommandAPI();
        } catch (Exception e) {
            LOGGER.error("Failed to load " + PLUGIN_NAME + ": " + e.getMessage());
            failOnload = true;
        }

        // VanillaEnoughItems config
        Style style = new Style()
            .setHasResourcePack(true);

        config = new VanillaEnoughItemsConfig()
            .setMissingImplementationWarnings(true)
            .setMissingRecipeProcess(true)
            .setStyle(style);

        LOGGER.info(PLUGIN_NAME + " loaded.");
    }

    @Override
    public void onEnable() {
        if (failOnload) {
            LOGGER.error(PLUGIN_NAME + " failed to load correctly, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LOGGER.debug("Enabling CommandAPI...");
        CommandAPI.onEnable();

        LOGGER.debug("Enabling FastInv...");
        FastInvManager.register(this);

        // Custom recipe
        registerCustomRecipe();

        this.recipeExtractorRegistry = new RecipeExtractorRegistryImpl();
        recipeExtractorRegistry.registerExtractor(new BlastingRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new CampfireRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new FurnaceRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new ShapedRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new ShapelessRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new SmithingTransformRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new SmithingTrimRecipeRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new SmokingRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new StonecuttingRecipeExtractor());
        recipeExtractorRegistry.registerExtractor(new TransmuteRecipeExtractor());
        
        this.processRegistry = new ProcessRegistryImpl();
        this.processPanelRegistry = new ProcessPanelRegistryImpl(recipeExtractorRegistry);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new CraftingProcess(), CraftingProcessPanel::new); // most common
        addProcessesAndPanels(processRegistry, processPanelRegistry, new StonecuttingProcess(), StonecuttingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new SmeltingProcess(), SmeltingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new SmithingProcess(), SmithingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new BlastingProcess(), BlastingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new SmokingProcess(), SmokingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new CampfireProcess(), CampfireProcessPanel::new);

        this.serverBookmarkRegistry = new ServerBookmarkRegistryImpl();
        
        // Register this plugin as the API implementation
        VanillaEnoughItemsAPI.register(this);
        
        // Fire VeiRegistrationEvent — plugins register custom processes/extractors/panels here
        getServer().getPluginManager().callEvent(new VeiRegistrationEvent(this));
        
        // Index recipes (after all registrations are done)
        this.recipeIndex = new RecipeIndex(processRegistry, recipeExtractorRegistry);
        Iterator<Recipe> recipeIterator = getServer().recipeIterator();
        recipeIndex.indexRecipe(() -> recipeIterator);
        recipeIndex.logSummary();
        
        // Fire VeiReadyEvent — indexation is complete, recipeIndex() is now safe to call
        getServer().getPluginManager().callEvent(new VeiReadyEvent(this));
        
        // Build tag index
        this.tagIndex = new TagIndex();
        Registry<ItemType> itemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
        tagIndex.index(itemRegistry.getTags(), itemRegistry);
        
        this.recipeServices = new RecipeServices(
            recipeExtractorRegistry,
            processRegistry,
            processPanelRegistry,
            recipeIndex,
            tagIndex,
            serverBookmarkRegistry
        );
        
        // Create player data manager
        playerDataManager = new PlayerDataManager(this);
        
        // Add example server bookmarks

        LOGGER.debug("Initializing server bookmarks...");
        initializeServerBookmarks(serverBookmarkRegistry);

        CraftCommand.register(this, recipeServices, playerDataManager);
        DebugVei.register();

        // Quick recipe access
        if (config().isQuickRecipeLookupEnabled()) {
            getServer().getPluginManager().registerEvents(new QuickRecipeAccessListener(this, recipeServices, playerDataManager), this);
        }

        LOGGER.debug("Initialize bStats metrics...");
        BStatsMetrics.initialize(this, recipeIndex);
        
        LOGGER.info(PLUGIN_NAME + " enabled.");
    }

    @Override
    public void onDisable() {
        VanillaEnoughItemsAPI.unregister();
        if (playerDataManager != null) {
            playerDataManager.clearAllPlayerData();
        }
        LOGGER.info(PLUGIN_NAME + " disabled.");
    }

    //#endregion Plugin Lifecycle

    //#region API

    // ---- Accessors ----

    @Override
    public VanillaEnoughItemsConfig config() {
        return veiConfig();
    }

    @Override
    public RecipeIndexView recipeIndex() {
        return recipeIndex;
    }

    @Override
    public ProcessRegistry processRegistry() {
        return processRegistry;
    }

    @Override
    public RecipeExtractorRegistry recipeExtractorRegistry() {
        return recipeExtractorRegistry;
    }

    @Override
    public ProcessPanelRegistry processPanelRegistry() {
        return processPanelRegistry;
    }

    @Override
    public ServerBookmarkRegistry serverBookmarkRegistry() {
        return serverBookmarkRegistry;
    }

    // ---- GUI ----

    @Override
    public boolean openRecipeGui(Player player, ItemStack result) {
        MultiProcessRecipeReader reader = recipeIndex.readerByResult(result);
        if (reader == null) return false;
        openReaderGui(player, reader);
        return true;
    }

    @Override
    public boolean openRecipeGui(Player player, Key recipeKey) {
        MultiProcessRecipeReader reader = recipeIndex.readerByKey(recipeKey);
        if (reader == null) return false;
        openReaderGui(player, reader);
        return true;
    }

    @Override
    public boolean openUsageGui(Player player, ItemStack ingredient) {
        MultiProcessRecipeReader reader = recipeIndex.readerByIngredient(ingredient);
        if (reader == null) return false;
        openReaderGui(player, reader);
        return true;
    }

    @Override
    public void openReaderGui(Player player, MultiProcessRecipeReader reader) {
        RecipeGui gui = new RecipeGui(recipeServices, playerDataManager.getPlayerData(player.getUniqueId()), reader);
        gui.open(player);
    }

    @Override
    public void openPlayerBookmarkGui(Player player) {
        PlayerGuiData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        Set<Bookmark> bookmarks = playerData.getBookmarks();

        BookmarkGui gui = new BookmarkGui(
            Component.text("Bookmarks"), recipeServices, playerData, bookmarks, null
        );
        gui.open(player);
    }

    @Override
    public void openServerBookmarkGui(Player player) {
        Set<Bookmark> bookmarks = serverBookmarkRegistry.getBookmarks();

        BookmarkGui gui = new BookmarkGui(
            Component.text("Server Bookmarks"), recipeServices, playerDataManager.getPlayerData(player.getUniqueId()), bookmarks, null
        );
        gui.open(player);
    }

    // ---- Operations ----

    /**
     * Reload the recipe indexation.
     * Clears the existing RecipeIndex and re-indexes all server recipes.
     * This only indexes recipes returned by the server's recipeIterator, so it may not include custom recipes added manually.
     */
    @Override
    public void reloadIndexation() {
        LOGGER.info("Reloading recipe indexation...");
        
        recipeIndex.clearIndex();
        Iterator<Recipe> recipeIterator = getServer().recipeIterator();
        recipeIndex.indexRecipe(() -> recipeIterator);
        recipeIndex.logSummary();

        LOGGER.info("Recipe indexation reloaded. All references remain valid.");
    }

    //#endregion API

    //#region Internal

    public static VanillaEnoughItems getPlugin() {
        return getPlugin(VanillaEnoughItems.class);
    }

    /**
     * Gets the current VanillaEnoughItems configuration.
     * Satisfies both the static access pattern ({@code VanillaEnoughItems.veiConfig()})
     * and the API interface ({@code api.config()}).
     * @return the VanillaEnoughItems configuration
     */
    public static VanillaEnoughItemsConfig veiConfig() {
        if (config == null) {
            throw new IllegalStateException("Tried to access VanillaEnoughItems config, but it was not initialized! Are you using VanillaEnoughItems features before calling VanillaEnoughItems#onLoad?");
        }
        return config;
    }

    private void onLoadCommandAPI() {
        CommandAPIPaperConfig commandApiConfig = new CommandAPIPaperConfig(this);
        commandApiConfig.setNamespace(NAMESPACE);
        commandApiConfig.dispatcherFile(new File(getDataFolder(), "command_registration.json"));

        CommandAPI.onLoad(commandApiConfig);
    }

    private void registerCustomRecipe() {
        
        // Normal shapeless
        ShapelessRecipe syntheticDiamond = new ShapelessRecipe(VeiKey.namespacedKey("synthetic_diamond"), ItemType.DIAMOND.createItemStack())
            .addIngredient(4, ItemType.COAL.createItemStack());
        getServer().addRecipe(syntheticDiamond);

        // Modified itemstack
        ItemStack magicBall = ItemType.MAGENTA_DYE.createItemStack(meta ->
            meta.displayName(Component.text("Magic Ball", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
        );
        ShapelessRecipe magicBallRecipe = new ShapelessRecipe(VeiKey.namespacedKey("magic_ball"), magicBall)
            .addIngredient(ItemType.DIAMOND.createItemStack())
            .addIngredient(ItemType.BLAZE_POWDER.createItemStack())
            .addIngredient(ItemType.GHAST_TEAR.createItemStack());
        getServer().addRecipe(magicBallRecipe);

        // Crafting Remaining
        ItemStack exposedCopper = ItemType.EXPOSED_COPPER.createItemStack();
        ShapedRecipe oxidizeCopperRecipe = new ShapedRecipe(VeiKey.namespacedKey("oxidize_copper"), exposedCopper)
            .shape("CCC", "CWC", "CCC")
            .setIngredient('C', ItemType.COPPER_BLOCK.createItemStack())
            .setIngredient('W', ItemType.WATER_BUCKET.createItemStack());
        getServer().addRecipe(oxidizeCopperRecipe);

        // Craft remaining with custom item
        ItemStack oxidizedCopper = ItemType.OXIDIZED_COPPER.createItemStack();
        ItemStack superOxidizer = ItemType.WATER_BUCKET.createItemStack(meta ->
            meta.displayName(Component.text("Super Oxidizer", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
        );
        superOxidizer.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(ItemType.STICK.createItemStack())); // dummy for change the item component

        ShapelessRecipe superOxidizerRecipe = new ShapelessRecipe(VeiKey.namespacedKey("super_oxidizer"), superOxidizer)
            .addIngredient(ItemType.WATER_BUCKET.createItemStack())
            .addIngredient(magicBall);
        getServer().addRecipe(superOxidizerRecipe);

        ShapedRecipe scrapeCopperRecipe = new ShapedRecipe(VeiKey.namespacedKey("super_oxidize_copper"), oxidizedCopper)
            .shape("CCC", "CWC", "CCC")
            .setIngredient('C', ItemType.COPPER_BLOCK.createItemStack())
            .setIngredient('W', superOxidizer);
        getServer().addRecipe(scrapeCopperRecipe);
    }

    /**
     * Initialize example server bookmarks.
     * Server administrators can modify this method to add custom bookmarks.
     * @param registry the server bookmark registry
     */
    private void initializeServerBookmarks(ServerBookmarkRegistry registry) {

        ProcessPanelRegistry panelRegistry = processPanelRegistry;
        Style bookmarkStyle = VanillaEnoughItems.veiConfig().style();
        
        // Example 1: All diamond recipes
        Bookmark diamondBookmark = BookmarkImpl.fromKey(Key.key("minecraft:diamond"), recipeIndex, panelRegistry, bookmarkStyle);
        if (diamondBookmark != null) {
            registry.addBookmark(diamondBookmark);
        }
        
        // Example 2: All gold recipe
        Bookmark gold = BookmarkImpl.fromResult(recipeIndex, ItemType.GOLD_INGOT.createItemStack(), bookmarkStyle);
        if (gold != null) {
            registry.addBookmark(gold);
        }
        
        // Example 3: All iron usage
        MultiProcessRecipeReader reader = recipeIndex.readerByIngredient(ItemType.IRON_INGOT.createItemStack());
        if (reader == null) {
            return;
        }
        Bookmark ironIngot = BookmarkImpl.fromReader(reader, new CyclicIngredient(0, ItemType.IRON_INGOT.createItemStack(meta ->
            meta.displayName(Component.text().applicableApply(bookmarkStyle.colorPrimary()).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("How to use iron !"))
                .build())
        )));
        registry.addBookmark(ironIngot);

        // Smithing
        Bookmark ironSmithingBookmark = BookmarkImpl.fromKey(Key.key("minecraft:bolt_armor_trim_smithing_template_smithing_trim"), recipeIndex, panelRegistry, bookmarkStyle);
        if (ironSmithingBookmark != null) {
            registry.addBookmark(ironSmithingBookmark);
        }
    }

    private void addProcessesAndPanels(ProcessRegistry processRegistry, ProcessPanelRegistry processPanelRegistry, Process process, ProcessPanelFactory panelFactory) {
           processRegistry.registerProcess(process);
           processPanelRegistry.registerProvider(process, panelFactory);
    }

    //#endregion Internal
}
