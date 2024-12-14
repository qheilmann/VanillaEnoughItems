package me.qheilmann.vei.Command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.RecipeArgument;
import me.qheilmann.vei.API.RecipeInterface;

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

    private JavaPlugin plugin;

    public CraftCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        new CommandAPICommand(NAME)
            .withAliases(ALIASES)
            .withPermission(PERMISSION)
            .withHelp(SHORT_DESCRIPTION, LONG_DESCRIPTION)
            .withArguments(new RecipeArgument("recipe"))
            .executesPlayer((player, args) -> {
                Recipe recipe = fixComplexRecipe((Recipe) args.get("recipe"));
                openRecipeAction(player, recipe);
            })
            .register();
    }

    // Command action
    // Must:
    // - Be suffixed with "Action"
    // - Perform the command backend API
    // Can:
    // - Verify and convert command arguments
    // - Send feedback messages
    // - Call other cosmetic methods (particles, sounds, etc.)

    private void openRecipeAction(Player player, Recipe recipe) {
        if(!(recipe instanceof ShapedRecipe shapedRecipe)) {
            player.sendMessage("Recipe other than ShapedRecipe are not supported yet (" + recipe.getClass().getName() + ")");
            return;
        }
        
        new RecipeInterface(plugin).openInterface(player, shapedRecipe);
    }

    // Utils

    /**
     * Convert a complex recipe to a possible simple recipe
     * If the original recipe is a simple recipe, it will return the same object
     * This can be use with the RecipeArgument, this class always return a ComplexRecipe even if the recipe is an other implementation of Recipe
     * @param recipe
     * @return Recipe implementation
     */
    Recipe fixComplexRecipe(Recipe recipe) {
        if(!(recipe instanceof ComplexRecipe complexRecipe)) {
            return recipe;
        }

        return plugin.getServer().getRecipe(complexRecipe.getKey());
    }
}
