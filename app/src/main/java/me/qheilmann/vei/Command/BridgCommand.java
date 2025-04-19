// package me.qheilmann.vei.Command;

// import static net.minecraft.commands.Commands.argument;
// import static net.minecraft.commands.Commands.literal;

// import org.bukkit.Material;
// import org.bukkit.NamespacedKey;
// import org.bukkit.command.CommandSender;
// import org.bukkit.inventory.ItemStack;

// import com.mojang.brigadier.CommandDispatcher;
// import com.mojang.brigadier.context.CommandContext;
// import com.mojang.brigadier.tree.LiteralCommandNode;

// import dev.jorel.commandapi.Brigadier;

// public class BridgCommand {

//     static {
//         CommandDispatcher<CommandContext<CommandSender>> dispatcher =
//         Brigadier.getCommandDispatcher();  // via CommandAPI’s bridge :contentReference[oaicite:2]{index=2}

//         // 2) Build your "craft" command node:
//         LiteralCommandNode<CommandContext<CommandSender>> craftNode = 
//             literal("craft")
//             .then(
//                 argument("item", new ValidItemKeyType(dummyItemsList))
//                     .executes(ctx -> {
//                     String key = ctx.getArgument("item", String.class);
//                     ItemStack stack = customItemRegistry.getOrDefault(
//                         NamespacedKey.fromString(key), 
//                         new ItemStack(Material.STONE)
//                     );
//                     ctx.getSource().getBukkitSender().sendMessage("Giving you: " + key);
//                     ctx.getSource().getBukkitSender().getPlayer().getInventory().addItem(stack);
//                     return 1;
//                     })
//             )
//             .build();

//         // 3) Register it:
//         dispatcher.register(craftNode);
//     }
// }
