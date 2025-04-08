package me.qheilmann.vei.Core.Recipe;

import java.util.ArrayList;
import java.util.List;

import me.qheilmann.vei.Core.Recipe.Index.Reader.MixedProcessRecipeReader;

public class RecipeHistory {

    private final List<MixedProcessRecipeReader> history = new ArrayList<>();
    private int currentIndex = -1;

    public RecipeHistory() {}

    public void push(MixedProcessRecipeReader state) {

        // If the state is null, or the same as the current state, don't add it to the history.
        if (state == null || state.equals(getCurrent())) {
            return;
        }

        // Clear the history forward the current index if we go forward with a different path.
        if (currentIndex < history.size() - 1) {
            history.subList(currentIndex + 1, history.size()).clear();
        }

        history.add(state);
        currentIndex++;
    }

    public MixedProcessRecipeReader goBackward() {
        if (hasBackward()) {
            currentIndex--;
            return getCurrent();
        }
        return null;
    }

    public MixedProcessRecipeReader goForward() {
        if (hasForward()) {
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

    public boolean hasBackward() {
        return currentIndex > 0;
    }

    public boolean hasForward() {
        return currentIndex < history.size() - 1;
    }

    @Override
    public String toString() {
        return "RecipeHistory{" +
                "history=" + history +
                ", currentIndex=" + currentIndex +
                '}';
    }
}
