package me.qheilmann.vei.Command.CustomArguments;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Service.CustomItemRegistry;

public class RecipeResultArgument extends CustomArgument<ItemStack, NamespacedKey>{

    CustomItemRegistry customItemRegistry;

    public RecipeResultArgument(String nodeName, RecipeIndexService recipeIndexService, CustomItemRegistry customItemRegistry) {
        super(new NamespacedKeyArgument(nodeName), info -> {
            NamespacedKey key = info.currentInput();

            // First, check if it's a vanilla Minecraft item:
            if (key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
                Material mat = Material.getMaterial(key.getKey().toUpperCase());
                if (mat != null) {
                    return new ItemStack(mat);
                }
            }

            // Then, check if it's a custom item:
            ItemStack item = customItemRegistry.getItem(key);
            if (item != null) {
                return item;
            }

            // Fail
            throw CustomArgumentException.fromString("Unknown item: " + key);
        });

        this.customItemRegistry = customItemRegistry;
        
        this.replaceSuggestions((info, builder) -> {
            String input = info.currentArg().toLowerCase();
            List<String> suggestions = new ArrayList<>();

            for (ItemStack item : recipeIndexService.getAllResultItemStacks()) {
                // Because we can't know if the item is a custom one or not, we need to check with a vanilla one
                
                // Vanilla item
                ItemStack vanillaItem = new ItemStack(item.getType());
                if (vanillaItem.isSimilar(item)) {
                    suggestions.add(item.getType().getKey().toString().toLowerCase());
                    continue;
                }

                // Custom item
                else {
                    NamespacedKey key = customItemRegistry.getKeyByItem(item);
                    if (key != null) {
                        suggestions.add(key.toString().toLowerCase());
                        continue;
                    }
                }

                // If we reach this point, the item is not a vanilla one and not a custom one, so print a warning
                VanillaEnoughItems.LOGGER.warn("Item " + item + " is not a found inside the vanilla and the custom registry, so it won't be suggested.");
            }

            for (String suggestion : suggestions) {
                if (suggestion.contains(input)) {
                    builder.suggest(suggestion);
                }
            }
    
            return builder.buildFuture();
        });
    }
}
