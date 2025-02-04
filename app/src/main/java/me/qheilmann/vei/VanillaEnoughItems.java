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
import me.qheilmann.vei.Command.TestCommand;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.Slot.GridSlot;
import me.qheilmann.vei.Core.Slot.Slot;
import me.qheilmann.vei.Core.Slot.Implementation.ChestSlot;
import me.qheilmann.vei.Core.Slot.Implementation.MaxChestSlot;
import me.qheilmann.vei.Core.Style.StyleManager;
import me.qheilmann.vei.Listener.InventoryClickListener;
import me.qheilmann.vei.Listener.InventoryDragListener;
import me.qheilmann.vei.Menu.InventoryShadow;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Menu.RecipeMenuOld;
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
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        menuManager = new MenuManager(this, StyleManager.DEFAULT_STYLE);
        
        new CraftCommand(this, menuManager).register();
        new TestCommand().register();

        LOGGER.info(NAME + " has been loaded!");
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        BaseGui.onEnable(this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(menuManager), this);
        getServer().getPluginManager().registerEvents(new InventoryDragListener(this), this);

        temporaryRecipe();
        temporaryTestMethode();

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

    private void temporaryTestMethode()
    {
        LOGGER.info("Test methode YoloBanza");
        // Without CRTP this is much less complicated, and it's now more familiar
        ChestSlot slot1 = new MaxChestSlot(0);                   // MaxChestSlot                         is a direct extend of ChestSlot
        GridSlot  slot2 = new MaxChestSlot(0);                   // MaxChestSlot>ChestSlot               is a direct extend of GridSlot
        Slot      slot3 = new MaxChestSlot(0);                   // MaxChestSlot>ChestSlot>GridSlot      is a direct extend of Slot
        Object    slot4 = new MaxChestSlot(0);                   // MaxChestSlot>ChestSlot>GridSlot>Slot is a direct extend of Object




        slot1.setIndex(0);
        slot2.setIndex(0);
        slot3.setIndex(0);
        slot4.equals(slot4);



        InventoryShadow<Inventory> inventory = new InventoryShadow<Inventory>(getServer().createInventory(null, 9,  Component.text("Test")));
        inventory.setItem(0, new ItemStack(Material.DIAMOND));
        inventory.setItem(1, new QuickLinkButton(VeiStyle.LIGHT, new RecipeMenuOld(this, menuManager), menuManager));

        LOGGER.info("Inventory[1]: " + inventory.getItem(0).getType());
        LOGGER.info("Inventory[2]: " + inventory.getItem(1).getType());

        ItemStack result1 = inventory.getItem(0);
        ItemStack result2 = inventory.getItem(1);

        if(result1 instanceof ItemStack)
        {
            LOGGER.info("Result1 is an ItemStack");
            LOGGER.info("Result1: " + result1.getClass());
        }
        else
        {
            LOGGER.info("Result1 is not an ItemStack");
            LOGGER.info("Result4: " + result1.getClass());
        }

        if(result2 instanceof QuickLinkButton)
        {
            LOGGER.info("Result2 is a QuickLinkButton");
            LOGGER.info("Result2: " + result2.getClass());
        }
        else
        {
            LOGGER.info("Result2 is not a QuickLinkButton");
            LOGGER.info("Result4: " + result2.getClass());
        }

        Inventory inventory2 = getServer().createInventory(null, 9,  Component.text("Test"));
        inventory2.setItem(0, new ItemStack(Material.DIAMOND));
        inventory2.setItem(1, new QuickLinkButton(VeiStyle.LIGHT, new RecipeMenuOld(this, menuManager), menuManager));

        LOGGER.info("Inventory2[1]: " + inventory2.getItem(0).getType());
        LOGGER.info("Inventory2[2]: " + inventory2.getItem(1).getType());

        ItemStack result3 = inventory2.getItem(0);
        ItemStack result4 = inventory2.getItem(1);

        if(result3 instanceof ItemStack)
        {
            LOGGER.info("Result3 is an ItemStack");
            LOGGER.info("Result3: " + result3.getClass());
        }
        else
        {
            LOGGER.info("Result3 is not an ItemStack");
        }

        if(result4 instanceof QuickLinkButton)
        {
            LOGGER.info("Result4 is a QuickLinkButton");
            LOGGER.info("Result4: " + result4.getClass());
        }
        else
        {
            LOGGER.info("Result4 is not a QuickLinkButton");
            LOGGER.info("Result4: " + result4.getClass());
        }
    }
}
