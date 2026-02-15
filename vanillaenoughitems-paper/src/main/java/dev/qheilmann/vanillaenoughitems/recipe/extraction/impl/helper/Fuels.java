package dev.qheilmann.vanillaenoughitems.recipe.extraction.impl.helper;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.stream.Collectors;

import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;


@NullMarked
public class Fuels {
    
    public static final SequencedSet<ItemStack> FUELS = fuels();

    private static final SequencedSet<ItemStack> fuels() {
        Registry<ItemType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
        LinkedHashSet<ItemStack> fuels = registry.stream()
            .filter(ItemType::isFuel)
            .map(ItemType::createItemStack)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSequencedSet(fuels);
    }
}
