package me.qheilmann.vei.Command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ItemStackArgument;
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
            .withArguments(new ItemStackArgument("item"))
            .executesPlayer((player, args) -> {
                ItemStack itemStack = (ItemStack) args.get("item");
                openRecipe(player, itemStack);
            })
            .register();
    }

    // Utils

    private void openRecipe(Player player, ItemStack item) {
        menuManager.openRecipeMenu(player, item);
    }
}
