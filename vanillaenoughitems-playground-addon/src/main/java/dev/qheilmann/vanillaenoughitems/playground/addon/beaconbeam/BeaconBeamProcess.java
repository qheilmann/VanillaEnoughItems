package dev.qheilmann.vanillaenoughitems.playground.addon.beaconbeam;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;
import dev.qheilmann.vanillaenoughitems.playground.addon.PlaygroundAddonPlugin;
import dev.qheilmann.vanillaenoughitems.recipe.process.AbstractProcess;
import dev.qheilmann.vanillaenoughitems.recipe.process.Workbench;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Process definition for DEMO 4: Beacon Beam transformation.
 * <p>
 * A Process in VEI represents a category or type of recipe crafting mechanism.
 * This Process defines the "Beacon Beam" category that groups all beacon beam recipes.
 * <p>
 * The Process provides:
 * - A symbol (beacon icon) shown in the recipe GUI process tab selector
 * - Associated catalyste (beacon and iron block) that are use like a workbenches
 * - Logic to identify which recipes belong to this process
 * <p>
 * This Process works with BeaconBeamExtractor and BeaconBeamPanel to create
 * a complete recipe system that VEI can index, search, and visualize.
 */
@NullMarked
public class BeaconBeamProcess extends AbstractProcess {

    public static final Key PROCESS_KEY = Key.key(PlaygroundAddonPlugin.NAMESPACE, "beacon_beam");

    private final VanillaEnoughItemsAPI api;

    public BeaconBeamProcess(VanillaEnoughItemsAPI api) {
        super(PROCESS_KEY);
        this.api = api;
    }

    @Override
    public boolean canHandleRecipe(Recipe recipe) {
        return recipe instanceof BeaconBeamRecipe;
    }

    @Override
    public ItemStack symbol() {
        ItemStack item = ItemStack.of(Material.BEACON);
        item.editMeta(meta ->
            meta.displayName(
                Component.text("Beacon Beam", api.config().style().colorPrimary()).decoration(TextDecoration.ITALIC, false)
            )
        );

        return item;
    }

    @Override
    public Set<Workbench> workbenches() {
        Workbench beacon = new Workbench(ItemStack.of(Material.BEACON));
        Workbench base = new Workbench(ItemStack.of(Material.IRON_BLOCK));
        return Set.of(beacon, base);
    }
}
