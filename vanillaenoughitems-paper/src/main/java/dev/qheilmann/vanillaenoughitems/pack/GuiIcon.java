package dev.qheilmann.vanillaenoughitems.pack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Represents an character used in the GUI title as a background, defined by a codepoint, offset, and width.
 */
public record GuiIcon (String codepoint, int offset, int width) {
                
    /**
     * Returns the codepoint string of the icon with the offset spaces applied before it.
     *
     * @return The offset string plus the codepoint.
     */
    public Component iconComponent() {
        return Component.textOfChildren(Component.text(toString(), NamedTextColor.WHITE)); // Add the white color only to the icon part
    }

    /**
     * Returns a string of spaces that cancels the offset applied for a
     * {@link GuiIcon} and the icon's width, restoring alignment for the next element.
     *
     * <p>Example usage after drawing an icon:
     * <pre>
     * Component.text(guiIcon.resetString() + "My Title")
     * </pre>
     *
     * @return A string of spaces to apply before the next element.
     */
    public String resetSpace() {
        return SpaceFont.space(-width - offset - 1); // -1 to account for the 1px gap between the icon and the next element
    }

    /**
     * Returns the codepoint string of the icon with the offset spaces applied before it.
     * This method is mainly here to allow implicit string conversion
     * wherever toString() is used implicitly.
     * @return The offset string plus the codepoint.
     * @see #iconComponent()
     */
    @Override
    public String toString() {
        return SpaceFont.space(offset) + codepoint;
    }
}