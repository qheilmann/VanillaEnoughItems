package me.qheilmann.vei.Command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import me.qheilmann.vei.VanillaEnoughItems;
import me.qheilmann.vei.Menu.MenuManager;

public class CraftCommand implements ICommand{
    public static final String NAME = "craft";
    public static final String[] ALIASES = {"c", "vei"};
    public static final String SHORT_DESCRIPTION = "Show item recipe";
    public static final String LONG_DESCRIPTION = "Open a GUI showing the recipe of an item, it can be crafting, smelting, brewing, etc.";
    public static final CommandPermission PERMISSION = CommandPermission.NONE;
    public static final String USAGE = """

                                        Note: Command sender must be a player
                                        /craft <recipe>
                                        """;

    private MenuManager menuManager;

    public CraftCommand(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public void register() {
        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_DESCRIPTION, LONG_DESCRIPTION)
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
            .withArguments(new ItemStackArgument("item").replaceSafeSuggestions(
                SafeSuggestions.suggest(info -> {
                    return VanillaEnoughItems.allRecipesMap.getItems().stream().filter(item ->
                        // This filter isn't really efficient: it compares the beginning of the argument with all possible elements
                        // BUT at the same time checks with the default prefix "minecraft:", so only minecraft:minecart works for a the 5 first characters.
                        // {
                        //     VanillaEnoughItems.LOGGER.info("item: " + item.getType().name().toLowerCase() + " input: " + info.currentArg().toLowerCase() + " match: " + item.getType().name().toLowerCase().startsWith(info.currentArg().toLowerCase()));
                        //     return item.getType().name().toLowerCase().startsWith(info.currentArg().toLowerCase());
                        // }
                        {
                            return true;
                        }
                    ).toArray(ItemStack[]::new);
                })
            ))
            .executesPlayer((player, args) -> {
                ItemStack itemStack = (ItemStack) args.get("item");
                openRecipeAction(player, itemStack);
            })
            .register();
    }

    // Utils

    private void openRecipeAction(Player player, ItemStack item) {
        menuManager.openRecipeMenu(player, item);
    }
}
