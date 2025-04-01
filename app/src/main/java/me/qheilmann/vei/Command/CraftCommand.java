package me.qheilmann.vei.Command;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.bukkit.NamespacedKey;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Menu.MenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CraftCommand implements ICommand{
    public static final String NAME = "craft";
    public static final String[] ALIASES = {"c", "vei"};
    public static final String SHORT_DESCRIPTION = "Show item recipe";
    public static final String LONG_DESCRIPTION = "Open a GUI showing the recipe of an item, it can be crafting, smelting, brewing, etc.";
    public static final CommandPermission PERMISSION = CommandPermission.NONE;
    // TODO: implement a better help system with the command API (separated --help with adventure component, and /help craft just show really basic help + "type /craft --help for more information")
    public static final String USAGE = """
                                [WORK IN PROGRESS]
                                Not all arguments are implemented yet.

                                /craft <item> [as-result | as-ingredient [<process> [<recipeId>]]]  
                                    Opens the possible recipes involving the specified <item>.  
                                    Arguments:  
                                    - <item>: The item to search for in recipes.  
                                    - [as-result | as-ingredient]: Whether to search for recipes where the item is the result or an ingredient. Default: as-result.  
                                    - [<process>]: If provided, opens directly to that process tab. Default: the first available process.  
                                    - [<recipeId>]: If provided, opens directly to that specific recipe ID. Default: the first recipe of the process.  

                                /craft --id=<recipeId>  
                                    Opens the recipe specified by <recipeId> and displays it alongside other recipes with the same result.  
                                    Arguments:  
                                    - --id=<recipeId>: The unique ID of the recipe to open.  

                                /craft --all [<process> [<recipeId>]]  
                                    Displays all recipes grouped by process.  
                                    Arguments:  
                                    - [<process>]: If provided, opens directly to that process tab. Default: the first available process.  
                                    - [<recipeId>]: If provided, opens directly to that specific recipe ID. Default: the first recipe of the process.  

                                /craft --help  
                                    Displays the help message for the /craft command.  

                                /craft --version  
                                    Displays the version of the plugin.  

                                /craft --reload  
                                    Reloads the plugin configuration and recipes.  

                                Example Usage:  
                                - `/craft iron_ingot as-result smelting minecraft:iron_ingot_from_blasting_iron_ore` → Opens recipes for an item as a result  
                                - `/craft iron_ingot as-ingredient crafting minecraft:iron_helmet` → Opens recipes for an item as an ingredient  
                                - `/craft --id=minecraft:cake` → Opens a specific recipe by ID  
                                - `/craft --all smelting` → Shows all recipes for a process  
                                - `/craft --all smelting minecraft:baked_potato` → Shows all recipes for a process and opens a specific recipe  

                                Notes:  
                                - The command sender must be a player to open the GUI.  
                                        """;

    private MenuManager menuManager;
    private RecipeIndexService recipeIndex;

    public CraftCommand(MenuManager menuManager, RecipeIndexService recipeIndex) {
        this.menuManager = menuManager;
        this.recipeIndex = recipeIndex;
    }

    @Override
    public void register() {
        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_DESCRIPTION, LONG_DESCRIPTION)
            .withUsage(USAGE)
            // Display name of the item but the ItemStack back conversion behind is missing
            // .withArguments(new StringArgument("item").replaceSuggestions(
            //     ArgumentSuggestions.strings(info -> VanillaEnoughItems.allRecipesMap.getItems().stream().map(t -> t.getI18NDisplayName()).toArray(String[]::new)))
            // )
            // A try with the brigadier builder 
            // (next time try with the builder.sugget to maybe force suggestions even if the start doesn't match (minecraft: prefix and argument))
            // https://commandapi.jorel.dev/9.6.0/brigadiersuggestions.html?highlight=emoji#example---making-an-emoji-broadcasting-message
            // .withArguments(new GreedyStringArgument("item").replaceSuggestions((info, builder) -> {
            //     builder = builder.createOffset(builder.getStart() + info.currentArg().length());
            //     builder.suggest("item");
            // }))
            // .withArguments(new ItemStackArgument("item").replaceSafeSuggestions(
            //     SafeSuggestions.suggest(info -> {
            //         return VanillaEnoughItems.allRecipesMap.getItems().stream().filter(item ->
            //             // This filter isn't really efficient: it compares the beginning of the argument with all possible elements
            //             // BUT at the same time checks with the default prefix "minecraft:", so only minecraft:minecart works for a the 5 first characters.
            //             // {
            //             //     VanillaEnoughItems.LOGGER.info("item: " + item.getType().name().toLowerCase() + " input: " + info.currentArg().toLowerCase() + " match: " + item.getType().name().toLowerCase().startsWith(info.currentArg().toLowerCase()));
            //             //     return item.getType().name().toLowerCase().startsWith(info.currentArg().toLowerCase());
            //             // }
            //             {
            //                 return true;
            //             }
            //         ).toArray(ItemStack[]::new);
            //     })
            // ))
            .withArguments(new NamespacedKeyArgument("recipeId").replaceSuggestions(
                ArgumentSuggestions.stringCollection(
                    info -> recipeIndex.getAllRecipeIds().stream()
                        .map(t -> t.getNamespace() + ":" + t.getKey())
                        .toList()
                ))
            )
            // .withOptionalArguments(VEICommandArguments.processArgument("process"))
            // .withOptionalArguments(new IntegerArgument("variant"))
            .executesPlayer((player, args) -> {
                // ItemStack itemStack = (ItemStack) args.get("item");
                // Process<?> process = (Process<?>) args.get("process");
                // int variant = (int) args.getOrDefault("variant", 1) - 1; // only 1-based for the final user, otherwise it's 0-based
                NamespacedKey recipeId = (NamespacedKey) args.get("recipeId");

                byIdAction(player, recipeId);
                // byItemAction(player, (ItemStack) args.get("item"), SearchMode.AS_RESULT, null, recipeId);
            })
            .register();
    }

    // Action

    private void byIdAction(@NotNull Player player, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        Recipe recipe = recipeIndex.getById(recipeId);
        if (recipe == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found: " + recipeId.toString(), NamedTextColor.RED));
        }

        ItemStack result = recipe.getResult();
        if (result == null || result.isEmpty() || result.getType().isAir()) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe result not found: " 
                + recipeId.toString() + ". Complexe recipes are not supported yet.", NamedTextColor.RED));
        }

        // For the moment simply take the result instead of right result variant
        MixedProcessRecipeReader recipeReader = recipeIndex.getByResult(result);

        if (recipeReader == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No recipes with result: ").append(result.displayName()).color(NamedTextColor.RED));
        }

        // TEMP Adapter to old API
        Process<?> process = recipeReader.currentProcess();
        Recipe[] RecipeArray = recipeReader.currentProcessRecipeReader().getAllRecipes().toArray(Recipe[]::new);
        int variant = 0;
        for (int i = 0; i < RecipeArray.length; i++) {
            if (RecipeArray[i].getResult().isSimilar(result)) {
                variant = i;
                break;
            }
        }

        openRecipe(player, result, process, variant);
    }

    private void byItemAction(@NotNull Player player, @NotNull ItemStack item, SearchMode searchMode, @Nullable Process<?> process, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        if (searchMode == SearchMode.AS_RESULT) {
            byResultAction(player, item, process, recipeId);
        } else if (searchMode == SearchMode.AS_INGREDIENT) {
            byIngredientAction(player, item, process, recipeId);
        } else {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Invalid search mode: " + searchMode.toString() + "." +
            " Use '" + SearchMode.AS_RESULT.toString() + "' or '" + SearchMode.AS_INGREDIENT.toString() + "'."
            , NamedTextColor.RED));
        }
    }

    // Utils

    private void byResultAction(@NotNull Player player, @NotNull ItemStack resultItem, @Nullable Process<?> process, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        MixedProcessRecipeReader mixedProcessRecipeReader = recipeIndex.getByResult(resultItem);
        if (mixedProcessRecipeReader == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No recipes with result: " + resultItem.getType().name(), NamedTextColor.RED));
        }

        if (process != null) {
            try {
                mixedProcessRecipeReader.setProcess(process);
            } catch (IllegalArgumentException e) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No process found with name: " + process.getProcessName()
                + " for this recipe.", NamedTextColor.RED));
            }
        }

        if (recipeId != null) {
            Recipe recipe = recipeIndex.getById(recipeId);
            if (recipe == null) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found: " + recipeId.toString(), NamedTextColor.RED));
            }
            ProcessRecipeReader<?> processRecipeReader = mixedProcessRecipeReader.currentProcessRecipeReader();
            boolean isRecipeSet = processRecipeReader.unsafeSetRecipe(recipe);
            if (!isRecipeSet) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found in the process: " + recipeId.toString(), NamedTextColor.RED));
            }
        }

        // Here mixedProcessRecipeReader is correctly set to be used
        

        // TEMP Adapter to old API
        Recipe[] RecipeArray = mixedProcessRecipeReader.currentProcessRecipeReader().getAllRecipes().toArray(Recipe[]::new);
        int variant = 0;
        for (int i = 0; i < RecipeArray.length; i++) {
            if (RecipeArray[i].getResult().isSimilar(resultItem)) {
                variant = i;
                break;
            }
        }

        openRecipe(player, resultItem, mixedProcessRecipeReader.currentProcess(), variant);
    }

    private void byIngredientAction(@NotNull Player player, @NotNull ItemStack ingredientItem, @Nullable Process<?> process, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        MixedProcessRecipeReader mixedProcessRecipeReader = recipeIndex.getByIngredient(ingredientItem);
        if (mixedProcessRecipeReader == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No recipes with ingredient: " + ingredientItem.getType().name(), NamedTextColor.RED));
        }

        if (process != null) {
            try {
                mixedProcessRecipeReader.setProcess(process);
            } catch (IllegalArgumentException e) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No process found with name: " + process.getProcessName()
                + " for this recipe.", NamedTextColor.RED));
            }
        }

        if (recipeId != null) {
            Recipe recipe = recipeIndex.getById(recipeId);
            if (recipe == null) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found: " + recipeId.toString(), NamedTextColor.RED));
            }
            ProcessRecipeReader<?> processRecipeReader = mixedProcessRecipeReader.currentProcessRecipeReader();
            boolean isRecipeSet = processRecipeReader.unsafeSetRecipe(recipe);
            if (!isRecipeSet) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found in the process: " + recipeId.toString(), NamedTextColor.RED));
            }
        }

        // Here mixedProcessRecipeReader is correctly set to be used

        // TEMP Adapter to old API
        Recipe[] RecipeArray = mixedProcessRecipeReader.currentProcessRecipeReader().getAllRecipes().toArray(Recipe[]::new);
        int variant = 0;
        for (int i = 0; i < RecipeArray.length; i++) {
            if (RecipeArray[i].getResult().isSimilar(ingredientItem)) {
                variant = i;
                break;
            }
        }

        openRecipe(player, ingredientItem, mixedProcessRecipeReader.currentProcess(), variant);
    }

    private void openRecipe(@NotNull Player player, @NotNull ItemStack item, @Nullable Process<?> process, int variant) {
        menuManager.openRecipeMenu(player, item, process, variant);
        // TODO add a global try catch here
    }

    private enum SearchMode {
        AS_RESULT,
        AS_INGREDIENT;

        public static SearchMode fromString(String mode) {
            return switch (mode.toLowerCase()) {
                case "as-result" -> AS_RESULT;
                case "as-ingredient" -> AS_INGREDIENT;
                default -> throw new IllegalArgumentException("Invalid search mode: " + mode);
            };
        }
    }
}
