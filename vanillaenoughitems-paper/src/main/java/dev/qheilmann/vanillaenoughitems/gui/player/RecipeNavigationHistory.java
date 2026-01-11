package dev.qheilmann.vanillaenoughitems.gui.player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;

/**
 * Manages recipe navigation history for a single player.
 * Maintains separate backward and forward stacks for browsing history.
 */
@NullMarked
public class RecipeNavigationHistory {
    private static final int MAX_HISTORY_SIZE = 100;
    
    @SuppressWarnings("unused")
    private final UUID playerUuid;
    private final Deque<MultiProcessRecipeReader> backwardStack = new ArrayDeque<>();
    private final Deque<MultiProcessRecipeReader> forwardStack = new ArrayDeque<>();
    /** The last reader that was being viewed (saved when on closes) */
    private @Nullable MultiProcessRecipeReader lastViewedReader = null;

    public RecipeNavigationHistory(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    /**
     * Called when starting to view a new reader.
     * Pushes the last viewed reader (if any) to history.
     * The new reader will be saved by stopViewing() when viewing ends.
     * 
     * @param newReader the reader now being viewed
     */
    public void startViewing(MultiProcessRecipeReader newReader) {
        // If there was a previously viewed reader, push it to history
        if (lastViewedReader != null) {
            pushToHistory(lastViewedReader);
        }
    }
    
    /**
     * Called when stopping viewing a reader.
     * Updates the last viewed reader to the current one being viewed.
     * 
     * @param currentReader the reader that was being viewed
     */
    public void stopViewing(MultiProcessRecipeReader currentReader) {
        lastViewedReader = currentReader;
    }

    /**
     * Push the current reader to backward history when navigating to a new recipe.
     * Clears forward history since we're starting a new branch.
     * 
     * @param currentReader the reader to save before navigating
     * @param targetReader the reader we're navigating to
     */
    public void pushForNavigation(MultiProcessRecipeReader currentReader, MultiProcessRecipeReader targetReader) {
        // Don't push if we're staying on the same recipe view
        if (currentReader.equals(targetReader)) {
            return;
        }

        pushToHistory(currentReader);
    }

    /**
     * Internal method to push a reader to the backward stack and clear forward history.
     * Prevents pushing duplicates - if the reader is already on top of the stack, does nothing.
     * Ensures forward stack is cleared when starting a new navigation branch.
     * 
     * @param reader the reader to push to history
     */
    private void pushToHistory(MultiProcessRecipeReader reader) {
        // Don't push if this is the same as the top of the stack (prevents duplicates)
        if (reader.equals(backwardStack.peek())) {
            return;
        }
        
        backwardStack.push(reader);
        forwardStack.clear(); // New navigation clears forward history
        
        // Limit history size
        while (backwardStack.size() > MAX_HISTORY_SIZE) {
            backwardStack.removeLast();
        }
    }

    /**
     * Go back to the previous reader in history
     * @param currentReader the current reader to push to forward stack
     * @return the previous reader, or null if no history
     */
    @Nullable
    public MultiProcessRecipeReader goBackward(MultiProcessRecipeReader currentReader) {
        if (backwardStack.isEmpty()) {
            return null;
        }
        
        forwardStack.push(currentReader);
        return backwardStack.pop();
    }

    /**
     * Go forward in history after going backward
     * @param currentReader the current reader to push to backward stack
     * @return the next reader, or null if no forward history
     */
    @Nullable
    public MultiProcessRecipeReader goForward(MultiProcessRecipeReader currentReader) {
        if (forwardStack.isEmpty()) {
            return null;
        }
        
        backwardStack.push(currentReader);
        return forwardStack.pop();
    }

    /**
     * Check if backward navigation is available
     * @return true if can go back
     */
    public boolean canGoBackward() {
        return !backwardStack.isEmpty();
    }

    /**
     * Check if forward navigation is available
     * @return true if can go forward
     */
    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    /**
     * Clear all navigation history
     */
    public void clear() {
        backwardStack.clear();
        forwardStack.clear();
        lastViewedReader = null;
    }

    /**
     * Get the size of backward history
     * @return number of entries in backward stack
     */
    public int getBackwardHistorySize() {
        return backwardStack.size();
    }

    /**
     * Get the size of forward history
     * @return number of entries in forward stack
     */
    public int getForwardHistorySize() {
        return forwardStack.size();
    }
}
