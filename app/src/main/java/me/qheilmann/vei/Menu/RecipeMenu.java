package me.qheilmann.vei.Menu;

import net.kyori.adventure.text.Component;

import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import me.qheilmann.vei.Menu.RecipeView.IRecipeView;
import me.qheilmann.vei.Menu.RecipeView.RecipeViewFactory;

public class RecipeMenu implements InventoryHolder {
    
    public Inventory inventory;
    
    private IRecipeView<Recipe> recipeView; // IRecipeView<? extends Recipe>
    private JavaPlugin plugin;
    
    public RecipeMenu(JavaPlugin plugin, Recipe recipe) {
        this.plugin = plugin;
        this.inventory = this.plugin.getServer().createInventory(this,54, Component.text("Recipe"));
        initInventory();
        setRecipe(recipe);
    }

    @Override
    public Inventory getInventory() {
        updateCycle();
        return inventory;
    }

    public void setRecipe(@NotNull Recipe recipe) {
        recipeView = RecipeViewFactory.createRecipeView(recipe);
        recipeView.setRecipe(recipe);
        updateRecipeViewPart();
    }

    private void initInventory() {
        // TODO: Implement this method

        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Special Item"));
        item.setItemMeta(meta);
        inventory.setItem(4, item);

        // Methode 1
        String fireworkHeadComponent = "[minecraft:custom_name='{\"text\":\"Firework Star (cyan)\",\"color\":\"gold\",\"underlined\":true,\"bold\":true,\"italic\":false}',minecraft:lore=['{\"text\":\"Custom Head ID: 29795\",\"color\":\"gray\",\"italic\":false}','{\"text\":\"www.minecraft-heads.com\",\"color\":\"blue\",\"italic\":false}'],profile={id:[I;962553342,1524713861,-1319432986,842621010],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhmOTIzNTNmZDUyZDBlZWQ2NTdkOTk3ZWE4MTQ5YzgyNWQzNjZiMDI4YzE1MTRiZDllY2FhZjI0M2ZiN2JjNiJ9fX0=\"}]}]";

        ItemStack item2 = new ItemStack(Material.PLAYER_HEAD);
        // String componentsStr = item2.getItemMeta().getAsComponentString();
        String itemKeyStr = item2.getType().getKey().toString();
        String itemAsString = itemKeyStr + fireworkHeadComponent;
        VanillaEnoughItems.LOGGER.info("Item as string: " + itemAsString);
        ItemStack recreatedItemStack = Bukkit.getItemFactory().createItemStack(itemAsString);
        inventory.setItem(5, recreatedItemStack);

        // Methode 2
        String fireworkHeadURI = "http://textures.minecraft.net/texture/964ad8da319e6eb37721e02c78864990b45fc0fea06ee52ed4c24ac197278cb7";
        ItemStack head = createCustomHead(fireworkHeadURI);
        inventory.setItem(6, head);
        return;
    }

    private static ItemStack createCustomHead(String URIString) {
        URL url = null;


        try {
            url = new URI(URIString).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
            ItemStack warningItem = new ItemStack(Material.BARRIER);
            warningItem.editMeta(meta -> meta.displayName(
                    Component.text("Warning: Conversion of the URI string to head failed (" + URIString + ")",
                            TextColor.color(255, 0, 0))));
            return warningItem;
        }

        UUID uuid = UUID.nameUUIDFromBytes(URIString.getBytes()); // Here we generate a UUID from the URI string, 
        PlayerProfile profile = Bukkit.createProfile(uuid);
        PlayerTextures playerTextures = profile.getTextures();
        playerTextures.setSkin(url);
        profile.setTextures(playerTextures);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setPlayerProfile(profile);
        head.setItemMeta(skullMeta);
        return head;
    }

    private void updateRecipeViewPart() {
        for (var slot : recipeView.getRecipeContainer().getSlots()) {
            int index = menuCoordAsMenuIndex(viewCoordAsMenuCoord(slot.getCoord()));
            inventory.setItem(index, slot.getCurrentItemStack());
        }
    }

    private void updateCycle() {
        recipeView.getRecipeContainer().updateCycle();
        updateRecipeViewPart();
        return;
    }

    static private Vector2i viewCoordAsMenuCoord(Vector2i coord) {
        Validate.inclusiveBetween(0, 6, coord.x, "x must be between 0 and 6");
        Validate.inclusiveBetween(0, 4, coord.y, "y must be between 0 and 4");

        return new Vector2i(coord.x + 1, coord.y + 1);
    }

    static private int menuCoordAsMenuIndex(Vector2i coord) {
        return coord.x + coord.y * 9;
    }
}
