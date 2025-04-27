package me.qheilmann.vei.Command;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

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
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

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

                                /craft --id <recipeId>  
                                    Opens the recipe specified by <recipeId> and displays it alongside other recipes with the same result.  
                                    Arguments:  
                                    - --id <recipeId>: The unique ID of the recipe to open.  

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
                                - `/craft iron_ingot as-result minecraft:blasting minecraft:iron_ingot_from_blasting_iron_ore` → Opens recipes for an item as a result  
                                - `/craft iron_ingot as-ingredient minecraft:crafting minecraft:iron_helmet` → Opens recipes for an item as an ingredient  
                                - `/craft --id minecraft:cake` → Opens a specific recipe by ID  
                                - `/craft --all minecraft:smelting` → Shows all recipes for a process  
                                - `/craft --all minecraft:smelting minecraft:baked_potato` → Shows all recipes for a process and opens a specific recipe  

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
                ItemStack itemStack = (ItemStack) args.get("resultItem");
                SearchModeArgument.SearchMode searchMode = (SearchModeArgument.SearchMode) args.get("searchMode");
                Process<?> process = (Process<?>) args.get("process");
                NamespacedKey recipeId = (NamespacedKey) args.get("recipeId");
                byItemAction(player, itemStack, searchMode, process, recipeId);
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
                NamespacedKey recipeId = (NamespacedKey) args.get("recipeId");
                allItemAction(player, process, recipeId);
            })
            .register();
            
        // /craft --help
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("help", "--help"))
            .executes((sender, args) -> {
                helpAction(sender);
            })
            .register();

        // /craft --version
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("version", "--version"))
            .executes((sender, args) -> {
                versionAction(sender);
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
            .executes((sender, args) -> {
                reloadAction(sender);
            })
            .register();
    }

    //#endregion Command

    //#region Action

    private void byIdAction(@NotNull Player player, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        MixedProcessRecipeReader recipeReader;
        try {
            recipeReader = recipeIndex.getById(recipeId);
        } catch (Exception e) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("An error occurred while retrieving the recipe reader: " + e.getMessage(), NamedTextColor.RED));
        }

        if (recipeReader == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No recipe found with the ID '" + recipeId.toString() + "'.", NamedTextColor.RED));
        }

        menuManager.openRecipeMenu(player, recipeReader);
    }

    private void byItemAction(@NotNull Player player, @NotNull ItemStack item, @Nullable SearchModeArgument.SearchMode searchMode, @Nullable Process<?> process, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        MixedProcessRecipeReader recipeReader;
        if (searchMode == SearchMode.AS_RESULT || searchMode == null) {
            recipeReader = recipeIndex.getByResult(item);
        } else if (searchMode == SearchMode.AS_INGREDIENT) {
            recipeReader = recipeIndex.getByIngredient(item);
        } else {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Invalid search mode '" + searchMode.toString() + "'." +
            "Use '" + SearchMode.AS_RESULT.toString() + "' or '" + SearchMode.AS_INGREDIENT.toString() + "'.", NamedTextColor.RED));
        }

        if (recipeReader == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No recipes found for the item '" + item.getType().name() + "'.", NamedTextColor.RED));
        }

        recipeAction(player, recipeReader, process, recipeId);
    }

    private void allItemAction(Player player, Process<?> process, NamespacedKey recipeId) throws WrapperCommandSyntaxException {
       
        MixedProcessRecipeReader recipeReader = recipeIndex.getGlobalIndex();
        if (recipeReader == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No recipes found in the current recipe index.", NamedTextColor.RED));
        }

        recipeAction(player, recipeReader, process, recipeId);
    }

    private void recipeAction(@NotNull Player player, @NotNull MixedProcessRecipeReader recipeReader, @Nullable Process<?> process, @Nullable NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        Objects.requireNonNull(player, "The player cannot be null.");
        Objects.requireNonNull(recipeReader, "The recipeReader cannot be null.");

        if(process == null && recipeId != null) {
            throw new IllegalArgumentException("A recipe ID cannot be used without specifying a process.");
        }

        if (process != null) {
            if(!recipeReader.contains(process)) {
                throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No process named '" + process.getKey() + "' exists in the current recipe index.", NamedTextColor.RED));
            }

            recipeReader.setProcess(process);

            if (recipeId != null) {
                Recipe recipe = recipeIndex.getSingleRecipeById(recipeId);
                if (recipe == null) {
                    throw CommandAPIBukkit.failWithAdventureComponent(Component.text("No recipe found with the ID '" + recipeId.toString() + "'.", NamedTextColor.RED));
                }

                ProcessRecipeReader<?> processRecipeReader = recipeReader.currentProcessRecipeReader();

                boolean isRecipeSet = false;
                try {
                    isRecipeSet = processRecipeReader.unsafeSetRecipe(recipe);
                } catch (ClassCastException ex) {
                    isRecipeSet = false;
                }

                if (!isRecipeSet) {
                    throw CommandAPIBukkit.failWithAdventureComponent(Component.text(
                        "The recipe ID '" + recipeId.toString() + "' could not be found in the process '" + process.getKey() + "' within the current recipe index.",
                        NamedTextColor.RED
                    ));
                }
            }
        }

        menuManager.openRecipeMenu(player, recipeReader);
    }

    private void helpAction(CommandSender sender) {
        sender.sendMessage(buildUsage());
    }

    private void versionAction(CommandSender sender) {
        sender.sendMessage(Component.text("Version: " + VanillaEnoughItems.getVersion(), NamedTextColor.YELLOW));
    }

    private void reloadAction(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("All recipes will be cleared and reloaded. (Note: custom non-vanilla process types will not be restored automatically)", TextColor.color(0xff7777)));
        recipeIndex.unindexAll();
        recipeIndex.indexRecipes(Bukkit.getServer().recipeIterator());
        sender.sendMessage(Component.text("All recipes reloaded.", NamedTextColor.GREEN));
    }

    //#endregion Action

    //#region Usage

    private Component buildUsage() {
        Builder usage = Component.text();

        usage.append(ComponentHeaderLarge("Usage:"))
            .append(Component.newline())

            .append(ComponentHeaderMedium("By item"))
            .append(ComponentUsageCommand("/craft <item> [as-result | as-ingredient [<process> [<recipeId>]]]", "/craft iron_ingot as-result minecraft:blasting minecraft:iron_ingot_from_blasting_iron_ore"))
            .append(ComponentDescription("Opens the possible recipes involving the specified <item>."))
            .append(ComponentHeaderSmall("Arguments"))
            .append(ComponentArgumentDescription("<item>", "The item to search for in recipes."))
            .append(ComponentArgumentDescription("[as-result | as-ingredient]", "Whether to search for recipes where the item is the result or an ingredient. Default: as-result."))
            .append(ComponentArgumentDescription("[<process>]", "If provided, opens directly to that process tab. Default: the first available process."))
            .append(ComponentArgumentDescription("[<recipeId>]", "If provided, opens directly to that specific recipe ID. Default: the first recipe of the process."))
            .append(Component.newline())
            
            .append(ComponentHeaderMedium("By process"))
            .append(ComponentUsageCommand("/craft --all [<process> [<recipeId>]]", "/craft --all minecraft:smelting"))
            .append(ComponentDescription("Displays all recipes grouped by process."))
            .append(ComponentHeaderSmall("Arguments"))
            .append(ComponentArgumentDescription("[<process>]", "If provided, opens directly to that process tab. Default: the first available process."))
            .append(ComponentArgumentDescription("[<recipeId>]", "If provided, opens directly to that specific recipe ID. Default: the first recipe of the process."))
            .append(Component.newline())
            
            .append(ComponentHeaderMedium("By recipe ID:"))
            .append(ComponentUsageCommand("/craft --id <recipeId>", "/craft --id minecraft:cake"))
            .append(ComponentDescription("Opens the recipe specified by <recipeId> and displays it alongside other recipes with the same result."))
            .append(ComponentHeaderSmall("Arguments"))
            .append(ComponentArgumentDescription("<recipeId>", "The unique ID of the recipe to open."))
            .append(Component.newline())
            
            .append(ComponentHeaderMedium("Other commands"))
            .append(ComponentUsageCommand("/craft --help", "/craft --help"))
            .append(ComponentDescription("Displays the help message for the /craft command."))
            .append(ComponentUsageCommand("/craft --version", "/craft --version"))
            .append(ComponentDescription("Displays the version of the plugin."))
            .append(ComponentUsageCommand("/craft --reload", "/craft --reload"))
            .append(ComponentDescription("Reloads the plugin configuration and recipes."))
            .append(Component.newline())
            
            .append(ComponentHeaderMedium("Examples"))
            .append(ComponentExampleCommand("/craft iron_ingot"))
            .append(ComponentExampleCommand("/craft iron_ingot as-ingredient"))
            .append(ComponentExampleCommand("/craft iron_ingot as-result minecraft:blasting minecraft:iron_ingot_from_blasting_iron_ore"))
            .append(ComponentExampleCommand("/craft --all"))
            .append(ComponentExampleCommand("/craft --all minecraft:smelting"))
            .append(ComponentExampleCommand("/craft --all minecraft:smelting minecraft:baked_potato"))
            .append(ComponentExampleCommand("/craft --id minecraft:cake"))
            .append(Component.newline())

            .append(ComponentHeaderSmall("Notes"))
            .append(ComponentDescription("The command sender must be a player to open the GUI."));
            
            return usage.build();
        }
        
        private Component ComponentHeaderLarge(String title) {
        return Component.text(title, NamedTextColor.YELLOW, TextDecoration.BOLD).appendNewline();
    }

    private Component ComponentHeaderMedium(String title) {
        return Component.text(title, NamedTextColor.YELLOW).appendNewline();
    }

    private Component ComponentHeaderSmall(String title) {
        return Component.text(title, NamedTextColor.GRAY, TextDecoration.BOLD).appendNewline();
    }

    /*
     * Builds a component with the usage format and the suggested command.
     * Similar to ComponentCommand, but specifically for usage commands, 
     * the suggested command can be different from the visible format.
     */
    private Component ComponentUsageCommand(String visibleFormat, String sugestCommand) {
        Builder usage = Component.text();

        usage.append(Component.text(visibleFormat, NamedTextColor.DARK_GREEN));
            addHoverAndSuggest(usage, sugestCommand)
            .appendNewline();
        
        return usage.build();
    }

    /*
     * Builds a component with an example command format and the suggested command.
     * Similar to ComponentUsageCommand, but specifically for example commands.
     */
    private Component ComponentExampleCommand(String exampleCommand) {
        Builder usage = Component.text();

        usage.append(Component.text("- ", NamedTextColor.GRAY))
            .append(Component.text(exampleCommand, NamedTextColor.GREEN));
                addHoverAndSuggest(usage, exampleCommand)
            .appendNewline();
        
        return usage.build();
    }

    private Component ComponentDescription(String description) {
        return Component.text(description, NamedTextColor.GRAY).appendNewline();
    }



    private Component ComponentArgumentDescription(String node, String description) {
        Builder component = Component.text();

        component.append(Component.text("- ", NamedTextColor.GRAY))
            .append(Component.text(node, NamedTextColor.GRAY, TextDecoration.UNDERLINED))
            .append(Component.text(": " + description, NamedTextColor.GRAY))
            .appendNewline();

        return component.build();
    }

    private Builder addHoverAndSuggest(Builder baseComponent, String suggestCommand) {
        return baseComponent.hoverEvent(Component.text("Click to suggest: ", NamedTextColor.GRAY)
            .append(Component.text(suggestCommand, NamedTextColor.GREEN)))
            .clickEvent(ClickEvent.suggestCommand(suggestCommand));
    }
}
