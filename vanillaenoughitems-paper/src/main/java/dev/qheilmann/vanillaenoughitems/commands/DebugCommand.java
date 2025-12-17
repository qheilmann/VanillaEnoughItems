package dev.qheilmann.vanillaenoughitems.commands;

import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGui;
import dev.qheilmann.vanillaenoughitems.gui.RecipeGuiContext;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.RecipeIndexReader;

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
            .executesPlayer((player, args) -> {

                RecipeIndexReader recipeIndex = new RecipeIndexReader(context.getRecipeIndex());
                MultiProcessRecipeReader reader = recipeIndex.readerByResult(ItemType.IRON_INGOT.createItemStack());

                @SuppressWarnings("null")
                RecipeGui gui = new RecipeGui(player, context, reader);
                gui.render();
                gui.open(player);
            })

            .register();
    }
}
