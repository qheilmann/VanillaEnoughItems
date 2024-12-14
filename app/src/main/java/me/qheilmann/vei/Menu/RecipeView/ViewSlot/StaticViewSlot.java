package me.qheilmann.vei.Menu.RecipeView.ViewSlot;

import java.util.List;
import java.util.function.Function;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.qheilmann.vei.Service.CustomHeadFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        BACK_RECIPE(CustomHeadFactory.BACK_RECIPE, RecipeSlotActions::buildBackButton),

        /**
         * Button to go to the next recipe in the history (undo the back action)
         */
        FORWARD_RECIPE(CustomHeadFactory.FORWARD_RECIPE, RecipeSlotActions::buildForwardButton),

        /**
         * Button to go to the previous variation of the same recipe on another page
         */
        PREVIOUS_RECIPE(CustomHeadFactory.PREVIOUS_RECIPE, RecipeSlotActions::buildPreviousButton),

        /**
         * Button to go to the next variation of the same recipe on another page
         */
        NEXT_RECIPE(CustomHeadFactory.NEXT_RECIPE, RecipeSlotActions::buildNextButton),

        /**
         * Button to move automatically the recipe to the workbench slots
         */
        MOVE_INGREDIENTS(CustomHeadFactory.MOVE_INGREDIENTS, RecipeSlotActions::buildMoveIngredientsButton);

        private final ItemStack itemStack;

        RecipeSlotActions(ItemStack itemStack, Function<ItemStack, ItemStack> setupItemStack) {
            this.itemStack = setupItemStack.apply(itemStack);
        }

        public ItemStack getItemStack() {
            return itemStack;
        }


        private static ItemStack buildBackButton(ItemStack itemStack) {
            ItemStack newItemStack = itemStack.clone();
            ItemMeta meta = newItemStack.getItemMeta();
            meta.displayName(Component.text("Navigate Back", NamedTextColor.WHITE));
            meta.lore(List.of(Component.text("Go back to the preceding recipe in the history", NamedTextColor.DARK_GRAY)));
            newItemStack.setItemMeta(meta);
            return newItemStack;
        }

        private static ItemStack buildForwardButton(ItemStack itemStack) {
            ItemStack newItemStack = itemStack.clone();
            ItemMeta meta = newItemStack.getItemMeta();
            meta.displayName(Component.text("Navigate Forward", NamedTextColor.WHITE));
            meta.lore(List.of(Component.text("Return to following recipe in history", NamedTextColor.DARK_GRAY)));
            newItemStack.setItemMeta(meta);
            return newItemStack;
        }

        private static ItemStack buildPreviousButton(ItemStack itemStack) {
            ItemStack newItemStack = itemStack.clone();
            ItemMeta meta = newItemStack.getItemMeta();
            meta.displayName(Component.text("Previous Recipe", NamedTextColor.WHITE));
            meta.lore(List.of(Component.text("Go to the previous variation of the same recipe", NamedTextColor.DARK_GRAY)));
            newItemStack.setItemMeta(meta);
            return newItemStack;
        }

        private static ItemStack buildNextButton(ItemStack itemStack) {
            ItemStack newItemStack = itemStack.clone();
            ItemMeta meta = newItemStack.getItemMeta();
            meta.displayName(Component.text("Next Recipe", NamedTextColor.WHITE));
            meta.lore(List.of(Component.text("Go to the next variation of the same recipe", NamedTextColor.DARK_GRAY)));
            newItemStack.setItemMeta(meta);
            return newItemStack;
        }

        private static ItemStack buildMoveIngredientsButton(ItemStack itemStack) {
            ItemStack newItemStack = itemStack.clone();
            ItemMeta meta = newItemStack.getItemMeta();
            meta.displayName(Component.text("Move Ingredients", NamedTextColor.WHITE));
            meta.lore(List.of(Component.text("Automatically place the recipe on the workbench", NamedTextColor.DARK_GRAY)));
            newItemStack.setItemMeta(meta);
            return newItemStack;
        }
    }
}