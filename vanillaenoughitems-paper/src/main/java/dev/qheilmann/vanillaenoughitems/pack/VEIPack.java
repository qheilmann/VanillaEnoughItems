package dev.qheilmann.vanillaenoughitems.pack;

import org.bukkit.NamespacedKey;

import dev.qheilmann.vanillaenoughitems.VanillaEnoughItems;

public class VeiPack {

    public static final String NAMESPACE = VanillaEnoughItems.NAMESPACE;

    //#region Characters

    public static class Font {
        public static class Gui {
            public static final GuiIcon BLANK_54 = new GuiIcon("\uE100", -8, 176);
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

                public static class Catalyst {
                    // Don't forget to constants and method in ModelSelector when modifying these
                    public static final NamespacedKey _1 = path(Catalyst.class, "1");
                    public static final NamespacedKey _2 = path(Catalyst.class, "2");
                    public static final NamespacedKey _3 = path(Catalyst.class, "3");
                    public static final NamespacedKey _4 = path(Catalyst.class, "4");
                    public static final NamespacedKey _3_AND_UP = path(Catalyst.class, "3_and_up");
                    
                    public static final int MAX_SCROLLABLE_CATALYST = 3;
                    public static final int MAX_NON_SCROLLABLE_CATALYSTS = MAX_SCROLLABLE_CATALYST + 1;
                    
                    /**
                     * Gets the catalyst model key for the given parameters.
                     * @param nbOfCatalyst Number of catalysts
                     * @param showUpButton Whether an up button is shown, nbOfCatalyst is ignored if true
                     * @return The NamespacedKey for the catalyst model
                     */
                    public static NamespacedKey getCatalystModel(int nbOfCatalyst, boolean showUpButton, boolean isAllCatalystVisible) {
                        return ModelSelector.getCatalystModel(nbOfCatalyst, showUpButton, isAllCatalystVisible);
                    }
                }

                public static class Panel {
                    public static final NamespacedKey BLASTING = path(Panel.class, "blasting");
                    public static final NamespacedKey CAMPFIRE = path(Panel.class, "campfire");
                    public static final NamespacedKey CRAFTING = path(Panel.class, "crafting");
                    public static final NamespacedKey SMELTING = path(Panel.class, "smelting");
                    public static final NamespacedKey SMOKING = path(Panel.class, "smoking");
                    public static final NamespacedKey STONECUTTING = path(Panel.class, "stonecutting");
                    public static final NamespacedKey SMITHING = path(Panel.class, "smithing");
                }

                public static class Process {
                    // Don't forget to constants and method in ModelSelector when modifying these
                    public static final NamespacedKey _1_0 = path(Process.class, "1_0");
                    public static final NamespacedKey _2_0 = path(Process.class, "2_0");
                    public static final NamespacedKey _2_1 = path(Process.class, "2_1");
                    public static final NamespacedKey _3_0 = path(Process.class, "3_0");
                    public static final NamespacedKey _3_1 = path(Process.class, "3_1");
                    public static final NamespacedKey _3_2 = path(Process.class, "3_2");
                    public static final NamespacedKey _4_0 = path(Process.class, "4_0");
                    public static final NamespacedKey _4_1 = path(Process.class, "4_1");
                    public static final NamespacedKey _4_2 = path(Process.class, "4_2");
                    public static final NamespacedKey _4_3 = path(Process.class, "4_3");
                    public static final NamespacedKey _5_0 = path(Process.class, "5_0");
                    public static final NamespacedKey _5_1 = path(Process.class, "5_1");
                    public static final NamespacedKey _5_2 = path(Process.class, "5_2");
                    public static final NamespacedKey _5_3 = path(Process.class, "5_3");
                    public static final NamespacedKey _5_4 = path(Process.class, "5_4");
                    public static final NamespacedKey _6_0 = path(Process.class, "6_0");
                    public static final NamespacedKey _6_1 = path(Process.class, "6_1");
                    public static final NamespacedKey _6_2 = path(Process.class, "6_2");
                    public static final NamespacedKey _6_3 = path(Process.class, "6_3");
                    public static final NamespacedKey _6_4 = path(Process.class, "6_4");
                    public static final NamespacedKey _6_5 = path(Process.class, "6_5");
                    public static final NamespacedKey _5_0_AND_LEFT = path(Process.class, "5_0_and_left");
                    public static final NamespacedKey _5_1_AND_LEFT = path(Process.class, "5_1_and_left");
                    public static final NamespacedKey _5_2_AND_LEFT = path(Process.class, "5_2_and_left");
                    public static final NamespacedKey _5_3_AND_LEFT = path(Process.class, "5_3_and_left");
                    public static final NamespacedKey _5_4_AND_LEFT = path(Process.class, "5_4_and_left");
                    public static final NamespacedKey _5_NON_VISIBLE_AND_LEFT = path(Process.class, "5_non_visible_and_left");

                    public static final int MAX_SCROLLABLE_PROCESSES = 5;
                    public static final int MAX_NON_SCROLLABLE_PROCESSES = MAX_SCROLLABLE_PROCESSES + 1;

