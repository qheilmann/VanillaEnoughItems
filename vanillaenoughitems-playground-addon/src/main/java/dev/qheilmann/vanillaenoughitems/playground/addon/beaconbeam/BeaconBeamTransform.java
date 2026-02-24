package dev.qheilmann.vanillaenoughitems.playground.addon.beaconbeam;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.key.Key;

/**
 * Game mechanic implementation for DEMO 4: Beacon beam item transformation.
 * <p>
 * This listener implements the actual game behavior for beacon beam recipes.
 * It listens for items spawning in the world and transforms them if they:
 * 1. Spawn directly above an active beacon (tier 1+)
 * 2. Match the input of a registered BeaconBeamRecipe
 * <p>
 * This class is separate from VEI's recipe system and handles the actual
 * Minecraft gameplay mechanic. VEI only handles the recipe indexation and
 * visualization through the BeaconBeamExtractor, BeaconBeamProcess, and
 * BeaconBeamPanel components.
 * <p>
 * Important: This is a simplified demo implementation. A production version would:
 * - Optimize beacon detection for performance
 * - Handle glass/transparent blocks above beacons properly
 */
public class BeaconBeamTransform implements Listener {
    
    Map<Key, BeaconBeamRecipe> recipes = new HashMap<>();

    public void register(BeaconBeamRecipe recipe) {
        recipes.put(recipe.key(), recipe);
    }

    public void unregister(Key recipeKey) {
        recipes.remove(recipeKey);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        
        if (!isAboveActiveBeacon(event.getLocation())) {
            return;
        }

        ItemStack droppedItem = event.getEntity().getItemStack();

        for (BeaconBeamRecipe recipe : recipes.values()) {
            if (droppedItem.isSimilar(recipe.input())) {
                ItemStack output = recipe.output();
                output.setAmount(droppedItem.getAmount());
                event.getEntity().setItemStack(output);
                return; // Stop after the first match to avoid multiple transformations
            }
        }

        Bukkit.getServer().getLogger().info("Item spawned: " + event.getEntity().getItemStack().getType());
    }

    /**
     * Checks if the given location is above an active beacon (with at least tier 1).
     * Note: This is a simplified check for demonstration purposes.
     * This will for example not work if the is glass above the beacon
     * @param location the location to check
     * @return true if the location is above an active beacon, false otherwise
     */
    private boolean isAboveActiveBeacon(Location location) {
        World world = location.getWorld();
        Block highestBlock = world.getHighestBlockAt(location, HeightMap.OCEAN_FLOOR);

        if (!highestBlock.getType().equals(Material.BEACON)) {
            return false;
        }

        Beacon beacon = (Beacon) highestBlock.getState();
        return beacon.getTier() > 0;
    }
}
