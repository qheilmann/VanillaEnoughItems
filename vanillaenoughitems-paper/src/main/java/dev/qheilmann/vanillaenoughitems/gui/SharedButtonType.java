package dev.qheilmann.vanillaenoughitems.gui;

import org.jspecify.annotations.NullMarked;

/**
 * Types of shared buttons that RecipeGui provides to ProcessPanel implementations.
 * These buttons are pre-built by RecipeGui and must be returned unmodified by panels.
 */
@NullMarked
public enum SharedButtonType {
    /** Navigate to the next recipe in the current process */
    NEXT_RECIPE,
    
    /** Navigate to the previous recipe in the current process */
    PREVIOUS_RECIPE,
    
    /** Navigate backward in recipe history */
    HISTORY_BACKWARD,
    
    /** Navigate forward in recipe history */
    HISTORY_FORWARD,
}
