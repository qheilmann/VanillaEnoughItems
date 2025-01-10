package me.qheilmann.vei.foundation.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Menu.Button.ButtonFactory;
import me.qheilmann.vei.Menu.Button.ButtonItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class GuiItemService
{
    private final Material WarningItem = Material.BARRIER;

    // return ButtonItem
    public ButtonItem CreateButtonItem(@NotNull ButtonType buttonType, @NotNull VeiStyle style) {
        Preconditions.checkNotNull(buttonType, "ButtonType cannot be null");
        Preconditions.checkNotNull(style, "VeiStyle cannot be null");
        
        ItemStack buttonSkin = style.getButtonSkin(buttonType);
        boolean isButtonItemNull = (buttonSkin == null);

        if(buttonSkin == null) {
            buttonSkin = new ItemStack(WarningItem);
        }
        else {
            buttonSkin = buttonSkin.clone();
        }

        buttonSkin.editMeta(meta -> meta.displayName(buttonType.getDisplayName().color(style.getColor())));
        buttonSkin.editMeta(meta -> meta.lore(buttonType.getLores().stream().map(lore -> lore.color(style.getSecondaryColor())).toList()));
        NamespacedKey  key = new NamespacedKey(VanillaEnoughItems.NAMESPACE, ButtonType.REFERENCE_KEY);
        buttonSkin.editMeta(meta -> meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, buttonType.getReference()));
        
        if(isButtonItemNull){
            buttonSkin = CreateWarningItem("Conversion of the button type %s to an item with the the style %s failed".formatted(buttonType.toString(), style.getName()), buttonSkin);
        }

        ButtonItem button = ButtonFactory.createButton(buttonType, buttonSkin);
        return button;
    }

    public ItemStack CreateWarningItem(@NotNull String warningMessage) {
        ItemStack warningItem = new ItemStack(WarningItem);
        warningItem.editMeta(meta -> meta.displayName(Component.text("Warning", TextColor.color(255, 0, 0))));
        return this.CreateWarningItem(warningMessage, warningItem);
    }

    public ItemStack CreateWarningItem(@NotNull String warningMessage, @NotNull ItemStack templateWarningItem) {
        Preconditions.checkNotNull(warningMessage, "Warning message cannot be null");
        Preconditions.checkArgument(!warningMessage.isEmpty(), "Warning message cannot be empty");
        Preconditions.checkNotNull(templateWarningItem, "Warning item cannot be null");
        Preconditions.checkArgument(templateWarningItem.getAmount() > 0, "Warning item cannot be empty (current: %s)".formatted(templateWarningItem.getAmount()));
        
        ItemStack warningItem = new ItemStack(WarningItem);
        List<Component> lores = new ArrayList<>();

        warningItem.setAmount(templateWarningItem.getAmount());
        if(templateWarningItem.hasItemMeta()){
            ItemMeta tempalteMeta = templateWarningItem.getItemMeta();
            warningItem.setItemMeta(tempalteMeta);
            if(tempalteMeta.hasLore()){
                lores = tempalteMeta.lore();
            }
        }

        lores.add(Component.text("Warning: %s".formatted(warningMessage), TextColor.color(255, 0, 0)));
        ItemMeta meta = warningItem.getItemMeta();
        meta.lore(lores);

        warningItem.setItemMeta(meta);

        return warningItem;
    }
}