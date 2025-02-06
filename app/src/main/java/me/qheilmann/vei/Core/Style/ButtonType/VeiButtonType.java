package me.qheilmann.vei.Core.Style.ButtonType;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

public class VeiButtonType implements ButtonType {
    public static class RecipeMenu {
        public static final ButtonType BACKWARD_RECIPE = new VeiButtonType("backward_recipe");
        public static final ButtonType BOOKMARK_LIST = new VeiButtonType("bookmark_list");
        public static final ButtonType BOOKMARK_SERVER_LIST = new VeiButtonType("bookmark_server_list");
        public static final ButtonType BOOKMARK_THIS_RECIPE = new VeiButtonType("bookmark_this_recipe");
        public static final ButtonType FORWARD_RECIPE = new VeiButtonType("forward_recipe");
        public static final ButtonType MOVE_INGREDIENTS = new VeiButtonType("move_ingredients");
        public static final ButtonType NEXT_RECIPE = new VeiButtonType("next_recipe");
        public static final ButtonType PREVIOUS_RECIPE = new VeiButtonType("previous_recipe");
        public static final ButtonType QUICK_LINK = new VeiButtonType("quick_link");
        public static final ButtonType UNBOOKMARK_THIS_RECIPE = new VeiButtonType("unbookmark_this_recipe");
        public static final ButtonType WORKBENCH_TYPE_SCROLL_LEFT = new VeiButtonType("workbench_type_scroll_left");
        public static final ButtonType WORKBENCH_TYPE_SCROLL_RIGHT = new VeiButtonType("workbench_type_scroll_right");
        public static final ButtonType WORKBENCH_VARIANT_SCROLL_DOWN = new VeiButtonType("workbench_variant_scroll_down");
        public static final ButtonType WORKBENCH_VARIANT_SCROLL_UP = new VeiButtonType("workbench_variant_scroll_up");
    }

    public static class Generic {
        public static final ButtonType EXIT = new VeiButtonType("exit");
        public static final ButtonType INFO = new VeiButtonType("info");
    }

    @NotNull
    private final String id;

    private VeiButtonType(@NotNull String id) {
        Preconditions.checkNotNull(id, "id cannot be null");
        this.id = id;
    }

    @Override
    @NotNull
    public String getId() {
        return id;
    }
}