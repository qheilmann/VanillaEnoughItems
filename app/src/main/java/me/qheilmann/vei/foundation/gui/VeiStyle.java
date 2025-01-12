package me.qheilmann.vei.foundation.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Menu.Button.ButtonItem;
import me.qheilmann.vei.Menu.Button.Generic.BookmarkListButton;
import me.qheilmann.vei.Menu.Button.Generic.BookmarkServerListButton;
import me.qheilmann.vei.Menu.Button.Generic.ExitButton;
import me.qheilmann.vei.Menu.Button.Generic.InfoButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.BackRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.BookmarkThisRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.ForwardRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.MoveIngredientsButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.NextRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.PreviousRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.QuickLinkButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.UnbookmarkThisRecipeButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchTypeScrollLeftButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchTypeScrollRightButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchVariantScrollDownButton;
import me.qheilmann.vei.Menu.Button.RecipeMenu.WorkbenchVariantScrollUpButton;
import me.qheilmann.vei.Service.CustomHeadFactory;
import net.kyori.adventure.text.format.TextColor;

public class VeiStyle {
    private final String name;
    private final ItemStack paddingItem;
    private final TextColor color; // color used for component color
    private final TextColor secondaryColor;
    private final Map<Class<? extends ButtonItem>, ItemStack> buttonSkins; // if the key is not present, the null key is used, if the null key is not present, the default skin is used
    private final ItemStack defaultSkin = new ItemStack(Material.STONE);

    public static final VeiStyle LIGHT;
    public static final VeiStyle DARK;

    static{
        LIGHT = new VeiStyle("Light", new ItemStack(Material.WHITE_STAINED_GLASS_PANE), TextColor.color(Color.WHITE.asRGB()), TextColor.color(Color.GRAY.asRGB()), getLightStyleButtonItems());
        DARK = new VeiStyle("Dark", new ItemStack(Material.BROWN_STAINED_GLASS_PANE), TextColor.color(Color.BLACK.asRGB()), TextColor.color(Color.GRAY.asRGB()), getDarkStyleButtonItems());
    }

    public VeiStyle(@NotNull String name, @NotNull ItemStack paddingItem, @NotNull  TextColor Color, @NotNull  TextColor secondaryColor, @NotNull Map<@NotNull Class<? extends ButtonItem>, @NotNull ItemStack> buttonItemSkins) {
        this.name = name;
        this.paddingItem = paddingItem;
        this.color = Color;
        this.secondaryColor = secondaryColor;
        this.buttonSkins = buttonItemSkins;
    }

    public String getName() {
        return name;
    }

    /**
     * Get the skin for a button
     * @param buttonClass the class of the button
     * @return the skin for the button, if the skin is not found, the default VeiStyle skin is returned if it is not found, the commun default skin is returned
     */
    public @NotNull ItemStack getButtonSkin(@NotNull Class<? extends ButtonItem> buttonClass) {
        ItemStack skin = buttonSkins.get(buttonClass);

        if (skin == null) {
            skin = buttonSkins.get(null);

            if (skin == null) {
                skin = defaultSkin;
            }
        }

        return skin.clone();
    }

    public ItemStack getPaddingItem() {
        return paddingItem;
    }

    public TextColor getColor() {
        return color;
    }

    public TextColor getSecondaryColor() {
        return secondaryColor;
    }

    private static Map<Class<? extends ButtonItem>, ItemStack> getLightStyleButtonItems(){
        Map<Class<? extends ButtonItem>, ItemStack> buttonSkins = new HashMap<>();

        buttonSkins.put(WorkbenchTypeScrollLeftButton.class, CustomHeadFactory.QUARTZ_ARROW_LEFT);
        buttonSkins.put(WorkbenchTypeScrollRightButton.class, CustomHeadFactory.QUARTZ_ARROW_RIGHT);
        buttonSkins.put(WorkbenchVariantScrollUpButton.class, CustomHeadFactory.QUARTZ_ARROW_UP);
        buttonSkins.put(WorkbenchVariantScrollDownButton.class, CustomHeadFactory.QUARTZ_ARROW_DOWN);
        buttonSkins.put(NextRecipeButton.class, CustomHeadFactory.QUARTZ_FORWARD);
        buttonSkins.put(PreviousRecipeButton.class, CustomHeadFactory.QUARTZ_BACKWORD);
        buttonSkins.put(BackRecipeButton.class, CustomHeadFactory.QUARTZ_BACKWARD_II);
        buttonSkins.put(ForwardRecipeButton.class, CustomHeadFactory.QUARTZ_FORWARD_II);
        buttonSkins.put(MoveIngredientsButton.class, CustomHeadFactory.QUARTZ_PLUS);
        buttonSkins.put(QuickLinkButton.class, CustomHeadFactory.QUARTZ_SLASH);
        buttonSkins.put(InfoButton.class, CustomHeadFactory.QUARTZ_REVERSE_EXCLAMATION_MARK);
        buttonSkins.put(BookmarkThisRecipeButton.class, new ItemStack(Material.WHITE_CANDLE));
        buttonSkins.put(UnbookmarkThisRecipeButton.class, new ItemStack(Material.YELLOW_CANDLE));
        buttonSkins.put(BookmarkListButton.class, CustomHeadFactory.FIREWORK_STAR_CYAN);
        buttonSkins.put(BookmarkServerListButton.class, CustomHeadFactory.FIREWORK_STAR_GREEN);
        buttonSkins.put(ExitButton.class, CustomHeadFactory.QUARTZ_X);
        buttonSkins.put(null, new ItemStack(Material.STONE));
        
        return buttonSkins;
    }

    private static Map<Class<? extends ButtonItem>, ItemStack> getDarkStyleButtonItems(){
        Map<Class<? extends ButtonItem>, ItemStack> buttonItemSkins = new HashMap<>();
        buttonItemSkins.put(null, new ItemStack(Material.COAL_BLOCK));
        return buttonItemSkins;
    }
}
