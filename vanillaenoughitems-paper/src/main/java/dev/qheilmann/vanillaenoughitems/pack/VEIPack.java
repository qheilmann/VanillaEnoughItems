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

            public static class Common {
                public static final NamespacedKey ARROW_DOWN = path(Common.class, "arrow_down");
                public static final NamespacedKey ARROW_LEFT = path(Common.class, "arrow_left");
                public static final NamespacedKey ARROW_RIGHT = path(Common.class, "arrow_right");
                public static final NamespacedKey ARROW_UP = path(Common.class, "arrow_up");

                public static final NamespacedKey BACKWARD_DOUBLE = path(Common.class, "backward_double");
                public static final NamespacedKey BACKWARD = path(Common.class, "backward");

                public static final NamespacedKey BOOKMARK_LIST = path(Common.class, "bookmark_list");
                public static final NamespacedKey BOOKMARK_SERVER = path(Common.class, "bookmark_server");
                public static final NamespacedKey BOOKMARK_BOOKMARK = path(Common.class, "bookmark_bookmark");
                public static final NamespacedKey BOOKMARK_UNBOOKMARK = path(Common.class, "bookmark_unbookmark");

                public static final NamespacedKey FORWARD = path(Common.class, "forward");
                public static final NamespacedKey FORWARD_DOUBLE = path(Common.class, "forward_double");

                public static final NamespacedKey INFO = path(Common.class, "info");
                public static final NamespacedKey PLUS = path(Common.class, "plus");
                public static final NamespacedKey QUICKLINK = path(Common.class, "quicklink");
            }
            
            public static class Panel {
                public static final NamespacedKey RECIPE_ARROW_SMALL = path(Panel.class, "recipe_arrow_small");
                public static final NamespacedKey RECIPE_ARROW = path(Panel.class, "recipe_arrow");

                public static class Blasting {
                    public static final NamespacedKey BACKGROUND = path(Blasting.class, "background");
                }

                public static class Campfire {
                    public static final NamespacedKey BACKGROUND = path(Campfire.class, "background");
                }

                public static class Crafting {
                    public static final NamespacedKey BACKGROUND = path(Crafting.class, "background");
                }

                public static class Smelting {
                    public static final NamespacedKey BACKGROUND = path(Smelting.class, "background");
                }

                public static class Smoking {
                    public static final NamespacedKey BACKGROUND = path(Smoking.class, "background");
                }

                public static class StoneCutting {
                    public static final NamespacedKey BACKGROUND = path(StoneCutting.class, "background");
                }
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
