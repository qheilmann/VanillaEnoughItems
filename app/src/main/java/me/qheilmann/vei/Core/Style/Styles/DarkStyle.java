package me.qheilmann.vei.Core.Style.Styles;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Style.ButtonType.ButtonType;
import me.qheilmann.vei.Core.Style.ButtonType.VeiButtonType;
import net.kyori.adventure.text.format.TextColor;

public class DarkStyle extends Style {
    public static final DarkStyle STYLE;
    public static final NamespacedKey ID;

    static {
        ID = new NamespacedKey(VanillaEnoughItems.NAMESPACE, "dark_theme");
        STYLE = new DarkStyle(); // must be set after ID
    }

    private DarkStyle() {
        super(
            buildStyleProfile(),
            new ItemStack(Material.BLACK_STAINED_GLASS_PANE), 
            TextColor.color(Color.GRAY.asRGB()), 
            TextColor.color(Color.BLACK.asRGB()),
            getbuttonMaterialsMap()
        );
    }

    private static StyleProfile buildStyleProfile(){
        return new StyleProfile(
            ID,
            "Dark", 
            "The default dark style with a black background and grey text", 
            new ItemStack(Material.COAL_BLOCK)
        );
    }

    private static Map<ButtonType, ItemStack> getbuttonMaterialsMap(){
        Map<ButtonType, ItemStack> buttonSkins = new HashMap<>();

        buttonSkins.put(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_BOOKMARKED, new ItemStack(Material.ORANGE_CANDLE));
        buttonSkins.put(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_UNBOOKMARKED, new ItemStack(Material.BLACK_CANDLE));
        buttonSkins.put(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE_UNABLE, new ItemStack(Material.RED_CANDLE));
        buttonSkins.put(null, new ItemStack(Material.BLACKSTONE)); // default
        
        return buttonSkins;
    }
}
