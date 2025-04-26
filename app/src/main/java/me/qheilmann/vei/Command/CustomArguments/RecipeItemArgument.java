package me.qheilmann.vei.Command.CustomArguments;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Service.CustomItemRegistry;
import net.kyori.adventure.text.Component;

public class RecipeItemArgument extends CustomArgument<ItemStack, NamespacedKey> {

    public RecipeItemArgument(String nodeName, CustomItemRegistry customItemRegistry) {
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
            throw CustomArgumentHelper.minecraftLikeException((arg) -> Component.text("Unknown item '" + arg + "'"), info);
        });
    }

    // This method is protected to prevent direct usage of the suggestion logic within CommandAPI's default suggestion checks.
    // Instead, the builder should be used to manually add suggestions while applying our custom checks.
    public static CompletableFuture<Collection<String>> suggestions(RecipeIndexService recipeIndexService, CustomItemRegistry customItemRegistry, String input) {
        return CompletableFuture.supplyAsync(() -> 
            // This is a resource-intensive operation, so it would be better to cache already calculated suggestions.
            // However, since we can have multiple RecipeIndex instances and this is a static method, implementing caching is a bit tricky.
            // Some caching implementations were done just after commit fbd4474649a04cdafb81c0c8d115ecda25db0ec0.
            // At least, this operation is currently performed on a separate thread, so the performance impact is mitigated.
            recalculateSuggestions(input, recipeIndexService, customItemRegistry)
        );
    }

    public static ArgumentSuggestions<CommandSender> argumentSuggestions(RecipeIndexService recipeIndexService, CustomItemRegistry customItemRegistry) {
        return (info, builder) -> {
            CompletableFuture<Collection<String>> suggestionsFuture = suggestions(recipeIndexService, customItemRegistry, info.currentArg());

            return suggestionsFuture.thenApply(suggestions -> {
                for (String suggestion : suggestions) {
                    builder.suggest(suggestion);
                }
                return builder.build();
            });
        };
    }

    //#region Utility

    @SuppressWarnings("unused")
    private static Set<String> filterCachedSuggestions(String input, Set<String> lastSuggestions) {
        Set<String> filteredSuggestions = new HashSet<>();
        for (String suggestion : lastSuggestions) {
            if (suggestion.contains(input)) {
                filteredSuggestions.add(suggestion);
            }
        }
        return filteredSuggestions;
    }

    private static Set<String> recalculateSuggestions(String input, RecipeIndexService recipeIndexService, CustomItemRegistry customItemRegistry) {
        Set<NamespacedKey> availableNamespaceKey = new HashSet<>();
        collectNamespaceKey(recipeIndexService.getAllResultItemStacks(), availableNamespaceKey, customItemRegistry);
        collectNamespaceKey(recipeIndexService.getAllIngredientItemStacks(), availableNamespaceKey, customItemRegistry);

        Set<String> suggestions = new HashSet<>();
        for (NamespacedKey namespacedKey : availableNamespaceKey) {
            String namespaceKeyStr = namespacedKey.toString().toLowerCase();
            if (namespaceKeyStr.contains(input.toLowerCase())) {
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
    private static void collectNamespaceKey(Set<ItemStack> itemStacks, Set<NamespacedKey> existingSet, CustomItemRegistry customItemRegistry) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return;
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
    }
}
