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
import dev.qheilmann.vanillaenoughitems.commands.arguments.ProcessArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.RecipeIdArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.RecipeItemArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.SearchModeArgument;
import dev.qheilmann.vanillaenoughitems.commands.arguments.SearchModeArgument.SearchMode;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGui;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
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

    @SuppressWarnings("null")
    private static JavaPlugin plugin;

    private CraftCommand() {}; // Prevent instantiation

    public static void init(JavaPlugin plugin, RecipeGuiContext context) {
        CraftCommand.plugin = plugin;
    }

    public static CommandAPICommand createBaseCraftCommand() {
        return new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_HELP, LONG_HELP)
            .withUsage(USAGE);
    }

    public static void register(JavaPlugin plugin, RecipeGuiContext context) {
        init(plugin, context);

        // craft <item> [as-result|as-ingredient] [<process>] [<recipeId>]
        createBaseCraftCommand()
            .withArguments(new RecipeItemArgument("resultItem", context)
                .replaceSuggestions(RecipeItemArgument.argumentSuggestions(context))
            )
            .withOptionalArguments(new SearchModeArgument("searchMode", context)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    ItemStack item = info.previousArgs().getUnchecked("resultItem");
                    return SearchModeArgument.suggestions(context, item);
                }))
            )
            .withOptionalArguments(new ProcessArgument("process", context)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    ItemStack item = info.previousArgs().getUnchecked("resultItem");
                    SearchMode searchMode = info.previousArgs().getUnchecked("searchMode");
                    return ProcessArgument.suggestions(context, item, searchMode);
                }))
            )
            .withOptionalArguments(new RecipeIdArgument("recipeId", context)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    ItemStack item = info.previousArgs().getUnchecked("resultItem");
                    SearchMode searchMode = info.previousArgs().getUnchecked("searchMode");
                    Process process = info.previousArgs().getUnchecked("process");
                    return RecipeIdArgument.suggestions(context, item, searchMode, process);
                }))
            )
            .executesPlayer((player, args) -> {
                ItemStack itemStack = args.getUnchecked("resultItem");
                SearchModeArgument.SearchMode searchMode = args.getUnchecked("searchMode");
                Process process = args.getUnchecked("process");
                NamespacedKey recipeId = args.getUnchecked("recipeId");
                byItemAction(player, itemStack, searchMode, process, recipeId, context);
            })
            .register();
        

        // craft --all [<process>] [<recipeId>]
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("all", "--all"))
            .withOptionalArguments(new ProcessArgument("process", context)
                .replaceSuggestions(ProcessArgument.argumentSuggestions(context, null, null))
            )
            .withOptionalArguments(new RecipeIdArgument("recipeId", context)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                    Process process = info.previousArgs().getUnchecked("process");
                    return RecipeIdArgument.suggestions(context, null, null, process);
                }))
            )
            .executesPlayer((player, args) -> {
                Process process = args.getUnchecked("process");
                NamespacedKey recipeId = args.getUnchecked("recipeId");
                allItemAction(player, process, recipeId, context);
            })
            .register();


        // craft --id <recipeId>
        createBaseCraftCommand()
            .withArguments(new MultiLiteralArgument("id", "--id"))
            .withArguments(new RecipeIdArgument("recipeId", context)
                .replaceSuggestions(RecipeIdArgument.argumentSuggestions(context, null, null, null))
            )
            .executesPlayer((player, args) -> {
                NamespacedKey recipeId = args.getUnchecked("recipeId");
                byIdAction(player, recipeId, context);
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
            .withArguments(new MultiLiteralArgument("version", "--version"))
            .executes((sender, args) -> {
                versionAction(sender);
            })
            .register();

        // craft --id <recipeId>


        // craft --reload
        // hmm that's a bit tricky, we don't know which recipe was indexed before reload
        // we can do all recipe in server, but who know if user register some more?
    }

    //#region Actions

    private static void byIdAction(Player player, NamespacedKey recipeId, RecipeGuiContext context) throws WrapperCommandSyntaxException {
        MultiProcessRecipeReader reader = context.getRecipeIndexReader().readerByKey(recipeId);

        if (reader == null) {
            Component noRecipeFoundMessage = Component.text().applicableApply(NamedTextColor.RED)
                .append(Component.text("No recipe found with the ID '"))
                .append(Component.text(recipeId.asString()).decorate(TextDecoration.BOLD))
                .append(Component.text("'."))
                .build();

            throw CommandAPIPaper.failWithAdventureComponent(noRecipeFoundMessage);
        }

        createAndOpenGui(player, reader, context);
    }

    private static void byItemAction(Player player, ItemStack item, SearchModeArgument.@Nullable SearchMode searchMode, @Nullable Process process, @Nullable NamespacedKey recipeId, RecipeGuiContext context) throws WrapperCommandSyntaxException {
        
        if (searchMode == null) {
            searchMode = SearchMode.DEFAULT;
        }
        
        MultiProcessRecipeReader reader = switch (searchMode) {
            case RECIPE -> context.getRecipeIndexReader().readerByResult(item);
            case USAGE -> context.getRecipeIndexReader().readerByIngredient(item);
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

        recipeAction(player, reader, process, recipeId, context);
    }

    private static void allItemAction(Player player, @Nullable Process process, @Nullable NamespacedKey recipeId, RecipeGuiContext context) throws WrapperCommandSyntaxException {
        MultiProcessRecipeReader reader = context.getRecipeIndexReader().readerWithAllRecipes();
        recipeAction(player, reader, process, recipeId, context);
    }

    private static void recipeAction(Player player, MultiProcessRecipeReader reader, @Nullable Process process, @Nullable NamespacedKey recipeId, RecipeGuiContext context) throws WrapperCommandSyntaxException {
        if(process == null && recipeId != null) {
            throw new IllegalArgumentException("A recipe ID cannot be used without specifying a process.");
        }

        // Set start process if provided
        if (process != null) {
            if(!reader.containsProcess(process)) {
                Component processNotFoundMessage = Component.text().applicableApply(NamedTextColor.RED)
                    .append(Component.text("No process named '"))
                    .append(Component.text(process.key().asString()).decorate(TextDecoration.BOLD))
                    .append(Component.text("' exists in the current recipe index."))
                    .build();

                throw CommandAPIPaper.failWithAdventureComponent(processNotFoundMessage);
            }

            reader.setCurrentProcess(process);

            // Set start recipe if provided
            if (recipeId != null) {
                Recipe recipe = context.getRecipeIndexReader().getSingleRecipeByKey(recipeId);
                if (recipe == null) {
                    Component recipeNotFoundMessage = Component.text().applicableApply(NamedTextColor.RED)
                        .append(Component.text("No recipe found with the ID '"))
                        .append(Component.text(recipeId.asString()).decorate(TextDecoration.BOLD))
                        .append(Component.text("'."))
                        .build();

                    throw CommandAPIPaper.failWithAdventureComponent(recipeNotFoundMessage);
                }

                if (!reader.getCurrentProcessRecipeReader().contains(recipe)) {
                    Component recipeNotFoundInProcessMessage = Component.text().applicableApply(NamedTextColor.RED)
                        .append(Component.text("The recipe ID '"))
                        .append(Component.text(recipeId.asString()).decorate(TextDecoration.BOLD))
                        .append(Component.text("' could not be found in the process '"))
                        .append(Component.text(process.key().asString()).decorate(TextDecoration.BOLD))
                        .append(Component.text("' within the current recipe index."))
                        .build();

                    throw CommandAPIPaper.failWithAdventureComponent(recipeNotFoundInProcessMessage);
                }

                reader.getCurrentProcessRecipeReader().setCurrent(recipe);
            }
        }

        createAndOpenGui(player, reader, context);
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

    //#region Help

    private static void helpAction(CommandSender sender) {
        Component title = Component.text("VanillaEnoughItems Help")
            .style(Style.style(NamedTextColor.GOLD, TextDecoration.BOLD, TextDecoration.UNDERLINED));

        // TODO add help

        sender.sendMessage(title);
    }

    private static void createAndOpenGui(Player player, MultiProcessRecipeReader reader, RecipeGuiContext context) {
        RecipeGui gui = new RecipeGui(player, context, reader);
        gui.render();
        gui.open(player);
    }

    //#endregion Help
}
