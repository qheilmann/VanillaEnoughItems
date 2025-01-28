package me.qheilmann.vei.foundation.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import me.qheilmann.vei.Menu.Button.ButtonItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class GuiItemService
{
    private final Material WarningItem = Material.BARRIER;

    // TODO remove this
    // return ButtonItem
    public ButtonItem CreateButtonItem(@NotNull Supplier<? extends ButtonItem> buttonSupplier, @NotNull VeiStyle style) {
        // Preconditions.checkNotNull(buttonSupplier, "ButtonSupplier cannot be null");
        // Preconditions.checkNotNull(style, "VeiStyle cannot be null");

        // ButtonItem button = buttonSupplier.get();
        // ItemStack buttonSkin = style.getButtonSkin(button.getClass());
        // boolean isButtonItemNull = (buttonSkin == null);



        // ItemStack item = style.getButtonSkin(buttonClass);
        // ButtonItem button = buttonClass.cast(item);
        
        // ItemStack buttonSkin = style.getButtonSkin(buttonClass);
        // boolean isButtonItemNull = (buttonSkin == null);

        // if(buttonSkin == null) {
        //     buttonSkin = new ItemStack(WarningItem);
        // }
        // else {
        //     buttonSkin = buttonSkin.clone();
        // }

        // TEMP
        // buttonSkin.editMeta(meta -> meta.displayName(buttonClass.getDisplayName().color(style.getColor())));
        // buttonSkin.editMeta(meta -> meta.lore(buttonClass.getLores().stream().map(lore -> lore.color(style.getSecondaryColor())).toList()));
        // NamespacedKey  key = new NamespacedKey(VanillaEnoughItems.NAMESPACE, ButtonType.REFERENCE_KEY);
        // buttonSkin.editMeta(meta -> meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, buttonClass.getReference()));
        
        // if(isButtonItemNull){
        //     buttonSkin = CreateWarningItem("Conversion of the button type %s to an item with the the style %s failed".formatted(buttonClass.toString(), style.getName()), buttonSkin);
        // }

        // ButtonItem button = ButtonFactory.createButton(buttonClass, buttonSkin);
        // return button;

        return null;
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