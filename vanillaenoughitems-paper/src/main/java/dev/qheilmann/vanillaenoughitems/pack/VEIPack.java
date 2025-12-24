package dev.qheilmann.vanillaenoughitems.pack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VEIPack {
    public static class Character {
        public static class Gui {
            public static final GuiIcon BLANK_54 = new GuiIcon("\uF100", -8, 176);

            public record GuiIcon (String codepoint, int offset, int width) {
                
                /**
                 * Returns the codepoint string of the icon with the offset spaces applied before it.
                 *
                 * @return The offset string plus the codepoint.
                 */
                public Component iconComponent() {
                    return Component.text(SpaceFont.space(offset) + codepoint, NamedTextColor.WHITE);
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
                public String resetString() {
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
        }
    }
}
