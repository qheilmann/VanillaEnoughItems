package dev.qheilmann.vanillaenoughitems.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.bookmark.ServerBookmarkRegistry;
import dev.qheilmann.vanillaenoughitems.config.VanillaEnoughItemsConfig;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractorRegistry;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.RecipeIndexView;
import dev.qheilmann.vanillaenoughitems.recipe.process.ProcessRegistry;

import net.kyori.adventure.key.Key;

/**
 * Main entry point for the VanillaEnoughItems API.
 * <p>
 * Use {@link #get()} to access the API instance after VEI has been enabled.
 * Two lifecycle events are available:
 * </p>
 * <ul>
 *   <li>{@link dev.qheilmann.vanillaenoughitems.api.event.VeiRegistrationEvent} — 
 *       fired <b>before</b> indexation. Register custom processes, extractors, and panels here.</li>
 *   <li>{@link dev.qheilmann.vanillaenoughitems.api.event.VeiReadyEvent} — 
 *       fired <b>after</b> indexation. {@link #recipeIndex()} is now available.
 *       Use this to create bookmarks, query recipes, or open GUIs.</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>{@code
 * VanillaEnoughItemsAPI api = VanillaEnoughItemsAPI.get();
 * RecipeIndexView index = api.recipeIndex(); // only after VeiReadyEvent
 * }</pre>
 */
@NullMarked
public interface VanillaEnoughItemsAPI {

    /**
     * Holds the singleton API instance.
     * Interfaces cannot have mutable static fields, so we use a holder class.
     */
    final class Holder {
        private static @Nullable VanillaEnoughItemsAPI instance;
        private Holder() {}
    }

    /**
     * Get the VanillaEnoughItems API instance.
     * @return the API instance
     * @throws IllegalStateException if VEI has not been enabled yet
     */
    static VanillaEnoughItemsAPI get() {
        VanillaEnoughItemsAPI api = Holder.instance;
        if (api == null) {
            throw new IllegalStateException(
                "VanillaEnoughItems API is not available yet. " +
                "Ensure VEI is listed as a dependency in your plugin.yml and access the API after onEnable()."
            );
        }
        return api;
    }

    /**
     * Register the API implementation.
     * <p><b>Internal use only.</b> Called by the VEI plugin during startup.</p>
     * @param api the API implementation to register
     * @throws IllegalStateException if an API instance is already registered
     */
    static void register(VanillaEnoughItemsAPI api) {
        if (Holder.instance != null) {
            throw new IllegalStateException("VanillaEnoughItems API is already registered");
        }
        Holder.instance = api;
    }

    /**
     * Unregister the API implementation.
     * <p><b>Internal use only.</b> Called by the VEI plugin during shutdown.</p>
     */
    static void unregister() {
        Holder.instance = null;
    }

    /**
     * Get the configuration.
     * @return the VEI configuration
     */
    VanillaEnoughItemsConfig config();

    /**
     * Get the recipe index view (read-only access to indexed recipes).
     * <p>Only available after {@link dev.qheilmann.vanillaenoughitems.api.event.VeiReadyEvent} has fired.</p>
     * @return the recipe index view
     * @throws IllegalStateException if indexation has not completed yet
     */
    RecipeIndexView recipeIndex();

    /**
     * Get the process registry.
     * Register custom processes here before indexation.
     * @return the process registry
     */
    ProcessRegistry processRegistry();

    /**
     * Get the recipe extractor registry.
     * Register custom extractors here before indexation.
     * @return the recipe extractor registry
     */
    RecipeExtractorRegistry recipeExtractorRegistry();

    /**
     * Get the process panel registry.
     * Register custom panel factories here before indexation.
     * @return the process panel registry
     */
    ProcessPanelRegistry processPanelRegistry();

    /**
     * Get the server-wide bookmark registry.
     * @return the server bookmark registry
     */
    ServerBookmarkRegistry serverBookmarkRegistry();

    /**
     * Trigger a reload of the recipe indexation.
     * This will re-index all recipes from the server.
     */
    void reloadIndexation();

    // ---- GUI ----

    /**
     * Open a recipe GUI showing recipes that produce the given item as a result.
     * <p>Only available after {@link dev.qheilmann.vanillaenoughitems.api.event.VeiReadyEvent} has fired.</p>
     *
     * @param player the player to open the GUI for
     * @param result the result item to look up recipes for
     * @return {@code true} if recipes were found and the GUI was opened, {@code false} otherwise
     */
    boolean openRecipeGui(Player player, ItemStack result);

    /**
     * Open a recipe GUI showing the recipe identified by the given key.
     * <p>Only available after {@link dev.qheilmann.vanillaenoughitems.api.event.VeiReadyEvent} has fired.</p>
     *
     * @param player the player to open the GUI for
     * @param recipeKey the recipe key (e.g. {@code Key.key("minecraft:diamond")})
     * @return {@code true} if the recipe was found and the GUI was opened, {@code false} otherwise
     */
    boolean openRecipeGui(Player player, Key recipeKey);

    /**
     * Open a usage GUI showing recipes that use the given item as an ingredient.
     * <p>Only available after {@link dev.qheilmann.vanillaenoughitems.api.event.VeiReadyEvent} has fired.</p>
     *
     * @param player the player to open the GUI for
     * @param ingredient the ingredient item to look up usages for
     * @return {@code true} if recipes were found and the GUI was opened, {@code false} otherwise
     */
    boolean openUsageGui(Player player, ItemStack ingredient);

    /**
     * Open a recipe GUI from an existing {@link MultiProcessRecipeReader}.
     * <p>This is the most flexible method — the caller controls which recipes and
     * starting position the reader points to.</p>
     *
     * @param player the player to open the GUI for
     * @param reader the reader containing recipe data to display
     */
    void openReaderGui(Player player, MultiProcessRecipeReader reader);

    /**
     * Open the bookmark GUI for a player, showing both their personal
     * and server-wide bookmarks.
     *
     * @param player the player to open the bookmark GUI for
     */
    void openPlayerBookmarkGui(Player player);

    /**
     * Open the server bookmark GUI for a player, showing only server-wide bookmarks.
     *
     * @param player the player to open the server bookmark GUI for
     */
    void openServerBookmarkGui(Player player);
}
