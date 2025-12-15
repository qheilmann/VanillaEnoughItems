package dev.qheilmann.vanillaenoughitems.index.process;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;
import dev.qheilmann.vanillaenoughitems.index.process.impl.CraftingProcess;
import dev.qheilmann.vanillaenoughitems.index.process.Process;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;

/** 
 * A process is a transformation of items in a certaine way using a recipe, (e.g. Crafting, Smelting, Smithing, etc). 
 * Each process can be done using one or more workbenche (e.g., Crafting table / Crafter for Crafting).
 * Each process is immutable
 * @apiNote Each process implementation must be immutable
*/
@NullMarked
public interface Process extends Keyed{

    /**
     * Comparator to order processes.
     * The CraftingProcess is prioritized first, followed by all other vanilla processes in lexicographical order, then all other non vanilla processes in lexicographical order and finally the UndefinedProcess.
     * 
     * @return a comparator for ordering processes
     */
    public static final Comparator<Process> COMPARATOR = comparator();

    /**
     * Check if the process can handle a recipe
     * @param recipe the recipe to check
     * @return true if the process can handle the recipe, false otherwise
     */
    boolean canHandleRecipe(Recipe recipe);

    /**
     * Get the display name of the process
     * @return the display name
     */
    Component displayName();

    /**
     * Get the symbol item of the process
     * @return the symbol item
     */
    ItemStack symbol();

    /**
     * Get all workbenches associated with this process
     * @return the workbenches
     */
    Set<Workbench> workbenches();


    private static Comparator<Process> comparator() {

        Predicate<Process> isCraftingProcess = (Process p) -> p.key().equals(CraftingProcess.KEY);
        Predicate<Process> isUndefinedProcess = (Process p) -> p.key().equals(UndefinedProcess.KEY);
        Predicate<Process> isVanillaProcess = (Process p) -> p.key().namespace().equals(Key.MINECRAFT_NAMESPACE);

        return Comparator.comparing((Process p) -> !isCraftingProcess.test(p))
                    .thenComparing((Process p) -> !isVanillaProcess.test(p))
                    .thenComparing(Comparator.comparing((Process p) -> !isUndefinedProcess.test(p)).reversed())
                    .thenComparing((Process p) -> p.key());

        // Note: negate with '!', is because the boolean comparator place false first and true last, so here we place it first if the predicate is true.
    }

    public static class UndefinedProcess implements Process {

        public static final Key KEY = Key.key(VanillaEnoughItems.NAMESPACE,"undefined");

        @Override
        public Key key() {
            return KEY;
        }

        @Override
        public boolean canHandleRecipe(Recipe recipe) {
            return true;
        }

        @Override
        public Component displayName() {
            return Component.text("Undefined Process");
        }

        @Override
        public ItemStack symbol() {
            return new ItemStack(Material.BARRIER);
        }

        @Override
        public Set<Workbench> workbenches() {
            ItemStack undefiedItem = new ItemStack(Material.BARRIER);
            undefiedItem.editMeta(meta -> {
                meta.displayName(Component.text("Undefiened Workbench")); // [Translation]
            });
            
            Workbench undefined = new Workbench(undefiedItem);

            return Set.of(undefined);
        }
    }
}