package dev.qheilmann.vanillaenoughitems.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInv;
import dev.qheilmann.vanillaenoughitems.utils.fastinv.FastInvItem;
import net.kyori.adventure.text.Component;

@NullMarked
public class RecipeGui extends FastInv{

    private static final int SIZE = FastInv.GENERIC_9X6_SIZE;

    public RecipeGui() {
        super(SIZE, Component.text("Recipes"));
    }

    public void render() {
        setItems(0, SIZE, dummyItem());
    }

    private FastInvItem dummyItem() {
        return new FastInvItem(
            new ItemStack(Material.DIAMOND),
            event -> event.getWhoClicked().sendMessage(Component.text("Clicked on dummy item"))
        );
    }
}
