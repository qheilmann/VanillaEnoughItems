package me.qheilmann.vei.foundation.gui;

import java.util.List;

import net.kyori.adventure.text.Component;

public enum ActionType {
        WORKBENCH_TYPE_SCROLL_LEFT(Component.text("Scroll left"), List.of(Component.text("See previous workbench type")), "WORKBENCH_TYPE_SCROLL_LEFT"),
        WORKBENCH_TYPE_SCROLL_RIGHT(Component.text("Scroll right"), List.of(Component.text("See next workbench type")), "WORKBENCH_TYPE_SCROLL_RIGHT"),
        WORKBENCH_VARIANT_SCROLL_UP(Component.text("Scroll up"), List.of(Component.text("See previous workbench variant")), "WORKBENCH_VARIANT_SCROLL_UP"),
        WORKBENCH_VARIANT_SCROLL_DOWN(Component.text("Scroll down"), List.of(Component.text("See next workbench variant")), "WORKBENCH_VARIANT_SCROLL_DOWN"),
        NEXT_RECIPE(Component.text("Next recipe"), List.of(Component.text("Go to the next variation of the same recipe")), "NEXT_RECIPE"),
        PREVIOUS_RECIPE(Component.text("Previous recipe"), List.of(Component.text("Go to the previous variation of the same recipe")), "PREVIOUS_RECIPE"),
        BACK_RECIPE(Component.text("Navigate Back"), List.of(Component.text("Go back to the preceding recipe in the history")), "BACK_RECIPE"),
        FORWARD_RECIPE(Component.text("Navigate Forward"), List.of(Component.text("Return to following recipe in history")), "FORWARD_RECIPE"),
        MOVE_INGREDIENTS(Component.text("Move ingredients"), List.of(Component.text("Automatically move all the ingredients inside the workbench"), Component.text("This work only if a empty acessible workbench is arround you")), "MOVE_INGREDIENTS"),
        QUICK_LINK(Component.text("Quick link"), List.of(Component.text("click to get the command equivalent for go to this recipe"), Component.text("/recipe <myRecipe> <category>")), "QUICK_LINK"),
        INFO(Component.text("Info"), List.of(Component.text("See VEI info")), "INFO"),
        BOOKMARK_THIS_RECIPE(Component.text("Bookmark this recipe"), List.of(Component.text("Add this recipe to your bookmark")), "BOOKMARK_THIS_RECIPE"),
        BOOKMARK_LIST(Component.text("Bookmark list"), List.of(Component.text("See your bookmarked recipes")), "BOOKMARK_LIST"),
        BOOKMARK_SERVER_LIST(Component.text("Bookmark server list"), List.of(Component.text("See the server bookmarked recipes")), "BOOKMARK_SERVER_LIST"),
        EXIT(Component.text("Exit"), List.of(Component.text("Exit the recipe menu")), "EXIT");

        private final Component displayName;
        private final List<? extends Component> lores;
        private final String reference;

        ActionType(Component displayName, List<? extends Component> lores, String reference) {
            this.displayName = displayName;
            this.lores = lores;
            this.reference = reference;
        }

        public Component getDisplayName() {
            return displayName;
        }

        public List<? extends Component> getLores() {
            return lores;
        }

        public String getReference() {
            return reference;
        }
}
