package dev.qheilmann.vanillaenoughitems.recipe.process;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.utils.VeiKey;
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
     * Key for the crafting process, used in comparator ordering.
     */
    Key CRAFTING_PROCESS_KEY = Key.key(Key.MINECRAFT_NAMESPACE, "crafting");

    /**
     * Comparator to order processes.
     * The CraftingProcess is prioritized first, followed by all other vanilla processes in lexicographical order,
     * then all other non vanilla processes in lexicographical order and finally the UndefinedProcess.<br>
     * Note: this is useful for ordered collections like NavigableSet.
     * 
     * @return a comparator for ordering processes
     */
    public static final Comparator<Process> COMPARATOR = comparator();

    /**
     * The undefined process instance
     */
    public static final UndefinedProcess UNDEFINED_PROCESS = UndefinedProcess.INSTANCE;

    /**
     * Check if the process can handle a recipe
     * @param recipe the recipe to check
     * @return true if the process can handle the recipe, false otherwise
     */
    boolean canHandleRecipe(Recipe recipe);

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

        Predicate<Process> isCraftingProcess = p -> p.key().equals(CRAFTING_PROCESS_KEY);
        Predicate<Process> isUndefinedProcess = p -> p.key().equals(UndefinedProcess.KEY);
        Predicate<Process> isVanillaProcess = p -> p.key().namespace().equals(Key.MINECRAFT_NAMESPACE);

        return Comparator.comparing((Process p) -> !isCraftingProcess.test(p))
                    .thenComparing((Process p) -> !isVanillaProcess.test(p))
                    .thenComparing(Comparator.comparing((Process p) -> !isUndefinedProcess.test(p)).reversed()) // place undefined last
                    .thenComparing((Process p) -> p.key());

        // Note: negate with '!', is because the boolean comparator place false first and true last, so here we place it first if the predicate is true.
    }

    public static class UndefinedProcess implements Process {

        public static final Key KEY = VeiKey.key("undefined");
        public static final UndefinedProcess INSTANCE = new UndefinedProcess();

        private UndefinedProcess() {} // singleton

        @Override
        public Key key() {
            return KEY;
        }

        @Override
        public boolean canHandleRecipe(Recipe recipe) {
            return true;
        }

        @Override
        public ItemStack symbol() {
            ItemStack undefinedSymbol = new ItemStack(Material.BARRIER);
            undefinedSymbol.editMeta(meta -> {
                meta.displayName(Component.text("Undefined Process")); // [Translation]
            });

            return undefinedSymbol;
        }

        @Override
        public Set<Workbench> workbenches() {
            ItemStack undefiedWorkbench = new ItemStack(Material.BARRIER);
            undefiedWorkbench.editMeta(meta -> {
                meta.displayName(Component.text("Undefiened Workbench")); // [Translation]
            });
            
            Workbench undefined = new Workbench(undefiedWorkbench);

            return Set.of(undefined);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Process other)) return false;
            return this.key().equals(other.key());
        }

        @Override
        public int hashCode() {
            return key().hashCode();
        }
    }
}
