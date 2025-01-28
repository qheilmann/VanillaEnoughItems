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
import me.qheilmann.vei.Service.CustomHeadFactory;
import net.kyori.adventure.text.format.TextColor;

public class LightStyle extends Style{
    public static final LightStyle STYLE = new LightStyle();
    public static final NamespacedKey ID = new NamespacedKey(VanillaEnoughItems.NAMESPACE, "light_theme");

    private LightStyle() {
        super(
            buildStyleProfile(), 
            new ItemStack(Material.WHITE_STAINED_GLASS_PANE), 
            TextColor.color(Color.GRAY.asRGB()), 
            TextColor.color(Color.WHITE.asRGB()), 
            buildbuttonMaterialsMap()
        );
    }

    private static StyleProfile buildStyleProfile(){
        return new StyleProfile(
            ID,
            "Light", 
            "The default light style with a white background and grey text", 
            new ItemStack(Material.SMOOTH_QUARTZ)
        );
    }

    private static Map<ButtonType, ItemStack> buildbuttonMaterialsMap(){
        Map<ButtonType, ItemStack> buttonSkins = new HashMap<>();

        buttonSkins.put(VeiButtonType.Generic.EXIT, CustomHeadFactory.QUARTZ_X);
        buttonSkins.put(VeiButtonType.Generic.INFO, CustomHeadFactory.QUARTZ_REVERSE_EXCLAMATION_MARK);
        buttonSkins.put(VeiButtonType.RecipeMenu.BACK_RECIPE, CustomHeadFactory.QUARTZ_BACKWARD_II);
        buttonSkins.put(VeiButtonType.RecipeMenu.BOOKMARK_LIST, CustomHeadFactory.FIREWORK_STAR_CYAN);
        buttonSkins.put(VeiButtonType.RecipeMenu.BOOKMARK_SERVER_LIST, CustomHeadFactory.FIREWORK_STAR_GREEN);
        buttonSkins.put(VeiButtonType.RecipeMenu.BOOKMARK_THIS_RECIPE, new ItemStack(Material.WHITE_CANDLE));
        buttonSkins.put(VeiButtonType.RecipeMenu.FORWARD_RECIPE, CustomHeadFactory.QUARTZ_FORWARD_II);
        buttonSkins.put(VeiButtonType.RecipeMenu.MOVE_INGREDIENTS, CustomHeadFactory.QUARTZ_PLUS);
        buttonSkins.put(VeiButtonType.RecipeMenu.NEXT_RECIPE, CustomHeadFactory.QUARTZ_FORWARD);
        buttonSkins.put(VeiButtonType.RecipeMenu.PREVIOUS_RECIPE, CustomHeadFactory.QUARTZ_BACKWORD);
        buttonSkins.put(VeiButtonType.RecipeMenu.QUICK_LINK, CustomHeadFactory.QUARTZ_SLASH);
        buttonSkins.put(VeiButtonType.RecipeMenu.WORKBENCH_TYPE_SCROLL_LEFT, CustomHeadFactory.QUARTZ_ARROW_LEFT);
        buttonSkins.put(VeiButtonType.RecipeMenu.WORKBENCH_TYPE_SCROLL_RIGHT, CustomHeadFactory.QUARTZ_ARROW_RIGHT);
        buttonSkins.put(VeiButtonType.RecipeMenu.WORKBENCH_VARIANT_SCROLL_DOWN, CustomHeadFactory.QUARTZ_ARROW_DOWN);
        buttonSkins.put(VeiButtonType.RecipeMenu.WORKBENCH_VARIANT_SCROLL_UP, CustomHeadFactory.QUARTZ_ARROW_UP);
        buttonSkins.put(VeiButtonType.RecipeMenu.UNBOOKMARK_THIS_RECIPE, new ItemStack(Material.YELLOW_CANDLE));
        buttonSkins.put(null, new ItemStack(Material.SMOOTH_QUARTZ)); // default
        
        return buttonSkins;
    }
}
