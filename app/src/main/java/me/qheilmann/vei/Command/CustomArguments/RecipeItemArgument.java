package me.qheilmann.vei.Command.CustomArguments;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Service.CustomItemRegistry;

public class RecipeItemArgument extends CustomArgument<ItemStack, NamespacedKey>{

    CustomItemRegistry customItemRegistry;

    // Cache to store the last input and suggestions
    private String lastInput = null;
    private Set<String> lastSuggestions = null;

    public RecipeItemArgument(String nodeName, RecipeIndexService recipeIndexService, CustomItemRegistry customItemRegistry) {
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
        
        // Generate the suggestions for the argument
        this.replaceSuggestions((info, builder) -> {
            String input = info.currentArg().toLowerCase();
            Set<String> suggestions = new HashSet<>();

            // Check if the input is a prefix of the last input, if so, use the cached suggestions
            if (lastInput != null && (input.startsWith(lastInput) || input.endsWith(lastInput))) {
                suggestions = filterCachedSuggestions(input, lastSuggestions);
            } else { // Otherwise, recalculate the suggestions
                suggestions = recalculateSuggestions(input, recipeIndexService); // TODO mb convert this to async suggestion to not overload the main thread
            }
            
            for (String suggestion : suggestions) {
                if (suggestion.contains(input)) {
                    builder.suggest(suggestion);
                }
            }
            
            lastInput = input;
            lastSuggestions = suggestions;
    
            return builder.buildFuture();
        });
    }

    private Set<String> filterCachedSuggestions(String input, Set<String> lastSuggestions) {
        Set<String> filteredSuggestions = new HashSet<>();
        for (String suggestion : lastSuggestions) {
            if (suggestion.contains(input)) {
                filteredSuggestions.add(suggestion);
            }
        }
        return filteredSuggestions;
    }

    private Set<String> recalculateSuggestions(String input, RecipeIndexService recipeIndexService) {
        Set<NamespacedKey> availableNamespaceKey = new HashSet<>();
        collectNamespaceKey(recipeIndexService.getAllResultItemStacks(), availableNamespaceKey);
        collectNamespaceKey(recipeIndexService.getAllIngredientItemStacks(), availableNamespaceKey);

        Set<String> suggestions = new HashSet<>();
        for (NamespacedKey namespacedKey : availableNamespaceKey) {
            String namespaceKeyStr = namespacedKey.toString().toLowerCase();
            if (namespaceKeyStr.contains(input)) {
                suggestions.add(namespaceKeyStr);
            }
        }

        return suggestions;
    }

    /**
     * Collects NamespacedKeys from ItemStacks and adds them to the existing set.
     * Adds Material's key for vanilla items, custom key for custom items, or logs a
     * warning if neither.
     * @param itemStacks ItemStacks to collect NamespacedKeys from
     * @param existingSet Existing set of NamespacedKeys, or null to create a new set
     * @return Set of NamespacedKeys
     */
    private Set<NamespacedKey> collectNamespaceKey(Set<ItemStack> itemStacks, Set<NamespacedKey> existingSet) {        
        if (existingSet == null) {
            existingSet = new HashSet<>();
        }
        
        if (itemStacks == null || itemStacks.isEmpty()) {
            return existingSet;
        }
        
        for (ItemStack item : itemStacks) {
            // Check if the item is a vanilla one
            ItemStack dummyVanillaItem = new ItemStack(item.getType());
            if (item.isSimilar(dummyVanillaItem)) {
                existingSet.add(item.getType().getKey());
                continue;
            }

            // Check if the item is a custom one
            NamespacedKey key = customItemRegistry.getKeyByItem(item);
            if (key != null) {
                existingSet.add(key);
                continue;
            }

            // Log a warning if the item is neither vanilla nor custom
            VanillaEnoughItems.LOGGER.warn("Item " + item + " is not found inside the vanilla and the custom registry, so it won't be suggested. Consider adding it to the custom registry.");
        }

        return existingSet;
    }
}
