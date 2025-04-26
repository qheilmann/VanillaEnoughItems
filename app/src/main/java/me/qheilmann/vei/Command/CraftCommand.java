package me.qheilmann.vei.Command;

import java.util.Collection;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Command.CustomArguments.ProcessArgument;
import me.qheilmann.vei.Command.CustomArguments.RecipeIdArgument;
import me.qheilmann.vei.Command.CustomArguments.RecipeItemArgument;
import me.qheilmann.vei.Command.CustomArguments.SearchModeArgument;
import me.qheilmann.vei.Command.CustomArguments.SearchModeArgument.SearchMode;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Recipe.Index.RecipeIndexService;
import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;
import me.qheilmann.vei.Core.Recipe.Index.Reader.ProcessRecipeReader;
import me.qheilmann.vei.Menu.MenuManager;
import me.qheilmann.vei.Service.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

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
    private CustomItemRegistry customItemRegistry;

    public CraftCommand(MenuManager menuManager, RecipeIndexService recipeIndex, CustomItemRegistry customItemRegistry) {
        this.menuManager = menuManager;
        this.recipeIndex = recipeIndex;
        this.customItemRegistry = customItemRegistry;
    }

    //#region Command

    public CommandAPICommand createBaseCraftCommand() {
        return new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_DESCRIPTION, LONG_DESCRIPTION)
            .withUsage(USAGE);
    }

    @Override
    public void register() {

        // /craft <item> [as-result | as-ingredient [<process> [<recipeId>]]]
        createBaseCraftCommand()
            .withArguments(new RecipeItemArgument("resultItem", recipeIndex, customItemRegistry)
                .replaceSuggestions(RecipeItemArgument.argumentSuggestionsFrom(recipeIndex, customItemRegistry))
            )
            .withOptionalArguments(new SearchModeArgument("searchMode")
                .replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                    ItemStack item = (ItemStack) info.previousArgs().get("resultItem");
                    return SearchModeArgument.suggestionsFrom(recipeIndex, item);
                }))
            )
            .withOptionalArguments(new ProcessArgument("process", recipeIndex)
                .replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                    ItemStack item = (ItemStack) info.previousArgs().get("resultItem");
                    SearchMode searchMode = (SearchMode) info.previousArgs().get("searchMode");
                    return ProcessArgument.suggestionsFrom(recipeIndex, item, searchMode)
                        .thenApply(treeSet -> (Collection<String>) treeSet);
                }))
            )
            .withOptionalArguments(new RecipeIdArgument("recipeId", recipeIndex)
                .replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                    ItemStack item = (ItemStack) info.previousArgs().get("resultItem");
                    SearchMode searchMode = (SearchMode) info.previousArgs().get("searchMode");
                    Process<?> process = (Process<?>) info.previousArgs().get("process");
                    return RecipeIdArgument.suggestionsFrom(recipeIndex, item, searchMode, process);
                }))
            )
            .executesPlayer((player, args) -> {
                // ItemStack itemStack = (ItemStack) args.get("item");
                // Process<?> process = (Process<?>) args.get("process");
                // int variant = (int) args.getOrDefault("variant", 1) - 1; // only 1-based for the final user, otherwise it's 0-based
                ItemStack resultItemStack = (ItemStack) args.get("resultItem");
                SearchModeArgument.SearchMode searchMode = (SearchModeArgument.SearchMode) args.get("searchMode");
                player.getInventory().addItem(resultItemStack);

                // byIdAction(player, recipeId);
                // byItemAction(player, (ItemStack) args.get("item"), SearchMode.AS_RESULT, null, recipeId);
            })
            .register();
        
        // /craft --all [<process> [<recipeId>]]
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("all", "--all"))
            .withOptionalArguments(new ProcessArgument("process", recipeIndex)
                .replaceSuggestions(ProcessArgument.argumentSuggestionsFrom(recipeIndex, null, null))
            )
            .withOptionalArguments(new RecipeIdArgument("recipeId", recipeIndex)
                .replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                    Process<?> process = (Process<?>) info.previousArgs().get("process");
                    return RecipeIdArgument.suggestionsFrom(recipeIndex, null, null, process);
                }))
            )
            .executesPlayer((player, args) -> {
                Process<?> process = (Process<?>) args.get("process");
                player.sendMessage(Component.text(process.getProcessIcon().getType().toString(), NamedTextColor.BLUE));
            })
            .register();
            
        // /craft --help
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("help", "--help"))
            .executesPlayer((player, args) -> {
                helpAction(player);
            })
            .register();

        // /craft --version
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("version", "--version"))
            .executesPlayer((player, args) -> {
                versionAction(player);
            })
            .register();

        // /craft --id <recipeId>
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("id", "--id"))
            .withArguments(new RecipeIdArgument("recipeId", recipeIndex)
                .replaceSuggestions(RecipeIdArgument.argumentSuggestionsFrom(recipeIndex, null, null, null))
            )
            .executesPlayer((player, args) -> {
                NamespacedKey recipeId = (NamespacedKey) args.get("recipeId");
                byIdAction(player, recipeId);
            })
            .register();

        // Requires OP permission. Note: Due to the first argument, the --reload suggestion still appears even if the permission requirement is not met. (but will not execute)
        // /craft --reload
        createBaseCraftCommand()
            .withArguments(new LiteralArgument("--reload")
                .withPermission(CommandPermission.OP)
            )
            .executesPlayer((player, args) -> {
                reloadAction(player);
            })
            .register();
    }

    //#endregion Command

    //#region Action

    private void byIdAction(@NotNull Player player, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {

        MixedProcessRecipeReader recipeReader;

        // // TODO TEMP
        // ItemStack resultItem;
        // try {
        //     resultItem = recipeIndex.getSingleRecipeById(recipeId).getResult();
        // } catch (Exception e) {
        //     throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Failed to retrieve the first ItemStack of the recipe: " + e.getMessage(), NamedTextColor.RED));
        // }

        // if (resultItem == null) {
        //     throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No result ItemStack found for recipe ID: " + recipeId.toString(), NamedTextColor.RED));
        // }

        // recipeReader = recipeIndex.getByIngredient(resultItem);

        // // TODO TEMP

        try {
            recipeReader = recipeIndex.getById(recipeId);
        } catch (Exception e) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("An error occurred while retrieving the recipe reader: " + e.getMessage(), NamedTextColor.RED));
        }

        if (recipeReader == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found: " + recipeId.toString(), NamedTextColor.RED));
        }

        menuManager.openRecipeMenu(player, recipeReader);
    }

    private void byItemAction(@NotNull Player player, @NotNull ItemStack item, SearchModeArgument.SearchMode searchMode, @Nullable Process<?> process, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
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

    private void helpAction(Player player) {
        player.sendMessage(Component.text(USAGE, NamedTextColor.YELLOW));
    }

    private void versionAction(Player player) {
        player.sendMessage(Component.text("Version: " + VanillaEnoughItems.getVersion(), NamedTextColor.YELLOW));
    }

    private void reloadAction(@NotNull Player player) {
        player.sendMessage(Component.text("All recipes will be cleared and reloaded, excluding non-vanilla processes.", TextColor.color(0xff7777)));
        recipeIndex.unindexAll();
        recipeIndex.indexRecipes(Bukkit.getServer().recipeIterator());
        player.sendMessage(Component.text("All recipes reloaded.", NamedTextColor.GREEN));
    }

    //#endregion Action

    //#region Utils

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
            Recipe recipe = recipeIndex.getSingleRecipeById(recipeId);
            if (recipe == null) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found: " + recipeId.toString(), NamedTextColor.RED));
            }
            ProcessRecipeReader<?> processRecipeReader = mixedProcessRecipeReader.currentProcessRecipeReader();
            boolean isRecipeSet = processRecipeReader.unsafeSetRecipe(recipe);
            if (!isRecipeSet) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found in the process: " + recipeId.toString(), NamedTextColor.RED));
            }
        }

        menuManager.openRecipeMenu(player, mixedProcessRecipeReader);
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
            Recipe recipe = recipeIndex.getSingleRecipeById(recipeId);
            if (recipe == null) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found: " + recipeId.toString(), NamedTextColor.RED));
            }
            ProcessRecipeReader<?> processRecipeReader = mixedProcessRecipeReader.currentProcessRecipeReader();
            boolean isRecipeSet = processRecipeReader.unsafeSetRecipe(recipe);
            if (!isRecipeSet) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found in the process: " + recipeId.toString(), NamedTextColor.RED));
            }
        }

        menuManager.openRecipeMenu(player, mixedProcessRecipeReader);
    }

    //#endregion Utils
}
