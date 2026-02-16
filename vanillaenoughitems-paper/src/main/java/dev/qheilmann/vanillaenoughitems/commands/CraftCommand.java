package dev.qheilmann.vanillaenoughitems.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaper;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.qheilmann.vanillaenoughitems.RecipeServices;
import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.commands.arguments.ProcessArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.RecipeIdArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.RecipeItemArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.SearchModeArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.SearchModeArgument.SearchMode;
import dev.qheilmann.vanillaenoughitems.recipe.index.RecipeIndex;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.gui.player.PlayerDataManager;
import dev.qheilmann.vanillaenoughitems.gui.recipegui.RecipeGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@NullMarked
public class CraftCommand {
    public static final String NAME = "craft";
    public static final String[] ALIASES = {"c", "vei"};
    public static final CommandPermission PERMISSION = CommandPermission.NONE;
    public static final String SHORT_HELP = "Show item recipe";
    public static final String LONG_HELP = "Open a GUI showing recipes, usages, or other informations about bunch of item";
    public static final String USAGE = """

                                    /craft <item>
                                    Type /craft --help for detailled usage instructions and examples.
                                    """;

    private static final TextColor COLOR_PRIMARY = VanillaEnoughItems.veiConfig().style().colorPrimary();
    private static final TextColor COLOR_PRIMARY_VARIANT = VanillaEnoughItems.veiConfig().style().colorPrimaryVariant();
    private static final TextColor COLOR_SECONDARY = VanillaEnoughItems.veiConfig().style().colorSecondary();
    private static final TextColor COLOR_SECONDARY_VARIANT = VanillaEnoughItems.veiConfig().style().colorSecondaryVariant();

    @SuppressWarnings("null")
    private static JavaPlugin plugin;
    @SuppressWarnings("null")
    private static RecipeServices recipeServices;
    @SuppressWarnings("null")
    private static PlayerDataManager playerDataManager;

    private CraftCommand() {}; // Prevent instantiation

    public static void init(JavaPlugin plugin, RecipeServices services, PlayerDataManager playerDataManager) {
        CraftCommand.plugin = plugin;
        CraftCommand.recipeServices = services;
        CraftCommand.playerDataManager = playerDataManager;
    }

