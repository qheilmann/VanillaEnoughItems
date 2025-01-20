package me.qheilmann.vei.Core.GUI;

import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import dev.triumphteam.gui.components.util.ItemNbt;

/*
 * 
 * @author Most part come from TriumphTeam <a href="https://github.com/TriumphTeam/triumph-gui">TriumphTeam</a>
 */
public class GuiItem<G extends BaseGui<G>> {
    
    // Action to do when clicking on the item
    GuiAction<InventoryClickEvent, G> action;

    // The ItemStack of the GuiItem
    private ItemStack itemStack;

    // Random UUID to identify the item when clicking
    private UUID uuid = UUID.randomUUID();

    /**
     * Main constructor of the GuiItem
     *
     * @param itemStack The {@link ItemStack} to be used
     * @param action    The {@link GuiAction} to run when clicking on the Item
     */
    public GuiItem(@NotNull final ItemStack itemStack, @Nullable final GuiAction<@NotNull InventoryClickEvent, G> action) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.action = action;

        // Sets the UUID to an NBT tag to be identifiable later
        setItemStack(itemStack);
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
     * Gets the GuiItem's {@link ItemStack}
     *
     * @return The {@link ItemStack}
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Replaces the {@link ItemStack} of the GUI Item
     *
     * @param itemStack The new {@link ItemStack}
     */
    public void setItemStack(@NotNull final ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        if (itemStack.getType() == Material.AIR) {
            this.itemStack = itemStack.clone();
            return;
        }

        this.itemStack = ItemNbt.setString(itemStack.clone(), "mf-gui", uuid.toString()); // TODO replace with PDC always
    }

    /**
     * Gets the random {@link UUID} that was generated when the GuiItem was made
     */
    @NotNull
    UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the {@link GuiAction} to do when the player clicks on it
     */
    @Nullable
    GuiAction<InventoryClickEvent, G> getAction() {
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
}
