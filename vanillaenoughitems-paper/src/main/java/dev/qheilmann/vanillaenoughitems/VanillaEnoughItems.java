package dev.qheilmann.vanillaenoughitems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VanillaEnoughItems extends JavaPlugin implements Listener {
    
    @Override
    public void onLoad() {
        getLogger().info("VanillaEnoughItems is loading...");
    }

    @Override
    public void onEnable() {
        getLogger().info("VanillaEnoughItems is enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("VanillaEnoughItems is disabled!");
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        getLogger().info(player.getName() + " broke " + block); // Testing HotSwap
    }
}
