package me.qheilmann.vei.Core.GUI;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.InteractionModifier;
import net.kyori.adventure.text.Component;

public class Gui extends BaseGui<Gui> {
    /**
     * Main constructor for the GUI
     *
     * @param rows                 The amount of rows the GUI should have
     * @param title                The GUI's title
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     */
    public Gui(final int rows, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }

    /**
     * Alternative constructor that takes both a {@link GuiType} and a set of {@link InteractionModifier}
     *
     * @param guiType              The {@link GuiType} to be used
     * @param title                The GUI's title
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     */
    public Gui(@NotNull final GuiType guiType, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(guiType, title, interactionModifiers);
    }
}
