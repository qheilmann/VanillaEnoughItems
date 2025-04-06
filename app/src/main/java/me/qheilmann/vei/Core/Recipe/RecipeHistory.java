package me.qheilmann.vei.Core.Recipe;

import java.util.ArrayList;
import java.util.List;

import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;

public class RecipeHistory {

    private final List<MixedProcessRecipeReader> history = new ArrayList<>();
    private int currentIndex = -1;

    public RecipeHistory() {}

    public void push(MixedProcessRecipeReader state) {
        // Clear the history forward the current index if we go forward with a different path.
        if (currentIndex < history.size() - 1) {
            history.subList(currentIndex + 1, history.size()).clear();
        }

        history.add(state);
        currentIndex++;
    }

    public MixedProcessRecipeReader goBack() {
        if (currentIndex > 0) {
            currentIndex--;
            return getCurrent();
        }
        return null;
    }

    public MixedProcessRecipeReader goForward() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
            return getCurrent();
        }
        return null;
    }

    public MixedProcessRecipeReader getCurrent() {
        if (currentIndex >= 0 && currentIndex < history.size()) {
            return history.get(currentIndex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "RecipeHistory{" +
                "history=" + history +
                ", currentIndex=" + currentIndex +
                '}';
    }
}
