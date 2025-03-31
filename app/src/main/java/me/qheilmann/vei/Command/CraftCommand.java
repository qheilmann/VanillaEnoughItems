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
import me.qheilmann.vei.Menu.MenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CraftCommand implements ICommand{
    public static final String NAME = "craft";
    public static final String[] ALIASES = {"c", "vei"};
    public static final String SHORT_DESCRIPTION = "Show item recipe";
    public static final String LONG_DESCRIPTION = "Open a GUI showing the recipe of an item, it can be crafting, smelting, brewing, etc.";
    public static final CommandPermission PERMISSION = CommandPermission.NONE;
    public static final String USAGE = """

                                        Note: Command sender must be a player
                                        /craft recipeId
                                        
                                        WIP:
                                        (/craft <item> [<process>] [<variant>])
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

                openRecipeAction(player, recipeId);
            })
            .register();
    }

    // Action

    private void openRecipeAction(@NotNull Player player, @NotNull NamespacedKey recipeId) throws WrapperCommandSyntaxException {
        Recipe recipe = recipeIndex.getById(recipeId);
        if (recipe == null) {
            throw CommandAPIBukkit.failWithAdventureComponent(Component.text("Recipe ID not found: " + recipeId.toString(), NamedTextColor.RED));
        }

        ItemStack result = recipe.getResult();

        // For the moment simply take the result instead of right result variant
        MixedProcessRecipeReader recipeReader = recipeIndex.getByResult(result);

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

    // Utils

    private void openRecipe(@NotNull Player player, @NotNull ItemStack item, @Nullable Process<?> process, int variant) {
        menuManager.openRecipeMenu(player, item, process, variant);
        // TODO add a global try catch here
    }
}
