package me.qheilmann.vei;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
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
import me.qheilmann.vei.Core.Style.StyleManager;
import me.qheilmann.vei.Listener.InventoryClickListener;
import me.qheilmann.vei.Listener.InventoryDragListener;
import me.qheilmann.vei.Menu.MenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class VanillaEnoughItems extends JavaPlugin {
    
    public static final String NAME = VanillaEnoughItems.class.getSimpleName();
    public static final String NAMESPACE = "vei";
    public static final ComponentLogger LOGGER = ComponentLogger.logger(NAME);

    private MenuManager menuManager;

    public static final AllRecipeMap allRecipesMap = new AllRecipeMap();

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        menuManager = new MenuManager(this, StyleManager.DEFAULT_STYLE);
        
        new CraftCommand(menuManager).register();
        new TestCommand().register();

        LOGGER.info(NAME + " has been loaded!");
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        BaseGui.onEnable(this);

        getServer().getPluginManager().registerEvents(new InventoryClickListener(menuManager), this);
        getServer().getPluginManager().registerEvents(new InventoryDragListener(this), this);

        addTemporaryRecipe();
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
    private void addTemporaryRecipe() {
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
        item2.editMeta(meta -> meta.displayName(Component.text("Warrior Sword")));
        item2.editMeta(meta -> meta.lore(List.of(Component.text("A sword for the bravest warriors"))));
        item2.editMeta(meta -> meta.setEnchantmentGlintOverride(true));

        ShapedRecipe warriorSwordRecipe = new ShapedRecipe(key2, item2);
        warriorSwordRecipe.shape("AAA", "AAA", " B ");
        warriorSwordRecipe.setIngredient('A', Material.GOLD_INGOT);
        warriorSwordRecipe.setIngredient('B', Material.STICK);

        getServer().addRecipe(warriorSwordRecipe, true);
    }

    @SuppressWarnings("null")
    private void fillRecipeMap() {
        LOGGER.info("[123_C] Fill the recipe map\n");

        // Get all recipes
        var recipeIterator = getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe =  recipeIterator.next();
            ItemStack result = recipe.getResult();
            Process<?> process = recipeToProcessConverter(recipe);

            if (result == null) {
                continue;
            }

            // Get/create the item recipe map
            ItemRecipeMap itemRecipeMap;
            if (allRecipesMap.containsItem(result)) {
                itemRecipeMap = allRecipesMap.getItemRecipeMap(result);
            } else {
                itemRecipeMap = new ItemRecipeMap();
                allRecipesMap.putItemRecipeMap(result, itemRecipeMap);
            }

            // Get/create the process recipe set
            ProcessRecipeSet<?> processRecipeSet;
            if (itemRecipeMap.containsProcess(process)) {
                processRecipeSet = itemRecipeMap.getProcessRecipeSet(process);
            } else {
                processRecipeSet = new ProcessRecipeSet<>();
                itemRecipeMap.unsafePutProcessRecipeSet(process, processRecipeSet);
            }

            // Add the recipe to the process recipe set
            if (!processRecipeSet.tryAdd(recipe) && !processRecipeSet.contains(recipe)) {
                throw new IllegalStateException("Recipe cannot be added to the process recipe set, the recipe type is not the same as the process recipe set type");
            }
        }

        // Check the recipe map
        // LOGGER.info("[***]Recipe map[***]: " + allRecipesMap.size());
        // for (ItemStack item : allRecipesMap.getItems()) {

        //     // log only iron_ingot recipe or all
        //     if(item.getType() != Material.IRON_INGOT) {
        //         continue;
        //     }

        //     LOGGER.info("[Item]: " + item.toString());
        //     ItemRecipeMap itemRecipeMap = allRecipesMap.getItemRecipeMap(item);
        //     for (Process<?> process : itemRecipeMap.getAllProcess()) {

        //         ProcessRecipeSet<?> processRecipeSet = itemRecipeMap.getProcessRecipeSet(process);
        //         for (Recipe recipe : processRecipeSet.toArray()) {

        //             String str = "Recipe: " + recipe.getResult() + " " + recipe.getClass().getName() + " ";
        //             if (recipe instanceof ShapedRecipe shapedRecipe) {
        //                 str += shapedRecipe.getChoiceMap();
        //             }
        //             LOGGER.info(str);
        //         }
        //     }
        //     LOGGER.info("\n\n");
        // }
    }

    private Process<?> recipeToProcessConverter(Recipe recipe) {
        // TODO convert this to static instance, it's not necessary to create a new instance each time it's just the map key (or Clazz, but not acces to methode ?)
        // TODO add a way to add process type by API (first before vanilla in case of derivate class, add a comment to add them in right way in case of sub sub process of client process)
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

// fast
// replace precondition with Preconditions.checkArgument(0 != 0, "%s cannot be null", RecipeMenu.class);
// getRecipeFor(ItemStack) inside Server class
// TODO set the style of the GuiItemService inside the ctor
// TODO check si quand je crée un itemStack avec un autre item stack (copy ctor), s'il garde les pcd, displayname, lore, etc
// TODO when the Gui item is remove by a player click (not canceled), the GuiListener throw an warning, (fix it ?, check with debug prrint)
// TODO For the moment the AllRecipeMap override workbench when they are the same supertype (eg: material.crafting table and material.crafter)
// TODO add Shapeless to the crafting process panel
// TODO we can use @implSpec and @throws IndexOutOfBoundsException {@inheritDoc}


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