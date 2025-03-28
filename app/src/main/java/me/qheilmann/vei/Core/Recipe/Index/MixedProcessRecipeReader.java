package me.qheilmann.vei.Core.Recipe.Index;

import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import me.qheilmann.vei.Core.Process.Process;

public class MixedProcessRecipeReader {
    private final @NotNull MixedProcessRecipeMap mixedProcessMap;
    private @NotNull Process<?> currentProcess;

    public MixedProcessRecipeReader(MixedProcessRecipeMap mixedProcessRecipeMap) {
        mixedProcessMap = mixedProcessRecipeMap;
        currentProcess = mixedProcessMap.getAllProcess().first();
    }

    public MixedProcessRecipeReader(MixedProcessRecipeMap mixedProcessRecipeMap, Process<?> process) {
        this(mixedProcessRecipeMap);
        setProcess(process);
    }

    public MixedProcessRecipeReader(MixedProcessRecipeMap mixedProcessRecipeMap, Process<?> process, Recipe recipe) {
        this(mixedProcessRecipeMap, process);
        currentProcessRecipeReader().setRecipe(recipe);
    }

    public void setProcess(Process<?> process) {
        Objects.requireNonNull(process, "Process cannot be null.");

        if (!mixedProcessMap.getAllProcess().contains(process)) {
            throw new IllegalArgumentException("Process not found in the recipe map: " + process.getProcessName());
        }
        this.currentProcess = process;
    }

    public Process<?> currentProcess() {
        return currentProcess;
    }

    public ProcessRecipeReader currentProcessRecipeReader() {
        throw new UnsupportedOperationException("Not implemented yet."); // TODO Impl currentProcessRecipeReader()
    }

    public boolean hasNext() {
        return mixedProcessMap.getAllProcess().higher(currentProcess) != null;
    }

    public Process<?> next() {
        Process<?> newProcess = mixedProcessMap.getAllProcess().higher(currentProcess);
        if (newProcess == null) {
            throw new NoSuchElementException("No next process available.");
        }
        return currentProcess = newProcess;
    }

    public boolean hasPrevious() {
        return mixedProcessMap.getAllProcess().lower(currentProcess) != null;
    }

    public Process<?> previous() {
        Process<?> newProcess = mixedProcessMap.getAllProcess().lower(currentProcess);
        if (newProcess == null) {
            throw new NoSuchElementException("No previous process available.");
        }
        return currentProcess = newProcess;
    }

    public Process<?> first() {
        if (mixedProcessMap.isEmpty()) {
            throw new NoSuchElementException("No processes available.");
        }
        return currentProcess = mixedProcessMap.getAllProcess().first();
    }

    public Process<?> last() {
        if (mixedProcessMap.isEmpty()) {
            throw new NoSuchElementException("No processes available.");
        }
        return currentProcess = mixedProcessMap.getAllProcess().last();
    }

    public boolean Contains(Process<?> process) {
        return mixedProcessMap.getAllProcess().contains(process);
    }

    public NavigableSet<Process<?>> getAllProcess() {
        return mixedProcessMap.getAllProcess();
    }
}
