package me.qheilmann.vei.Core.Recipe.Index.Reader;

import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.annotation.Nullable;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import me.qheilmann.vei.Core.Process.Process;
import me.qheilmann.vei.Core.Recipe.Index.MixedProcessRecipeMap;

public class MixedProcessRecipeReader {
    private final @NotNull MixedProcessRecipeMap mixedProcessMap;
    /**
     * The process currently being read.
     */
    private @NotNull Process<?> currentProcess;
    /**
     * The process that was active the last time the ProcessRecipeReader was accessed.
     */
    private @Nullable Process<?> lastProcess; // Cache the last process to avoid unnecessary reloading.
    private ProcessRecipeReader<?> currentProcessRecipeReader;

    public MixedProcessRecipeReader(MixedProcessRecipeMap mixedProcessRecipeMap) {
        this(mixedProcessRecipeMap, mixedProcessRecipeMap.getAllProcess().first());
    }

    public <R extends Recipe> MixedProcessRecipeReader(MixedProcessRecipeMap mixedProcessRecipeMap, Process<R> process) {
        Objects.requireNonNull(mixedProcessRecipeMap, "MixedProcessRecipeMap cannot be null.");
        Objects.requireNonNull(process, "Process cannot be null.");

        mixedProcessMap = mixedProcessRecipeMap;
        setProcess(process);
    }

    public void setProcess(Process<?> process) {
        Objects.requireNonNull(process, "Process cannot be null.");

        if (process == currentProcess) {
            // Prevents replacing the currentProcessRecipeReader.
            // Ensures the current process and recipes are not reset if no changes are made.
            return;
        }

        if (!mixedProcessMap.getAllProcess().contains(process)) {
            throw new IllegalArgumentException("Process not found in the recipe map: " + process.getProcessName());
        }

        this.currentProcess = process;
    }

    public Process<?> currentProcess() {
        return currentProcess;
    }

    public ProcessRecipeReader<?> currentProcessRecipeReader() {
        // Return the cached reader if the process hasn't changed.
        if (currentProcessRecipeReader != null && lastProcess != null && lastProcess.equals(currentProcess)) {
            return currentProcessRecipeReader;
        } 

        lastProcess = currentProcess;
        currentProcessRecipeReader = new ProcessRecipeReader<>(mixedProcessMap.getProcessRecipeSet(currentProcess));
        return currentProcessRecipeReader;
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
