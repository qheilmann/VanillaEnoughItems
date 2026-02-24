package dev.qheilmann.vanillaenoughitems.playground.addon.campfiresponge;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class CampfireSpongeOverrideProcess extends AbstractProcess {

    // We use the same key as the vanilla campfire process, so we override it
    public static final Key PROCESS_KEY = Key.key("campfire_cooking");

    private final VanillaEnoughItemsAPI api;

    public CampfireSpongeOverrideProcess(VanillaEnoughItemsAPI api) {
        super(PROCESS_KEY);
        this.api = api;
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return recipe instanceof CampfireRecipe;
    }

    @Override
    public ItemStack symbol() {
        ItemStack item = ItemStack.of(Material.SPONGE);
        item.editMeta(meta ->
            meta.displayName(
                Component.text("Campfire Cooking", api.config().style().colorPrimary()).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(" WITH SPONGES", NamedTextColor.YELLOW, TextDecoration.BOLD))
            )
        );

        return item;
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench campfire = new Workbench(ItemStack.of(Material.CAMPFIRE));
        Workbench soulCampfire = new Workbench(ItemStack.of(Material.SOUL_CAMPFIRE));
        Workbench spongWorkbench = new Workbench(ItemStack.of(Material.SPONGE));
        return Set.of(campfire, soulCampfire, spongWorkbench);
    }
}
