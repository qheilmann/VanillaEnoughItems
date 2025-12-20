package dev.qheilmann.vanillaenoughitems.recipe.index.reader;

import java.util.NavigableSet;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.recipe.index.MultiProcessRecipeMap;
import dev.qheilmann.vanillaenoughitems.recipe.index.ProcessRecipeSet;
import dev.qheilmann.vanillaenoughitems.recipe.process.Process;

/**
 * Walk over an MultiProcessRecipeMap and save current process position.
 * The inner processRecipeMap can't be accessed to avoid modification.
 */
@NullMarked
public class MultiProcessRecipeReader {
    private final MultiProcessRecipeMap multiProcessRecipeMap;
    private ProcessRecipeReader currentProcessRecipeReader;

    /**
     * Create a MultiProcessRecipeReader starting at the first process
     * @param multiProcessRecipeMap the MultiProcessRecipeMap to read from
     */
    public MultiProcessRecipeReader(MultiProcessRecipeMap multiProcessRecipeMap) {
        this(multiProcessRecipeMap, multiProcessRecipeMap.getAllProcesses().first());
    }

    /**
     * Create a MultiProcessRecipeReader starting at a specific process
     * @param multiProcessRecipeMap the MultiProcessRecipeMap to read from
     * @param startProcess the process to start at
     */
    public MultiProcessRecipeReader(MultiProcessRecipeMap multiProcessRecipeMap, Process startProcess) {
        this.multiProcessRecipeMap = multiProcessRecipeMap;
        this.currentProcessRecipeReader = initializeProcess(startProcess);
    }

    /**
     * Initialize and validate a process, creating a new ProcessRecipeReader
     * @param process the process to initialize
     * @return a new ProcessRecipeReader for the process
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    private ProcessRecipeReader initializeProcess(Process process) {
        if (!multiProcessRecipeMap.getAllProcesses().contains(process)) {
            throw new IllegalArgumentException("Process does not exist in the MultiProcessRecipeMap");
        }

        ProcessRecipeSet processRecipeSet = multiProcessRecipeMap.getProcessRecipeSet(process);
        if (processRecipeSet == null) {
            throw new IllegalArgumentException("ProcessRecipeSet does not exist for the given process");
        }

        return new ProcessRecipeReader(processRecipeSet);
    }

    /**
     * Set the current process
     * @param process the process to set as current. Use {@link #containsProcess(Process)} to check if the process exists in the MultiProcessRecipeMap
     * @return the ProcessRecipeReader for the current process
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    public ProcessRecipeReader setCurrentProcess(Process process) {
        if (getCurrentProcess().equals(process)) {
            // Prevents replacing the currentProcessRecipeReader.
            // Ensures the current process and recipes are not reset if no changes are made.
            return currentProcessRecipeReader;
        }

        currentProcessRecipeReader = initializeProcess(process);
        return currentProcessRecipeReader;
    }

    /**
     * Get the current process
     * @return the current process
     */
    public Process getCurrentProcess() {
        return currentProcessRecipeReader.getAssociatedProcess();
    }

    /**
     * Get the current ProcessRecipeReader
     * @return the current ProcessRecipeReader
     */
    public ProcessRecipeReader getCurrentProcessRecipeReader() {
        return currentProcessRecipeReader;
    }

    /**
     * Check if the MultiProcessRecipeMap contains a specific process
     * @param process the process to check
     * @return true if the process exists in the MultiProcessRecipeMap, false otherwise
     */
    public boolean containsProcess(Process process) {
        return multiProcessRecipeMap.getAllProcesses().contains(process);
    }

    /**
     * Check if the current process is the first process in the MultiProcessRecipeMap
     * @return true if the current process is the first process, false otherwise
     */
    public boolean isFirst() {
        return getCurrentProcess().equals(multiProcessRecipeMap.getAllProcesses().first());
    }

    /**
     * Move to the previous process
     * @return the ProcessRecipeReader for the previous process, or null if there is no previous process
     */
    @Nullable
    public ProcessRecipeReader previous() {
        Process previousProcess = multiProcessRecipeMap.getAllProcesses().lower(getCurrentProcess());
        if (previousProcess == null) {
            return null;
        }
        setCurrentProcess(previousProcess);
        return currentProcessRecipeReader;
    }

    public boolean isLast() {
        return getCurrentProcess().equals(multiProcessRecipeMap.getAllProcesses().last());
    }

    /**
     * Move to the next process
     * @return the ProcessRecipeReader for the next process, or null if there is no next process
     */
    @Nullable
    public ProcessRecipeReader next() {
        Process nextProcess = multiProcessRecipeMap.getAllProcesses().higher(getCurrentProcess());
        if (nextProcess == null) {
            return null;
        }
        setCurrentProcess(nextProcess);
        return currentProcessRecipeReader;
    }

    /**
     * Get all processes in the MultiProcessRecipeMap
     * @return a NavigableSet of all processes
     */
    public NavigableSet<Process> getAllProcesses() {
        return multiProcessRecipeMap.getAllProcesses();
    }
}
