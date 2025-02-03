package me.qheilmann.vei.Core.GUI;

import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Item.PersistentDataType.UuidPdt;

/*
 *
 * @author Most original part come from Triumph GUI <a href="https://github.com/TriumphTeam/triumph-gui">TriumphTeam</a>
 */
public class GuiItem<G extends BaseGui<G, ?>> extends ItemStack {

    private static final @NotNull String UUID_KEY_STRING = "gui_item_uuid";
    public static final @NotNull NamespacedKey UUID_KEY = new NamespacedKey(VanillaEnoughItems.NAMESPACE, UUID_KEY_STRING);

    // Action to do when clicking on the item
    GuiAction<InventoryClickEvent, G> action;

    // Random UUID to identify the item when clicking
    private UUID uuid;

    /**
     * Main constructor of the GuiItem
     *
     * @param itemStack The {@link ItemStack} to be used
     * @param action    The {@link GuiAction} to run when clicking on the Item
     */
    public GuiItem(@NotNull final ItemStack itemStack, @Nullable final GuiAction<@NotNull InventoryClickEvent, G> action) {
        super(itemStack);

        this.action = action;
        setUuid(UUID.randomUUID());
    }

    /**
     * Secondary constructor with no action
     *
     * @param itemStack The ItemStack to be used
     */
    public GuiItem(@NotNull final ItemStack itemStack) {
        this(itemStack, null);
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack} but without a {@link GuiAction}
     *
     * @param material The {@link Material} to be used when invoking class
     */
    public GuiItem(@NotNull final Material material) {
        this(new ItemStack(material), null);
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack}
     *
     * @param material The {@code Material} to be used when invoking class
     * @param action   The {@link GuiAction} should be passed on {@link InventoryClickEvent}
     */
    public GuiItem(@NotNull final Material material, @Nullable final GuiAction<@NotNull InventoryClickEvent, G> action) {
        this(new ItemStack(material), action);
    }

    /**
     * Gets the random {@link UUID} that was generated when the GuiItem was made
     */
    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the {@link GuiAction} to do when the player clicks on it
     */
    @Nullable
    public GuiAction<InventoryClickEvent, G> getAction() {
        return action;
    }

    /**
     * Replaces the {@link GuiAction} of the current GUI Item
     *
     * @param action The new {@link GuiAction} to set
     */
    public void setAction(@Nullable final GuiAction<@NotNull InventoryClickEvent, G> action) {
        this.action = action;
    }

    public GuiItem<G> clone() {
        try {
            GuiItem<G> cloned = new GuiItem<>(super.clone());
            cloned.setUuid(this.getUuid());
            cloned.setAction(this.getAction());
            return cloned;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
        this.editMeta(meta -> meta.getPersistentDataContainer().set(UUID_KEY, UuidPdt.TYPE, uuid));
    }
}
