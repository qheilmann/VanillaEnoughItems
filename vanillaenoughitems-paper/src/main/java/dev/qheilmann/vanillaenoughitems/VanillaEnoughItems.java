package dev.qheilmann.vanillaenoughitems;

import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Iterator;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import dev.qheilmann.vanillaenoughitems.commands.DebugCommand;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
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
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmithingTransformProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmithingTrimProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.SmokingProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.impl.StonecuttingProcess;
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
        
        ProcessRegistry processRegistry = new ProcessRegistry();
        processRegistry.registerProcess(new BlastingProcess());
        processRegistry.registerProcess(new CampfireProcess());
        processRegistry.registerProcess(new CraftingProcess());
        processRegistry.registerProcess(new SmeltingProcess());
        processRegistry.registerProcess(new SmithingTransformProcess());
        processRegistry.registerProcess(new SmithingTrimProcess());
        processRegistry.registerProcess(new SmokingProcess());
        processRegistry.registerProcess(new StonecuttingProcess());

        RecipeExtractor recipeExtractor = new RecipeExtractor();
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

        RecipeIndex recipeIndex = new RecipeIndex(processRegistry, recipeExtractor);
        Iterator<Recipe> recipeIterator = getServer().recipeIterator();
        recipeIndex.indexRecipe(() -> recipeIterator);

        recipeIndex.logSummary();

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
