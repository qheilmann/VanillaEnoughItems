package me.qheilmann.vei.Core.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeHistory {

    private final List<RecipePath> history = new ArrayList<>();
    private int currentIndex = -1;

    public RecipeHistory() {
    }

    public void push(RecipePath state) {
        if (currentIndex < history.size() - 1) {
            history.subList(currentIndex + 1, history.size()).clear();
        }
        history.add(state);
        currentIndex++;
    }

    public RecipePath goBack() {
        if (currentIndex > 0){
            currentIndex--;  
            return getCurrent();
        }
        return null;
    }

    public RecipePath goForward() {
        if (currentIndex < history.size() - 1){
            currentIndex++;
            return getCurrent();
        }
        return null;
    }

    public RecipePath getCurrent() {
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
