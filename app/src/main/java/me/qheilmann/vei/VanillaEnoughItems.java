package me.qheilmann.vei;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.qheilmann.vei.Command.CraftCommand;

public class VanillaEnoughItems extends JavaPlugin {
    
    public static final String PLUGIN_NAME = VanillaEnoughItems.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(PLUGIN_NAME);

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        new CraftCommand(this).register();

        LOGGER.info(PLUGIN_NAME + " has been loaded!");
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        temporaryRecipe();

        LOGGER.info(PLUGIN_NAME+ " has been enabled!");
    }

    @Override
    public void onDisable() {
        LOGGER.info(PLUGIN_NAME + " has been disabled!");
    }

    // TODO: Remove this method (temporary recipe)
    private void temporaryRecipe() {
        NamespacedKey key = new NamespacedKey(this, "WarriorSword");
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape(" A ", "AAA", " B ");
        recipe.setIngredient('A', Material.DIAMOND);
        recipe.setIngredient('B', Material.STICK);

        getServer().addRecipe(recipe, true);

        // Check the recipe
        var myRecipe = getServer().getRecipe(key);
        ShapedRecipe myShapedRecipe;
        LOGGER.info(myRecipe.getClass().getName());
        if (myRecipe instanceof ShapedRecipe)
        {
            myShapedRecipe = (ShapedRecipe) myRecipe;
            LOGGER.info("Recipe: " + myShapedRecipe.getChoiceMap());
        }
    }
}
