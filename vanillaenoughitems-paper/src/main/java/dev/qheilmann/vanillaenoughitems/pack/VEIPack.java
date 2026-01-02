package dev.qheilmann.vanillaenoughitems.pack;

import org.bukkit.NamespacedKey;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;

public class VeiPack {

    public static final String NAMESPACE = VanillaEnoughItems.NAMESPACE;

    //#region Characters

    public static class Font {
        public static class Gui {
            public static final GuiIcon BLANK_54 = new GuiIcon("\uF100", -8, 176);
        }
    }

    //#endregion Characters

    //#region Item Models

    // TODO warning this don't support multiple namespace yet 
    // (VeiPack represente the pack, maybe a sub class there ? like VeiPack.Vei.ItemModel.etc, but this is verbose but corrrect ?
    // or make only a sub class for custom namespace under VeiPack or under ItemModel like VeiPack.ItemModel.Minecraft.Common.etc
    // or just ignore the probmemme and assume we don't deal with other namespace like minecraft ? or sub project ? becasue they have their own ?)
    /**
     * Contains all item model keys used in VEI.
     * Describes the resource pack folder structure under the "pack/assets/<namespace>/items" directory.
     */
    public static class ItemModel {
        
        public static class Common {
            public static final NamespacedKey EMPTY = path(Common.class, "empty");
        }

        public static class Gui {

            public static class Background {
                public static final NamespacedKey BLASTING = path(Background.class, "blasting");
                public static final NamespacedKey CAMPFIRE = path(Background.class, "campfire");
                public static final NamespacedKey CRAFTING = path(Background.class, "crafting");
                public static final NamespacedKey SMELTING = path(Background.class, "smelting");
                public static final NamespacedKey SMOKING = path(Background.class, "smoking");
                public static final NamespacedKey STONECUTTING = path(Background.class, "stonecutting");
                public static final NamespacedKey SMITHING = path(Background.class, "smithing");

                public static class Catalyst {
                    public static final NamespacedKey _1 = path(Catalyst.class, "1");
                    public static final NamespacedKey _2 = path(Catalyst.class, "2");
                    public static final NamespacedKey _3 = path(Catalyst.class, "3");
                    public static final NamespacedKey _4 = path(Catalyst.class, "4");
                    public static final NamespacedKey _3_AND_UP = path(Catalyst.class, "3_and_up");
                }
            }

            public static class Button {
                public static final NamespacedKey ARROW_DOWN = path(Button.class, "arrow_down");
                public static final NamespacedKey ARROW_LEFT = path(Button.class, "arrow_left");
                public static final NamespacedKey ARROW_RIGHT = path(Button.class, "arrow_right");
                public static final NamespacedKey ARROW_UP = path(Button.class, "arrow_up");

                public static final NamespacedKey BACKWARD_DOUBLE = path(Button.class, "backward_double");
                public static final NamespacedKey BACKWARD = path(Button.class, "backward");

                public static final NamespacedKey BOOKMARK_LIST = path(Button.class, "bookmark_list");
                public static final NamespacedKey BOOKMARK_SERVER = path(Button.class, "bookmark_server");
                public static final NamespacedKey BOOKMARK_BOOKMARK = path(Button.class, "bookmark_bookmark");
                public static final NamespacedKey BOOKMARK_UNBOOKMARK = path(Button.class, "bookmark_unbookmark");

                public static final NamespacedKey FORWARD = path(Button.class, "forward");
                public static final NamespacedKey FORWARD_DOUBLE = path(Button.class, "forward_double");

                public static final NamespacedKey INFO = path(Button.class, "info");
                public static final NamespacedKey PLUS = path(Button.class, "plus");
                public static final NamespacedKey QUICKLINK = path(Button.class, "quicklink");
            }
            
            public static class Decoration {
                public static final NamespacedKey COOKING_FLAME = path(Decoration.class, "cooking_flame");
                public static final NamespacedKey RECIPE_PROGRESS = path(Decoration.class, "recipe_progress");
                public static final NamespacedKey RECIPE_ARROW_SMALL = path(Decoration.class, "recipe_arrow_small");
                public static final NamespacedKey RECIPE_ARROW = path(Decoration.class, "recipe_arrow");
            }
        }

        // Helpers

        /**
         * Automatically generates a resource path from the class hierarchy under ItemModel.
         * <p>
         * Example: {@code path(ItemModel.Gui.Panel.Smelting.class, "background")}
         * produces {@code "namespace:gui/panel/smelting/background"}
         * 
         * @param clazz The nested class under ItemModel (defines the folder path)
         * @param fileName The file name (without extension)
         * @return A Key with the auto-generated path
         * @throws IllegalArgumentException if clazz is not nested under ItemModel
         */
        private static NamespacedKey path(Class<?> clazz, String fileName) {
            String canonicalName = clazz.getCanonicalName();
            if (canonicalName == null) {
                throw new IllegalArgumentException("Class must have a canonical name: " + clazz.getName());
            }
            
            String lowerName = canonicalName.toLowerCase();
            String marker = "." + ItemModel.class.getSimpleName().toLowerCase() + ".";
            
            int markerIndex = lowerName.indexOf(marker);
            if (markerIndex == -1) {
                String underName = ItemModel.class.getCanonicalName();

                throw new IllegalArgumentException(
                    "Class must be nested under" + underName + ": " + canonicalName
                );
            }
            
            // Extract everything after the marker and convert to path
            String classPath = lowerName.substring(markerIndex + marker.length());
            String folderPath = classPath.replace('.', '/');
            
            // Append file name
            if (fileName.isEmpty()) {
                throw new IllegalArgumentException("File name cannot be empty");
            }
            
            return new NamespacedKey(NAMESPACE, folderPath + "/" + fileName);
        }
    }

    //#endregion Item Models
}
