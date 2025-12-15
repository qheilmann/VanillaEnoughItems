package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;


@NullMarked
public class Fuels {
    
    public static final Set<ItemStack> FUELS = fuels();

    @SuppressWarnings("null")
    private static final Set<ItemStack> fuels() {
        Registry<ItemType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
        return registry.stream()
            .filter(ItemType::isFuel)
            .map(ItemType::createItemStack)
            .collect(Collectors.toUnmodifiableSet());
    }
}
