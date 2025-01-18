package me.qheilmann.vei;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.qheilmann.vei.Command.CraftCommand;
import me.qheilmann.vei.Listener.InventoryClickListener;
import me.qheilmann.vei.Listener.InventoryDragListener;
import me.qheilmann.vei.Menu.InventoryShadow;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.RecipeMenu;
import me.qheilmann.vei.Menu.Button.RecipeMenu.QuickLinkButton;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

public class VanillaEnoughItems extends JavaPlugin {
    
    public static final String NAME = VanillaEnoughItems.class.getSimpleName();
    public static final String NAMESPACE = "vei";
    public static final Logger LOGGER = Logger.getLogger(NAME);

    private MenuManager menuManager;

    @Override
    public void onLoad() {
        menuManager = new MenuManager(this);

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        new CraftCommand(this, menuManager).register();

        LOGGER.info(NAME + " has been loaded!");
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        getServer().getPluginManager().registerEvents(new InventoryClickListener(menuManager), this);
        getServer().getPluginManager().registerEvents(new InventoryDragListener(this), this);

        temporaryRecipe();

        LOGGER.info(NAME+ " has been enabled!");
    }

    @Override
    public void onDisable() {
        LOGGER.info(NAME + " has been disabled!");
    }

    // TEMP: Remove this method (temporary recipe)
    private void temporaryRecipe() {
        NamespacedKey key = new NamespacedKey(NAMESPACE, "warriorsword");
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape(" A ", "AAA", " B ");
        recipe.setIngredient('A', Material.DIAMOND);
        recipe.setIngredient('B', Material.STICK);

        getServer().addRecipe(recipe, true);

        // BrewingStandFuelEvent
        // BrewingRecipe brewingRecipe = new BrewingRecipe(key, item);
        

        // Check the recipe/methodes
        var recipeIterator = getServer().recipeIterator();
        var recipes = getServer().getRecipesFor(item);
        var myRecipe = getServer().getRecipe(key);

        LOGGER.info("My custom recipe:");
        if (myRecipe instanceof ShapedRecipe myShapedRecipe)
        {
            LOGGER.info("Recipe: " + myShapedRecipe.getResult() + " " + myShapedRecipe.getClass().getName() + " " + myShapedRecipe.getChoiceMap());
        }

        LOGGER.info("Recipe for diamond sword:");
        for (var recipeItem : recipes)
        {
            LOGGER.info("Recipe: " + recipeItem.getResult() + " " + recipeItem.getClass().getName() + " " + ((ShapedRecipe)recipeItem).getChoiceMap());
        }

        LOGGER.info("RecipeIterator:");
        int maxIteration = 0;
        while (recipeIterator.hasNext() && maxIteration < 30)
        {
            var recipeItem = recipeIterator.next();
            LOGGER.info("Recipe: " + recipeItem.getResult() + " " + recipeItem.getClass().getName());
            maxIteration++;
        }
    }
}
