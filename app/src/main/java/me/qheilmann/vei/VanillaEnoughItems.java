package me.qheilmann.vei;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.qheilmann.vei.Command.CraftCommand;
import me.qheilmann.vei.Command.TestCommand;
import me.qheilmann.vei.Core.GUI.BaseGui;
import me.qheilmann.vei.Core.Process.CraftingProcess;
import me.qheilmann.vei.Core.Process.DummyProcess;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Process.SmeltingProcess;
import me.qheilmann.vei.Core.Recipe.AllRecipeMap;
import me.qheilmann.vei.Core.Recipe.ItemRecipeMap;
import me.qheilmann.vei.Core.Recipe.ProcessRecipeSet;
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
        fillRecipeMap();

        LOGGER.info(NAME+ " has been enabled!");
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        BaseGui.onDisable();
        LOGGER.info(NAME + " has been disabled!");
    }

    // TEMP: Remove this method (temporary recipe)
    private void temporaryRecipe() {
        LOGGER.info("[123_A] Temporary recipe\n");
        // Custom recipe 1 (minecraft item with a new recipe)

        NamespacedKey key = new NamespacedKey(NAMESPACE, "second_diamond_swore");
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape(" A ", "AAA", " B ");
        recipe.setIngredient('A', Material.DIAMOND);
        recipe.setIngredient('B', Material.STICK);

        getServer().addRecipe(recipe, true);

        // Custom recipe 2 (custom item with a new recipe = here juste a rename diamond sword)

        NamespacedKey key2 = new NamespacedKey(NAMESPACE, "warrior_sword");
        ItemStack item2 = new ItemStack(Material.DIAMOND_SWORD);
        item2.editMeta(meta -> meta.displayName(Component.text("Warrior Sword")));
        item2.editMeta(meta -> meta.lore(List.of(Component.text("A sword for the bravest warriors"))));
        item2.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
        // item2.editMeta(meta -> meta.setCustomModelData(1));

        ShapedRecipe recipe2 = new ShapedRecipe(key2, item2);
        recipe2.shape("AAA", "AAA", " B ");
        recipe2.setIngredient('A', Material.GOLD_INGOT);
        recipe2.setIngredient('B', Material.STICK);

        getServer().addRecipe(recipe2, true);

        // BrewingStandFuelEvent
        // BrewingRecipe brewingRecipe = new BrewingRecipe(key, item);
        

        // Check the recipe/methodes
        var recipeIterator = getServer().recipeIterator();
        var recipes = getServer().getRecipesFor(item);
        var recipes2 = getServer().getRecipesFor(item2);
        var myRecipe = getServer().getRecipe(key);

        LOGGER.info("My custom recipe:");
        if (myRecipe instanceof ShapedRecipe myShapedRecipe)
        {
            LOGGER.info("Recipe: " + myShapedRecipe.getResult() + " " + myShapedRecipe.getClass().getName() + " " + myShapedRecipe.getChoiceMap());
        }

        LOGGER.info("[**Recipe for diamond sword:**]");
        for (var recipeItem : recipes)
        {
            LOGGER.info("Recipe: " + recipeItem.getResult() + " " + recipeItem.getClass().getName() + " " + ((ShapedRecipe)recipeItem).getChoiceMap());
        }

        LOGGER.info("[**Recipe for warrior sword:**]");
        for (var recipeItem : recipes2)
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
        LOGGER.info("[123_B] Test methode YoloBanza\n");
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

    @SuppressWarnings("null")
    private void fillRecipeMap() {
        LOGGER.info("[123_C] Fill the recipe map\n");

        AllRecipeMap allRecipesMap = new AllRecipeMap();

        // Get all recipes
        var recipeIterator = getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            ItemStack result = recipe.getResult();
            Process<?> process = recipeToProcessConverter(recipe);

            if (result == null) {
                continue;
            }

            // Skip non-iron ingot recipes for check only this one
            if(result.getType() != Material.IRON_INGOT) {
                continue;
            }

            // Logs some information
            LOGGER.info("Recipe: " + result + " " + recipe.getClass().getName());
            LOGGER.info("Process: " + process);

            // Get/set the recipe map
            ItemRecipeMap itemRecipeMap;
            if (allRecipesMap.containsItem(result)) {
                itemRecipeMap = allRecipesMap.getItemRecipeMap(result);
            } else {
                itemRecipeMap = new ItemRecipeMap();
                allRecipesMap.putItemRecipeMap(result, itemRecipeMap);
            }

            // Get/set the process recipe set
            ProcessRecipeSet processRecipeSet;
            if (itemRecipeMap.containsProcess(process)) {
                processRecipeSet = itemRecipeMap.getProcessRecipeSet(process);
            } else {
                processRecipeSet = new ProcessRecipeSet();
                itemRecipeMap.putProcessRecipeSet(process, processRecipeSet);
            }

            // Add the recipe to the process recipe set
            processRecipeSet.add(recipe);
        }

        // Check the recipe map
        LOGGER.info("[***]Recipe map[***]: " + allRecipesMap.size());
        for (ItemStack item : allRecipesMap.getItems()) {
            LOGGER.info("[Item]: " + item.toString());
            ItemRecipeMap itemRecipeMap = allRecipesMap.getItemRecipeMap(item);
            for (Process<?> process : itemRecipeMap.ProcessSet()) {
                LOGGER.info("Process: " + process);
                ProcessRecipeSet processRecipeSet = itemRecipeMap.getProcessRecipeSet(process);
                for (Recipe recipe : processRecipeSet.toArray()) {
                    String str = "Recipe: " + recipe.getResult() + " " + recipe.getClass().getName() + " ";
                    if (recipe instanceof ShapedRecipe shapedRecipe) {
                        str += shapedRecipe.getChoiceMap();
                    }
                    LOGGER.info(str);
                }
            }
            LOGGER.info("\n\n");
        }
    }

    private Process<?> recipeToProcessConverter(Recipe recipe) {
        if        (recipe instanceof ShapedRecipe) {
            return new CraftingProcess();
        } else if (recipe instanceof ShapelessRecipe) {
            return new CraftingProcess();
        // } else if (recipe instanceof TransmuteRecipe) {
        //     return Material.SMITHING_TABLE;
        } else if (recipe instanceof BlastingRecipe) {
            return new DummyProcess();
        } else if (recipe instanceof SmokingRecipe) { 
            return new DummyProcess();
        } else if (recipe instanceof CampfireRecipe) {
            return new DummyProcess();
        } else if (recipe instanceof FurnaceRecipe) { // Must be after BlastingRecipe and SmokingRecipe (same superclass)
            return new SmeltingProcess();
        } else if (recipe instanceof SmithingTransformRecipe) {
            return new DummyProcess();
        } else if (recipe instanceof SmithingTrimRecipe) {
            return new DummyProcess();
        } else if (recipe instanceof SmithingRecipe) { // Must be after SmithingTransformRecipe and SmithingTrimRecipe (same superclass)
            return new DummyProcess();
        } else if (recipe instanceof StonecuttingRecipe) {
            return new DummyProcess();
        } else {
            return new DummyProcess();
        }
    }
}