    public static CommandAPICommand createBaseCraftCommand() {
        return new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_HELP, LONG_HELP)
            .withUsage(USAGE);
    }

    public static void register(JavaPlugin plugin, RecipeServices services, PlayerDataManager playerDataManager) {
        init(plugin, services, playerDataManager);

        // craft <item> [recipe|usage] [<process>] [<recipeId>]
        createBaseCraftCommand()
            .withArguments(new RecipeItemArgument("resultItem", services.recipeIndex())
                .replaceSuggestions(RecipeItemArgument.argumentSuggestions(services.recipeIndex()))
            )
            .withOptionalArguments(new SearchModeArgument("searchMode", services.recipeIndex())
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    ItemStack item = info.previousArgs().getUnchecked("resultItem");
                    return SearchModeArgument.suggestions(services.recipeIndex(), item);
                }))
            )
            .withOptionalArguments(new ProcessArgument("process", services.recipeIndex())
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    ItemStack item = info.previousArgs().getUnchecked("resultItem");
                    SearchMode searchMode = info.previousArgs().getUnchecked("searchMode");
                    return ProcessArgument.suggestions(services.recipeIndex(), item, searchMode);
                }))
            )
            .withOptionalArguments(new RecipeIdArgument("recipeId", services.recipeIndex())
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    ItemStack item = info.previousArgs().getUnchecked("resultItem");
                    SearchMode searchMode = info.previousArgs().getUnchecked("searchMode");
                    Process process = info.previousArgs().getUnchecked("process");
                    return RecipeIdArgument.suggestions(services.recipeIndex(), item, searchMode, process);
                }))
            )
            .executesPlayer((player, args) -> {
                ItemStack itemStack = args.getUnchecked("resultItem");
                SearchModeArgument.SearchMode searchMode = args.getUnchecked("searchMode");
                Process process = args.getUnchecked("process");
                NamespacedKey recipeId = args.getUnchecked("recipeId");
                byItemAction(player, itemStack, searchMode, process, recipeId);
            })
            .register();
        

        // craft --all [<process>] [<recipeId>]
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("all", "--all"))
            .withOptionalArguments(new ProcessArgument("process", services.recipeIndex())
                .replaceSuggestions(ProcessArgument.argumentSuggestions(services.recipeIndex(), null, null))
            )
            .withOptionalArguments(new RecipeIdArgument("recipeId", services.recipeIndex())
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    Process process = info.previousArgs().getUnchecked("process");
                    return RecipeIdArgument.suggestions(services.recipeIndex(), null, null, process);
                }))
            )
            .executesPlayer((player, args) -> {
                Process process = args.getUnchecked("process");
                NamespacedKey recipeId = args.getUnchecked("recipeId");
                allItemAction(player, process, recipeId);
            })
            .register();


        // craft --id <recipeId>
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("id", "--id"))
            .withArguments(new RecipeIdArgument("recipeId", services.recipeIndex())
                .replaceSuggestions(RecipeIdArgument.argumentSuggestions(services.recipeIndex(), null, null, null))
            )
            .executesPlayer((player, args) -> {
                NamespacedKey recipeId = args.getUnchecked("recipeId");
                byIdAction(player, recipeId);
            })
            .register();


        // craft --help
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("help", "--help"))
            .executes((sender, args) -> {
                helpAction(sender);
            })
            .register();


        // craft --version
        createBaseCraftCommand()
            .withPermission(CommandPermission.OP)
            .withArguments(new MultiLiteralArgument("version", "--version"))
            .executes((sender, args) -> {
                versionAction(sender);
            })
            .register();

        // craft --id <recipeId>

        // craft --reload
        createBaseCraftCommand()
            .withPermission(CommandPermission.OP)
            .withArguments(new MultiLiteralArgument("reload", "--reload"))
            .executes((sender, args) -> {
                sender.sendMessage(Component.text("Note: Recipe index reload, will only re-index all the recipe registered in the server.", NamedTextColor.GOLD));
                sender.sendMessage(Component.text("Reloading recipe index...", NamedTextColor.YELLOW));
                RecipeIndex recipeIndex = recipeServices.recipeIndex();
                recipeIndex.clearIndex();
                recipeIndex.indexRecipe(() -> (plugin.getServer().recipeIterator()));
                sender.sendMessage(Component.text("Recipe index reload initiated...", NamedTextColor.YELLOW));
            })
            .register();

        // hmm that's a bit tricky, we don't know which recipe was indexed before reload
        // we can do all recipe in server, but who know if user register some more?
    }

    //#region Actions

    private static void byIdAction(Player player, NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        MultiProcessRecipeReader reader = recipeServices.recipeIndex().readerByKey(recipeId);

        if (reader == null) {
            Component noRecipeFoundMessage = Component.text().applicableApply(NamedTextColor.RED)
                .append(Component.text("No recipe found with the ID '"))
                .append(Component.text(recipeId.asString()).decorate(TextDecoration.BOLD))
                .append(Component.text("'."))
                .build();

            throw CommandAPIPaper.failWithAdventureComponent(noRecipeFoundMessage);
        }

        createAndOpenGui(player, reader);
    }

    private static void byItemAction(Player player, ItemStack item, SearchModeArgument.@Nullable SearchMode searchMode, @Nullable Process startProcess, @Nullable NamespacedKey startRecipeId) throws WrapperCommandSyntaxException {
        
        if (searchMode == null) {
            searchMode = SearchMode.DEFAULT;
        }
        
        MultiProcessRecipeReader reader = switch (searchMode) {
            case RECIPE -> recipeServices.recipeIndex().readerByResult(item);
            case USAGE -> recipeServices.recipeIndex().readerByIngredient(item);
            default -> throw new UnsupportedOperationException("Search mode " + searchMode + " is not implemented");
        };

        if (reader == null) {
            Component noRecipesFoundMessage = Component.text().applicableApply(NamedTextColor.RED)
                .append(Component.text("No recipes found for "))
                .append(item.displayName().decorate(TextDecoration.BOLD))
                .append(Component.text(" using search mode '"))
                .append(Component.text(searchMode.getName()).decorate(TextDecoration.BOLD))
                .append(Component.text("'."))
                .build();

            throw CommandAPIPaper.failWithAdventureComponent(noRecipesFoundMessage);
        }

        recipeAction(player, reader, startProcess, startRecipeId);
    }

    /**
     * Show all recipes in the recipe index.
     *
     * @param player  the player to open the GUI for
     * @param startProcess the Process to use, or null for all processes
     * @param startRecipeId the NamespacedKey of the recipe to start with, or null for no specific recipe
     */
    private static void allItemAction(Player player, @Nullable Process startProcess, @Nullable NamespacedKey startRecipeId) throws WrapperCommandSyntaxException {
        MultiProcessRecipeReader reader = recipeServices.recipeIndex().readerWithAllRecipes();
        recipeAction(player, reader, startProcess, startRecipeId);
    }

    /**
     * Use the provided reader, set the starting process and starting recipe ID if provided, then open the GUI.
     *
     * @param player   the player to open the GUI for
     * @param reader   the MultiProcessRecipeReader containing the recipes to display
     * @param startProcess  the Process to use, or null for non starting process
     * @param startRecipeId the NamespacedKey of the recipe to start with, or null for no specific recipe
     * @throws WrapperCommandSyntaxException if any errors occur during command execution
     */
    private static void recipeAction(Player player, MultiProcessRecipeReader reader, @Nullable Process startProcess, @Nullable NamespacedKey startRecipeId) throws WrapperCommandSyntaxException {
        if(startProcess == null && startRecipeId != null) {
            throw new IllegalArgumentException("A recipe ID cannot be used without specifying a process.");
        }

        // Set start process if provided
        if (startProcess != null) {
            if(!reader.containsProcess(startProcess)) {
                Component processNotFoundMessage = Component.text().applicableApply(NamedTextColor.RED)
                    .append(Component.text("No process named '"))
                    .append(Component.text(startProcess.key().asString()).decorate(TextDecoration.BOLD))
                    .append(Component.text("' found in your current search."))
                    .build();

                throw CommandAPIPaper.failWithAdventureComponent(processNotFoundMessage);
            }

            reader.setCurrentProcess(startProcess);

            // Set start recipe if provided
            if (startRecipeId != null) {
                Recipe recipe = recipeServices.recipeIndex().getSingleRecipeByKey(startRecipeId);
                if (recipe == null) {
                    Component recipeNotFoundMessage = Component.text().applicableApply(NamedTextColor.RED)
                        .append(Component.text("No recipe found with the ID '"))
                        .append(Component.text(startRecipeId.asString()).decorate(TextDecoration.BOLD))
                        .append(Component.text("'."))
                        .build();

                    throw CommandAPIPaper.failWithAdventureComponent(recipeNotFoundMessage);
                }

                if (!reader.getCurrentProcessRecipeReader().contains(recipe)) {
                    Component recipeNotFoundInProcessMessage = Component.text().applicableApply(NamedTextColor.RED)
                        .append(Component.text("The recipe ID '"))
                        .append(Component.text(startRecipeId.asString()).decorate(TextDecoration.BOLD))
                        .append(Component.text("' could not be found in the process '"))
                        .append(Component.text(startProcess.key().asString()).decorate(TextDecoration.BOLD))
                        .append(Component.text("' within your current search."))
                        .build();

                    throw CommandAPIPaper.failWithAdventureComponent(recipeNotFoundInProcessMessage);
                }

                reader.getCurrentProcessRecipeReader().setCurrent(recipe);
            }
        }

        createAndOpenGui(player, reader);
    }

    private static void versionAction(CommandSender sender) {
        String version = CraftCommand.plugin.getPluginMeta().getVersion();

        Component text = Component.text().applicableApply(NamedTextColor.YELLOW)
            .append(Component.text("VanillaEnoughItems version: "))
            .append(Component.text(version).decorate(TextDecoration.BOLD))
            .build();

        sender.sendMessage(text);
    }

    //#endregion Actions

    /**
     * Create, render and open the recipe GUI for the player.
     *
     * @param player  the player to open the GUI for
     * @param reader  the MultiProcessRecipeReader containing the recipes to display
     */
    private static void createAndOpenGui(Player player, MultiProcessRecipeReader reader) {
        RecipeGui gui = new RecipeGui(recipeServices, playerDataManager.getPlayerData(player.getUniqueId()), reader);
        gui.render();
        gui.open(player);
    }

    //#region Help

    private static void helpAction(CommandSender sender) {
        Component helpMessage = Component.text()
            // Title
            .append(Component.text(VanillaEnoughItems.PLUGIN_NAME + " Help", COLOR_PRIMARY, TextDecoration.BOLD).appendNewline())
            .append(Component.text("------------------------------", COLOR_PRIMARY_VARIANT).appendNewline())
            
            // By Item
            .appendNewline()
            .append(helpCommandPrototype("/craft <item> [recipe|usage] [<process>] [<recipeId>]", "/craft "))
            .append(helpDescription("Open the recipe GUI for a specific item, showing either recipes or usages."))
            .append(helpSubTitle("Arguments:"))
            .append(helpArgument("<item>", false, "The item to show recipes/usages for"))
            .append(helpArgument("[recipe|usage]", true, "Search mode, default: 'recipe'"))
            .append(helpArgument("[<process>]", true, "Preselect a process"))
            .append(helpArgument("[<recipeId>]", true, "Preselect a recipe"))
            .append(helpSubTitle("Examples:"))
            .append(helpExample("/craft diamond_sword"))
            .append(helpExample("/craft minecraft:iron_ingot usage"))
            .append(helpExample("/craft minecraft:salmon usage minecraft:smoking minecraft:cooked_salmon_from_smoking"))

            // All Items
            .appendNewline()
            .append(helpCommandPrototype("/craft --all [<process>] [<recipeId>]", "/craft --all"))
            .append(helpDescription("Open the recipe GUI showing all recipes classified by process."))
            .append(helpSubTitle("Arguments:"))
            .append(helpArgument("[<process>]", true, "Preselect a process"))
            .append(helpArgument("[<recipeId>]", true, "Preselect a recipe"))
            .append(helpSubTitle("Examples:"))
            .append(helpExample("/craft --all"))
            .append(helpExample("/craft --all minecraft:smithing minecraft:netherite_axe_smithing"))

            // By Recipe ID
            .appendNewline()
            .append(helpCommandPrototype("/craft --id <recipeId>", "/craft --id "))
            .append(helpDescription("Open the recipe GUI for a specific recipe by its ID."))
            .append(helpSubTitle("Argument:"))
            .append(helpArgument("<recipeId>", false, "The recipe to display"))
            .append(helpSubTitle("Example:"))
            .append(helpExample("/craft --id minecraft:cooked_salmon_from_smoking"))

            // Other Commands
            .appendNewline()
            .append(Component.text("Other Commands", COLOR_PRIMARY, TextDecoration.BOLD).appendNewline())
            .append(helpShortStaticPrototypeAndDesc("/craft --help", "Show this help message"))
            .append(helpShortStaticPrototypeAndDesc("/craft --version", "Show the plugin version"))

            // Footer
            .append(Component.text("------------------------------", COLOR_PRIMARY_VARIANT))

            .build();

        sender.sendMessage(helpMessage);
    }

    private static Component helpCommandPrototype(String prototype, String suggest) {
        return Component.text(prototype, COLOR_SECONDARY, TextDecoration.BOLD)
            .clickEvent(ClickEvent.suggestCommand(suggest))
            .appendNewline();
    }

    private static Component helpDescription(String string) {
        return Component.text(string, COLOR_PRIMARY_VARIANT).appendNewline();
    }
    
    private static Component helpSubTitle(String string) {
        return Component.text(string, COLOR_PRIMARY).appendNewline();
    }

    private static Component helpArgument(String arg, boolean optional, String desc) {
        return Component.text()
            .append(Component.text("  ")) // Indentation
            .append(Component.text(arg, COLOR_SECONDARY))
            .append(optional ? Component.text(" (Optional) ", COLOR_PRIMARY_VARIANT, TextDecoration.ITALIC) : Component.empty())
            .append(Component.text(desc, COLOR_PRIMARY_VARIANT))
            .appendNewline()
            .build();
    }

    private static Component helpExample(String example) {
        return Component.text()
            .append(Component.text("  ")) // Indentation
            .append(Component.text(example, COLOR_SECONDARY_VARIANT))
            .hoverEvent(Component.text("Click to suggest this command", COLOR_SECONDARY_VARIANT))
            .clickEvent(ClickEvent.suggestCommand(example))
            .appendNewline()
            .build();
    }

    private static Component helpShortStaticPrototypeAndDesc(String prototype, String desc) {
        return Component.text()
            .append(Component.text("  ")) // Indentation
            .append(Component.text(prototype, COLOR_SECONDARY, TextDecoration.BOLD)
                .hoverEvent(Component.text("Click to suggest this command", COLOR_SECONDARY_VARIANT))
                .clickEvent(ClickEvent.suggestCommand(prototype))
            )
            .append(Component.text(" " + desc, COLOR_PRIMARY_VARIANT))
            .appendNewline()
            .build();
    }

    //#endregion Help
}
