package dev.qheilmann.vanillaenoughitems;

import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Iterator;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import dev.qheilmann.vanillaenoughitems.commands.CraftCommand;
import dev.qheilmann.vanillaenoughitems.commands.DebugVei;
import dev.qheilmann.vanillaenoughitems.config.VanillaEnoughItemsConfig;
import dev.qheilmann.vanillaenoughitems.config.style.Style;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelFactory;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.BlastingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.CampfireProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.CraftingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.SmeltingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.SmithingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.SmokingProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.impl.StonecuttingProcessPanel;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.recipe.RecipeContext;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorRegistry;
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
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.BlastingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.CampfireProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.CraftingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmeltingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmithingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmokingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.StonecuttingProcess;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvManager;

@NullMarked
public class VanillaEnoughItems extends JavaPlugin {
    
    public static final String PLUGIN_NAME = "VanillaEnoughItems";
    public static final String NAMESPACE = "vanillaenoughitems";
    public static final ComponentLogger LOGGER = ComponentLogger.logger(PLUGIN_NAME);

    @Nullable
    private static VanillaEnoughItemsConfig config;

    private boolean failOnload = false;
    @SuppressWarnings("null")
    private RecipeContext recipeGuiContext;

    @Override
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

        LOGGER.info("Enabling CommandAPI...");
        CommandAPI.onEnable();

        LOGGER.info("Enabling FastInv...");
        FastInvManager.register(this);

        RecipeExtractorRegistry recipeExtractor = new RecipeExtractorRegistry();
        recipeExtractor.registerExtractor(new BlastingRecipeExtractor());
        recipeExtractor.registerExtractor(new CampfireRecipeExtractor());
        recipeExtractor.registerExtractor(new FurnaceRecipeExtractor());
        recipeExtractor.registerExtractor(new ShapedRecipeExtractor());
        recipeExtractor.registerExtractor(new ShapelessRecipeExtractor());
        recipeExtractor.registerExtractor(new SmithingTransformRecipeExtractor());
        recipeExtractor.registerExtractor(new SmithingTrimRecipeRecipeExtractor());
        recipeExtractor.registerExtractor(new SmokingRecipeExtractor());
        recipeExtractor.registerExtractor(new StonecuttingRecipeExtractor());
        recipeExtractor.registerExtractor(new TransmuteRecipeExtractor());
        
        ProcessRegistry processRegistry = new ProcessRegistry();
        ProcessPanelRegistry processPanelRegistry = new ProcessPanelRegistry();
        addProcessesAndPanels(processRegistry, processPanelRegistry, new CraftingProcess(), CraftingProcessPanel::new); // most common
        addProcessesAndPanels(processRegistry, processPanelRegistry, new StonecuttingProcess(), StonecuttingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new SmeltingProcess(), SmeltingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new SmithingProcess(), SmithingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new BlastingProcess(), BlastingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new SmokingProcess(), SmokingProcessPanel::new);
        addProcessesAndPanels(processRegistry, processPanelRegistry, new CampfireProcess(), CampfireProcessPanel::new);

        RecipeIndex recipeIndex = new RecipeIndex(processRegistry, recipeExtractor);
        recipeGuiContext = new RecipeContext(this, recipeIndex, processPanelRegistry);

        Iterator<Recipe> recipeIterator = getServer().recipeIterator();
        recipeIndex.indexRecipe(() -> recipeIterator);
        recipeIndex.logSummary();
        
        // Initialize Recipe GUI Context

        CraftCommand.register(this, recipeGuiContext);
        DebugVei.register();
        
        LOGGER.info(PLUGIN_NAME + " enabled.");
    }

    @Override
    public void onDisable() {
        if (recipeGuiContext != null) {
            recipeGuiContext.clearAllPlayerData();
        }
        LOGGER.info(PLUGIN_NAME + " disabled.");
    }

    /**
     * Gets the current VanillaEnoughItems configuration
     * @return the VanillaEnoughItems configuration
     */
    @SuppressWarnings("null")
    public static VanillaEnoughItemsConfig config() {
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

    private void addProcessesAndPanels(ProcessRegistry processRegistry, ProcessPanelRegistry processPanelRegistry, Process process, ProcessPanelFactory panelFactory) {
           processRegistry.registerProcess(process);
           processPanelRegistry.registerProvider(process, panelFactory);
    }
}