                    /**
                     * Gets the process model key for the given parameters.
                     * @param nbOfProcesses Number of processes
                     * @param processIndex Zero-based index of the current showed process (0 to nbOfProcesses-1)
                     * @param showLeftButton Whether a left button is shown, only relevant for 5+ processes
                     * @return The NamespacedKey for the process model
                     */
                    public static final NamespacedKey getProcessModel(int nbOfProcesses, int processIndex, boolean showLeftButton, boolean isAllProcessVisible) {
                        return ModelSelector.getProcessModel(nbOfProcesses, processIndex, showLeftButton, isAllProcessVisible);
                    }
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
                public static final NamespacedKey SHAPELESS_INDICATOR = path(Decoration.class, "shapeless_indicator");
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
            
            // Find the marker position
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

    //#region Model Selection

    /**
     * Provides utility methods to select appropriate models based on dynamic parameters.
     */
    private static class ModelSelector {

        /**
         * Selects the appropriate catalyst model based on the number of catalysts
         * and whether an up scroll button is shown.
         * 
         * @param totalNbOfCatalyst Number of catalysts, ignored if showUpButton is true
         * @param showUpButton Whether an up scroll button is shown
         * @return The NamespacedKey for the appropriate catalyst model
         */
        public static NamespacedKey getCatalystModel(int totalNbOfCatalyst, boolean showUpButton, boolean isAllCatalystVisible) {
            // Special case: show up button uses the specific "3_and_up" model
            if (showUpButton) {
                return ItemModel.path(ItemModel.Gui.Background.Catalyst.class, "3_and_up");
            }

            // Clamp nbOfCatalyst to valid range
            int nbOfVisibleCatalyst = totalNbOfCatalyst;
            if (isAllCatalystVisible) {
                if (totalNbOfCatalyst < 0 || totalNbOfCatalyst > ItemModel.Gui.Background.Catalyst.MAX_NON_SCROLLABLE_CATALYSTS) {
                    nbOfVisibleCatalyst = ItemModel.Gui.Background.Catalyst.MAX_NON_SCROLLABLE_CATALYSTS;
                }
            } else {
                if (totalNbOfCatalyst < 0 || totalNbOfCatalyst > ItemModel.Gui.Background.Catalyst.MAX_SCROLLABLE_CATALYST) {
                    nbOfVisibleCatalyst = ItemModel.Gui.Background.Catalyst.MAX_SCROLLABLE_CATALYST;
                }
            }
            
            // Build file name and return model key
            return ItemModel.path(ItemModel.Gui.Background.Catalyst.class, String.valueOf(nbOfVisibleCatalyst));
        }

        /**
         * Selects the appropriate process model based on the number of processes, 
         * the process index, and whether a left scroll button is shown.
         * 
         * @param totalNbOfProcess Number of processes
         * @param processIndex Zero-based index of the current showed process
         * @param showLeftButton Whether a left scroll button is shown (only relevant for 5+ processes)
         * @return The NamespacedKey for the appropriate process model
         * @throws IllegalArgumentException if parameters are invalid
         */
        public static NamespacedKey getProcessModel(int totalNbOfProcess, int processIndex, boolean showLeftButton, boolean isAllProcessVisible) {
            // Validation
            if (!showLeftButton && processIndex < 0 || processIndex > ItemModel.Gui.Background.Process.MAX_NON_SCROLLABLE_PROCESSES) {
                throw new IllegalArgumentException(
                    "Process index must be between 0 and " + (totalNbOfProcess - 1) + ", got: " + processIndex
                );
            }

            // Clamp nbOfVisibleProcesses to valid range
            int nbOfVisibleProcesses = totalNbOfProcess;
            if (isAllProcessVisible) {
                if (totalNbOfProcess < 0 || totalNbOfProcess > ItemModel.Gui.Background.Process.MAX_NON_SCROLLABLE_PROCESSES) {
                    nbOfVisibleProcesses = ItemModel.Gui.Background.Process.MAX_NON_SCROLLABLE_PROCESSES;
                }
            } else {
                if (totalNbOfProcess < 0 || totalNbOfProcess > ItemModel.Gui.Background.Process.MAX_SCROLLABLE_PROCESSES) {
                    nbOfVisibleProcesses = ItemModel.Gui.Background.Process.MAX_SCROLLABLE_PROCESSES;
                }
            }

            // Non visible cases            
            if (processIndex < 0 || processIndex > nbOfVisibleProcesses - 1) {
                return ItemModel.Gui.Background.Process._5_NON_VISIBLE_AND_LEFT;
            }
            
            // 5+ processes with left button
            if (showLeftButton) {
                return ItemModel.path(ItemModel.Gui.Background.Process.class, "5_" + processIndex + "_and_left");
            }

            String fileName = nbOfVisibleProcesses + "_" + processIndex;
            return ItemModel.path(ItemModel.Gui.Background.Process.class, fileName);
        }
    }

    //#endregion Model Selection Helpers
}
