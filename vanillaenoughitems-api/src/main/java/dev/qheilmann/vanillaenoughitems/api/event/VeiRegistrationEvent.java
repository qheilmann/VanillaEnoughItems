package dev.qheilmann.vanillaenoughitems.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.api.VanillaEnoughItemsAPI;

/**
 * Event fired when VanillaEnoughItems is ready for external registrations.
 * <p>
 * Listen for this event to register custom processes, recipe extractors, 
 * and process panels <b>before</b> recipe indexation occurs.
 * </p>
 * 
 * <h2>Example:</h2>
 * <pre>{@code
 * @EventHandler
 * public void onVeiRegistration(VeiRegistrationEvent event) {
 *     VanillaEnoughItemsAPI api = event.getApi();
 *     api.processRegistry().registerProcess(myCustomProcess);
 *     api.recipeExtractorRegistry().registerExtractor(myExtractor);
 *     api.processPanelRegistry().registerProvider(myProcess, myPanelFactory);
 * }
 * }</pre>
 */
@NullMarked
public class VeiRegistrationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final VanillaEnoughItemsAPI api;

    /**
     * Construct the VEI registration event.
     * @param api the VanillaEnoughItems API instance for registrations
     */
    public VeiRegistrationEvent(VanillaEnoughItemsAPI api) {
        this.api = api;
    }

    /**
     * Get the VanillaEnoughItems API instance.
     * <p>Use it to access registries and register custom processes, extractors, and panels.</p>
     * @return the API (recipe index is NOT yet available at this point)
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
     * Get the handler list for this event type.
     * @return the handler list for this event
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
