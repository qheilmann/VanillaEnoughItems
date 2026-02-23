package dev.qheilmann.vanillaenoughitems.bookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanel;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPanelRegistry;
import dev.qheilmann.vanillaenoughitems.gui.processpannel.ProcessPannelSlot;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.MultiProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.ProcessRecipeReader;
import dev.qheilmann.vanillaenoughitems.recipe.index.reader.RecipeIndexView;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;
import dev.qheilmann.vanillaenoughitems.config.Style;
import dev.qheilmann.vanillaenoughitems.gui.CyclicIngredient;
import dev.qheilmann.vanillaenoughitems.gui.helper.GuiComponentHelper;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Implementation of {@link Bookmark}.
 * Can be created from recipe keys or readers.
 * 
 * Equality is based on MultiProcessRecipeMap content - bookmarks with
 * the same recipes are considered duplicates regardless of defaults/symbols.
 */
@NullMarked
public class BookmarkImpl implements Bookmark {
    private final MultiProcessRecipeReader reader;
    private final CyclicIngredient symbol;

    /**
     * Private constructor - use factory methods to create bookmarks
     * @param reader the reader containing all recipe data
     * @param symbol the visual symbol for this bookmark
     */
    private BookmarkImpl(MultiProcessRecipeReader reader, CyclicIngredient symbol) {
        this.reader = reader;
        this.symbol = symbol;
    }

    /**
     * Create a bookmark from a MultiProcessRecipeReader with custom symbol
     * @param reader the reader
     * @param symbol the symbol to display
     * @return a new bookmark
     */
    public static Bookmark fromReader(MultiProcessRecipeReader reader, CyclicIngredient symbol) {
        return new BookmarkImpl(reader, symbol);
    }

    /**
     * Create a bookmark from a recipe key.
     * Extracts the symbol from the ProcessPanel's ticked results for proper cycling support.
     * @param key the recipe key
     * @param recipeIndex the recipe index view
     * @param panelRegistry the process panel registry for symbol extraction
     * @param style the style configuration
     * @return a new bookmark, or null if the key doesn't exist in any recipes
     */
    @Nullable
    public static Bookmark fromKey(Key key, RecipeIndexView recipeIndex, ProcessPanelRegistry panelRegistry, Style style) {
        MultiProcessRecipeReader reader = recipeIndex.readerByKey(key);
        if (reader == null) {
            return null;
        }

        Process process = reader.getCurrentProcess();
        ProcessRecipeReader processReader = reader.getCurrentProcessRecipeReader();
        Recipe recipe = processReader.getCurrent();
        
        // Extract symbol from ProcessPanel's ticked results
        CyclicIngredient symbol = extractCyclicResultFromPanel(process, recipe, panelRegistry, style);

        // Add lore to indicate it's a bookmark by key
        CyclicIngredient symbolLored = new CyclicIngredient(
            items -> {
                ItemStack item = items[0];
                ItemStack itemLored = item.clone();
                ItemMeta meta = itemLored.getItemMeta();
                if (meta == null) return itemLored;

                List<Component> lore = new ArrayList<>();
                if (meta.hasLore()) {
                    lore = meta.lore();
                }
                lore.add(Component.text().applicableApply(style.colorPrimary()).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Recipe with key: "))
                    .append(Component.text(key.asMinimalString(), NamedTextColor.WHITE).decorate(TextDecoration.UNDERLINED))
                    .build()
                );

                meta.lore(lore);
                itemLored.setItemMeta(meta);
                return itemLored;
                
            }, symbol
        );
        
        return new BookmarkImpl(reader, symbolLored);
    }

    /**
     * Create a bookmark from a result ItemStack.
     * Uses a simple non-cycling symbol.
     * @param recipeIndex the recipe index view
     * @param result the result item to use as symbol
     * @param style the style configuration
     * @return a new bookmark, or null if the result doesn't exist in any recipes
     */
    @Nullable
    public static Bookmark fromResult(RecipeIndexView recipeIndex, ItemStack result, Style style) {
        MultiProcessRecipeReader reader = recipeIndex.readerByResult(result);
        if (reader == null) {
            return null;
        }
        
        // Simple non-cycling symbol using the result item
        CyclicIngredient symbol = new CyclicIngredient(0, result);
        
        // Add lore to indicate it's a bookmark symbol
        CyclicIngredient symbolLored = new CyclicIngredient(
            items -> {
                ItemStack item = items[0];
                ItemStack itemLored = item.clone();
                ItemMeta meta = itemLored.getItemMeta();
                if (meta == null) return itemLored;

                List<Component> lore = new ArrayList<>();
                if (meta.hasLore()) {
                    lore = meta.lore();
                }
                lore.add(Component.text().applicableApply(style.colorPrimary()).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Recipes with "))
                    .append(item.displayName())
                    .append(Component.text(" as result", style.colorPrimary()))
                    .build()
                );

                meta.lore(lore);
                itemLored.setItemMeta(meta);
                return itemLored;
                
            }, symbol
        );

        return new BookmarkImpl(reader, symbolLored);
    }

    /**
     * Extract the symbol CyclicIngredient from the ProcessPanel's ticked results.
     * Falls back to recipe result if panel has no ticked results.
     */
    private static CyclicIngredient extractCyclicResultFromPanel(Process process, Recipe recipe, ProcessPanelRegistry panelRegistry, Style style) {
        ProcessPanel panel = panelRegistry.createPanel(process, recipe, style);
        Map<ProcessPannelSlot, CyclicIngredient> tickedResults = panel.getTickedResults();

        if (tickedResults.isEmpty()) {
            return new CyclicIngredient(0, GuiComponentHelper.createFillerItem(style.hasResourcePack())); // Fallback to empty ingredient if no ticked results (shouldn't happen for valid recipes)
        }

        return tickedResults.values().iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiProcessRecipeReader getReader() {
        return new MultiProcessRecipeReader(reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CyclicIngredient getSymbol() {
        return symbol;
    }

    /**
     * Equality based on grouping only.
     * Same grouping = duplicate bookmark regardless of symbols/position.
     */
    @SuppressWarnings("null")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BookmarkImpl bookmark)) return false;
        if (!reader.equals(bookmark.reader)) return false;

        return symbol.equals(bookmark.symbol, false);
    }

    @Override
    public int hashCode() {
        return reader.getGrouping().hashCode();
    }
}
