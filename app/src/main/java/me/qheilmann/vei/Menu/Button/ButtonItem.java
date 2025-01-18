package me.qheilmann.vei.Menu.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.Name;

import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Item.PersistentDataType.UuidPdt;
import me.qheilmann.vei.Menu.IMenu;
import me.qheilmann.vei.Menu.IOwnedByMenu;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.foundation.gui.VeiStyle;
import net.kyori.adventure.text.Component;

public abstract class ButtonItem extends ItemStack implements IOwnedByMenu {

    protected Component displayName = Component.text("undefined");
    protected List<? extends Component> lores = List.of(Component.text("undefined"));
    
    protected static final String REFERENCE = "undefined";
    public static final String REFERENCE_KEY = "recipe_action";
    public static final String UUID_KEY = "button_uuid";
    
    private final MenuManager menuManager;
    private final IMenu ownerMenu;
    private static final Map<String, TriFunction<ItemStack, IMenu, MenuManager, ButtonItem>> buttonConstructorMap = new HashMap<>();

    public ButtonItem(@NotNull VeiStyle style, IMenu owner, MenuManager menuManager) {
        this(style.getButtonSkin(ButtonItem.class), owner, menuManager);
        initButton(displayName, lores, style, REFERENCE);
    }

    protected ButtonItem(@NotNull ItemStack skin, IMenu ownerMenu, MenuManager menuManager) {
        super(skin);
        this.ownerMenu = ownerMenu;
        this.menuManager = menuManager;
    }

    // TODO remove this method and menuManager
    public static ButtonItem restoreButton(String reference, ItemStack originalItemStack, IMenu originalMenuOwner, MenuManager menuManager) {
        TriFunction<ItemStack, IMenu, MenuManager, ButtonItem> constructor = buttonConstructorMap.get(reference);
        
        if (constructor == null) {
            throw new IllegalStateException("No button constructor found for reference: " + reference);
        }
        
        return constructor.apply(originalItemStack, originalMenuOwner, menuManager);
    }

    protected void initButton(Component displayName, List<? extends Component> lores, VeiStyle style, String reference) {
        setNameAndLore(displayName, lores, style);
        setReference(reference);
        setUuid();
    }

    public Component getDisplayName() {
        return displayName;
    }

    public List<? extends Component> getLores() {
        return lores;
    }

    public IMenu getOwnedMenu() {
        return ownerMenu;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public static Map<String, TriFunction<ItemStack, IMenu, MenuManager, ButtonItem>> getButtonConstructorMap() {
        return buttonConstructorMap;
    }

    protected static void registerButtonItem(String reference, TriFunction<ItemStack, IMenu, MenuManager, ButtonItem> constructor) {
        buttonConstructorMap.put(reference, constructor);
    }

    protected void setNameAndLore(Component displayName, List<? extends Component> lores, VeiStyle style) {
        this.editMeta(meta -> meta.displayName(displayName.color(style.getColor())));
        this.editMeta(meta -> meta.lore(lores.stream().map(lore -> lore.color(style.getSecondaryColor())).toList()));
    }

    protected void setReference(String reference) {
        NamespacedKey  key = new NamespacedKey(VanillaEnoughItems.NAMESPACE, REFERENCE_KEY);
        this.editMeta(meta -> meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, reference));
    }

    protected void setUuid() {
        NamespacedKey key = new NamespacedKey(VanillaEnoughItems.NAMESPACE, UUID_KEY);
        UUID uuid = java.util.UUID.randomUUID();
        this.editMeta(meta -> meta.getPersistentDataContainer().set(key, UuidPdt.TYPE, uuid));
    }

    public abstract void trigger(Player player); // TODO change to trigger(InventoryClickEvent event)
}
