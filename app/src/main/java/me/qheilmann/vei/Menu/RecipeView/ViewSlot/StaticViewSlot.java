package me.qheilmann.vei.Menu.RecipeView.ViewSlot;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.qheilmann.vei.VanillaEnoughItems;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

/**
 * Represents a static slot in the view
 * This slot is not meant to be change between different recipes
 */
public class StaticViewSlot extends ViewSlot {
    
    ItemStack item;

    public StaticViewSlot(Vector2i coord, @NotNull ItemStack item) {
        super(coord);
        setItemStack(item);
    }

    public StaticViewSlot(Vector2i coord, @NotNull RecipeSlotActions slotActions) {
        super(coord);
        setItemStack(slotActions);
    }

    public void setItemStack(@NotNull ItemStack item) {
        this.item = item;
    }

    public void setItemStack(@NotNull RecipeSlotActions slotActions) {
        this.item = slotActions.getItemStack();
    }

    @Override
    public void updateCycle() {
        return;
    }

    @Override
    public void updateCycle(@NotNull Cycle cycle, int step) {
        return;
    }

    @Override
    public ItemStack getCurrentItemStack() {
        return item.clone();
    }

    public enum RecipeSlotActions {

        /**
         * Button to go to the previous recipe in the history
         */
        NAVIGATE_BACK{
            @Override
            public ItemStack getItemStack() {
                return getNavigateBack();
            }
        },

        /**
         * Button to go to the next recipe in the history (undo the back action)
         */
        NAVIGATE_FORWARD{
            @Override
            public ItemStack getItemStack() {
                return getNavigateForward();
            }
        },

        /**
         * Button to go to the previous variation of the same recipe on another page
         */
        NAVIGATE_PREVIOUS{
            @Override
            public ItemStack getItemStack() {
                return getNavigatePrevious();
            }
        },

        /**
         * Button to go to the next variation of the same recipe on another page
         */
        NAVIGATE_NEXT{
            @Override
            public ItemStack getItemStack() {
                return getNavigateNext();
            }
        },

        /**
         * Button to move automatically the recipe to the workbench slots
         */
        AUTO_PLACEMENT{
            @Override
            public ItemStack getItemStack() {
                return getAutoPlacement();
            }
        };

        public ItemStack getItemStack() {
            throw new IllegalArgumentException("Unknown type: " + this);
        }

        private static ItemStack getNavigateBack() {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            JavaPlugin plugin = JavaPlugin.getPlugin(VanillaEnoughItems.class);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            OfflinePlayer player = plugin.getServer().getOfflinePlayer("quoinquoin");
            meta.displayName(Component.text("Navigate Back"));
            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        private static ItemStack getNavigateForward() {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            JavaPlugin plugin = JavaPlugin.getPlugin(VanillaEnoughItems.class);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            OfflinePlayer player = plugin.getServer().getOfflinePlayer("quoinquoin");
            meta.displayName(Component.text("Navigate Forward"));
            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        private static ItemStack getNavigatePrevious() {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            JavaPlugin plugin = JavaPlugin.getPlugin(VanillaEnoughItems.class);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            OfflinePlayer player = plugin.getServer().getOfflinePlayer("quoinquoin");
            meta.displayName(Component.text("Navigate Previous"));
            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        private static ItemStack getNavigateNext() {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            JavaPlugin plugin = JavaPlugin.getPlugin(VanillaEnoughItems.class);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            OfflinePlayer player = plugin.getServer().getOfflinePlayer("quoinquoin");
            meta.displayName(Component.text("Navigate Next"));
            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        private static ItemStack getAutoPlacement() {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            JavaPlugin plugin = JavaPlugin.getPlugin(VanillaEnoughItems.class);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            OfflinePlayer player = plugin.getServer().getOfflinePlayer("quoinquoin");
            meta.displayName(Component.text("Auto Placement"));
            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }
}