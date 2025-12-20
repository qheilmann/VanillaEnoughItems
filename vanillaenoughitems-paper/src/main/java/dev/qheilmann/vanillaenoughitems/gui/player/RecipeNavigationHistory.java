package dev.qheilmann.vanillaenoughitems.gui.player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.helper.RecipeHelper;
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

    public RecipeNavigationHistory(UUID playerUuid) {
        this.playerUuid = playerUuid;
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
        if (isSameReaderView(currentReader, targetReader)) {
            return;
        }
        
        // Don't push if this is the same as the top of the stack
        if (isSameReaderView(backwardStack.peek(), currentReader)) {
            // Prevents pushing the same view multiple times
            return;
        }

        backwardStack.push(currentReader);
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

    private boolean isSameReaderView(@Nullable MultiProcessRecipeReader r1, @Nullable MultiProcessRecipeReader r2) {
        if (r1 == r2) {
            return true;
        }

        if (r1 == null || r2 == null) {
            return false;
        }

        if(!r1.getCurrentProcess().equals(r2.getCurrentProcess())) {
            return false;
        }

        if(RecipeHelper.RECIPE_COMPARATOR.compare(
            r1.getCurrentProcessRecipeReader().getCurrent(),
            r2.getCurrentProcessRecipeReader().getCurrent()) != 0) {
            return false;
        }

        // Here we can have different readers showing the same recipe but with different recipe collection (like reader by ingredient/result showing same recipe)
        // but we can't really detect that, so we consider them the same view

        return true;
    }
}
