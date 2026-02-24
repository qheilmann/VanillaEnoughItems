package dev.qheilmann.vanillaenoughitems.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;

/**
 * Event fired when VanillaEnoughItems has completed recipe indexation and is fully ready.
 * <p>
 * At this point, {@link VanillaEnoughItemsAPI#recipeIndex()} is available and all
 * recipe data can be queried. Use this event to create bookmarks, open recipe GUIs,
 * or perform any logic that depends on indexed recipes.
 * </p>
 * <p>
 * For registering custom processes, extractors, and panels, use
 * {@link VeiRegistrationEvent} instead (it fires before indexation).
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>{@code
 * @EventHandler
 * public void onVeiReady(VeiReadyEvent event) {
 *     VanillaEnoughItemsAPI api = event.getApi();
 *     RecipeIndexView index = api.recipeIndex(); // safe to call now
 * }
 * }</pre>
 */
@NullMarked
public class VeiReadyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final VanillaEnoughItemsAPI api;

    /**
     * Construct the VEI ready event.
     * @param api the fully initialized VanillaEnoughItems API instance
     */
    public VeiReadyEvent(VanillaEnoughItemsAPI api) {
        this.api = api;
    }

    /**
     * Get the fully initialized VanillaEnoughItems API instance.
     * @return the API with recipe index available
     */
    public VanillaEnoughItemsAPI getApi() {
        return api;
    }

    /**
     * Get the handler list for this event.
     * @return the handler list for this event
     */
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * Get the handler list for this event.
     * @return the handler list for this event
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
