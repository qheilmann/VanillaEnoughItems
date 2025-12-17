package dev.qheilmann.vanillaenoughitems.gui.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import dev.qheilmann.vanillaenoughitems.recipe.extraction.RecipeExtractor;
import net.kyori.adventure.key.Key;

/**
 * Manages bookmarked recipes for a single player.
 * Currently, bookmarks are stored in-memory and cleared on logout.
 */
@NullMarked
public class BookmarkCollection {
    private final UUID playerUuid;
    private final RecipeExtractor recipeExtractor;

    private final Set<Key> bookmarkedRecipeKeys = new HashSet<>();

    /**
     * Create a new BookmarkCollection for the specified player
     * @param playerUuid the player's UUID
     * @param recipeExtractor the recipe extractor to identify recipes
     */
    public BookmarkCollection(UUID playerUuid, RecipeExtractor recipeExtractor) {
        this.playerUuid = playerUuid;
        this.recipeExtractor = recipeExtractor;
    }

    /**
     * Get the player's UUID associated with this bookmark collection
     * @return the player UUID
     */
    public UUID playerUuid() {
        return playerUuid;
    }

    /**
     * Check if a recipe is bookmarked
     * @param recipe the recipe to check
     * @return true if bookmarked, false otherwise
     */
    public boolean isBookmarked(Recipe recipe) {
        if (!recipeExtractor.canHandle(recipe)) {
            return false;
        }

        Key key = recipeExtractor.extractKey(recipe);
        return bookmarkedRecipeKeys.contains(key);
    }

    /**
     * Toggle bookmark status for a recipe
     * @param recipe the recipe to toggle
     * @return true if now bookmarked, false if now unbookmarked
     */
    public boolean toggleBookmark(Recipe recipe) {
        if (!recipeExtractor.canHandle(recipe)) {
            return false;
        }

        Key key = recipeExtractor.extractKey(recipe);
        if (bookmarkedRecipeKeys.contains(key)) {
            bookmarkedRecipeKeys.remove(key);
            return false;
        } else {
            bookmarkedRecipeKeys.add(key);
            return true;
        }
    }

    /**
     * Add a recipe to bookmarks
     * @param recipe the recipe to bookmark
     */
    public void addBookmark(Recipe recipe) {
        if (!recipeExtractor.canHandle(recipe)) {
            return;
        }

        Key key = recipeExtractor.extractKey(recipe);
        bookmarkedRecipeKeys.add(key);
    }

    /**
     * Remove a recipe from bookmarks
     * @param recipe the recipe to unbookmark
     */
    public void removeBookmark(Recipe recipe) {
        if (!recipeExtractor.canHandle(recipe)) {
            return;
        }

        Key key = recipeExtractor.extractKey(recipe);
        bookmarkedRecipeKeys.remove(key);
    }

    /**
     * Get all bookmarked recipe keys
     * @return unmodifiable set of bookmarked recipe keys
     */
    public Set<Key> getBookmarkedKeys() {
        return Set.copyOf(bookmarkedRecipeKeys);
    }

    /**
     * Clear all bookmarks
     */
    public void clear() {
        bookmarkedRecipeKeys.clear();
    }

    /**
     * Get the number of bookmarked recipes
     * @return bookmark count
     */
    public int getBookmarkCount() {
        return bookmarkedRecipeKeys.size();
    }
}
