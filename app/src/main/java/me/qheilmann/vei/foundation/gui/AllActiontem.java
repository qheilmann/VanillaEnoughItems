package me.qheilmann.vei.foundation.gui;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class AllActiontem {

    ItemStack actionItem;
    Consumer<List<? extends String>> updatePredicate;

    public AllActiontem(@NotNull ActionType actionType, @NotNull VeiStyle style) {
        actionItem = style.getActionItem(actionType);
        actionItem.editMeta(meta -> meta.displayName(actionType.getDisplayName().color(style.getColor())));
        actionItem.editMeta(meta -> meta.lore(actionType.getLores().stream().map(lore -> lore.color(style.getSecondaryColor())).toList()));
        NamespacedKey  key = new NamespacedKey ("vei", "recipe_action");
        actionItem.editMeta(meta -> meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, actionType.getReference()));
    }

    public ItemStack getActionItem() {
        return actionItem;
    }
}