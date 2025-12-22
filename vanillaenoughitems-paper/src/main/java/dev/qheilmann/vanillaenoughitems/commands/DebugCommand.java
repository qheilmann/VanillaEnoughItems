package dev.qheilmann.vanillaenoughitems.commands;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGui;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import net.kyori.adventure.text.Component;

@NullMarked
public class DebugCommand {
    public static final String NAME = "debugvei";
    public static final String[] ALIASES = {"dc"};
    public static final CommandPermission PERMISSION = CommandPermission.OP;
    public static final String SHORT_HELP = "Debug VEI";
    public static final String LONG_HELP = SHORT_HELP;
    public static final String USAGE = """

                                    /debugvei
                                    """;

    private DebugCommand() {}; // Prevent instantiation

    public static void register(RecipeGuiContext context) {

        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_HELP, LONG_HELP)
            .withUsage(USAGE)

            // debugvei
            .withArguments(new ItemStackArgument("item"))
            .executesPlayer((player, args) -> {
                ItemStack itemStack = args.getUnchecked("item");
                itemStack = itemStack.asOne(); // only one item to lookup recipes
                MultiProcessRecipeReader reader = context.getRecipeIndexReader().readerByResult(itemStack);

                if (reader == null) {
                    Component text = Component.text()
                        .append(Component.text("No recipes found for "))
                        .append(itemStack.displayName())
                        .build();
                    player.sendMessage(text);
                    return;
                }

                RecipeGui gui = new RecipeGui(player, context, reader);
                gui.render();
                gui.open(player);
            })

            .register();
    }
}
