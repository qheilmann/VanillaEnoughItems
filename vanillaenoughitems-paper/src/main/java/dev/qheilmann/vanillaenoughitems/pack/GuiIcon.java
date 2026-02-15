package dev.qheilmann.vanillaenoughitems.pack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Represents a character used in the GUI title as a background icon, defined by a codepoint, offset, and width.
 * The offset is typically negative to shift the icon left, allowing it to overlay properly with the GUI background.
 * 
 * @param codepoint The Unicode character representing this icon
 * @param offset The horizontal offset in pixels (typically negative for left shift)
 * @param width The width of the icon in pixels
 */
public record GuiIcon (String codepoint, /* typically negative */ int offset, int width) {
                
    /**
     * Returns a complete Component with the icon properly positioned.
     * This includes: spacing before (for offset), the icon itself (in white), and spacing after (to reset position).
     * This is the recommended way to use GuiIcon for displaying icons in titles.
     * 
     * @see #iconComponent(boolean)
     * @return A complete Component with spaceBefore + icon + spaceAfter
     */
    public Component iconComponent() {
        return iconComponent(true);
    }

    /**
     * Returns a Component with the icon, with optional spacing after.
     * 
     * @param resetSpaceAfter If true, includes spaceAfter() to reset cursor position; if false, omits it
     * @return A Component with spaceBefore + icon, and optionally spaceAfter
     */
    public Component iconComponent(boolean resetSpaceAfter) {
        if (resetSpaceAfter) {
            return Component.textOfChildren(
                Component.text(spaceBefore()),
                Component.text(toString(), NamedTextColor.WHITE), // Add the white color only to the icon part
                Component.text(spaceAfter())
            );
        } else {
            return Component.textOfChildren(
                Component.text(spaceBefore()),
                Component.text(toString(), NamedTextColor.WHITE)
            );
        }
    }

    /**
     * Returns a string of spaces representing the offset for this icon.
     * This shifts the cursor position before drawing the icon.
     *
     * @return A string of space characters for the offset
     */
    public String spaceBefore() {
        return SpaceFont.space(offset);
    }

    /**
     * Returns a string of spaces that resets the cursor position after drawing the icon.
     * This cancels both the offset and the icon's width, plus adds an extra pixel gap,
     * restoring normal alignment for subsequent text.
     *
     * @return A string of spaces to reset cursor position for the next element
     */
    public String spaceAfter() {
        return SpaceFont.space(-width - offset - 1); // -1 to account for the 1px gap between the icon and the next element
    }

    /**
     * Returns just the codepoint of this icon without any spacing.
     * It's preferred to use {@link component()}, this method is mainly for implicit string conversions.
     * 
     * <p>Note: This does NOT include offset spacing. To get the icon with proper positioning,
     * use {@link #iconComponent()} for Components with spacing
     *
     * @return The raw codepoint string
     * @see #iconComponent()
     * @see #spaceBefore()
     * @see #spaceAfter()
     */
    @Override
    public String toString() {
        return codepoint;
    }
}