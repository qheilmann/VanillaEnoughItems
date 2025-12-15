package dev.qheilmann.vanillaenoughitems.index.processrecipe.reader;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import dev.qheilmann.vanillaenoughitems.index.process.Process;
import dev.qheilmann.vanillaenoughitems.index.processrecipe.MultiProcessRecipeMap;
import dev.qheilmann.vanillaenoughitems.index.processrecipe.ProcessRecipeSet;

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
    @SuppressWarnings("null")
    public MultiProcessRecipeReader(MultiProcessRecipeMap multiProcessRecipeMap, Process startProcess) {
        this.multiProcessRecipeMap = multiProcessRecipeMap;
        setCurrentProcess(startProcess);
    }

    /**
     * Set the current process
     * @param process the process to set as current.<br>
     * Use {@link #containsProcess(Process)} to check if the process exists in the MultiProcessRecipeMap
     * @throws IllegalArgumentException if the process does not exist in the MultiProcessRecipeMap
     */
    public ProcessRecipeReader setCurrentProcess(Process process) {
        if (!multiProcessRecipeMap.getAllProcesses().contains(process)) {
            throw new IllegalArgumentException("Process does not exist in the MultiProcessRecipeMap");
        }

        if (getCurrentProcess().equals(process)) {
            // Prevents replacing the currentProcessRecipeReader.
            // Ensures the current process and recipes are not reset if no changes are made.
            return currentProcessRecipeReader;
        }

        ProcessRecipeSet processRecipeSet = multiProcessRecipeMap.getProcessRecipeSet(process);
        if (processRecipeSet == null) {
            throw new IllegalArgumentException("ProcessRecipeSet does not exist for the given process");
        }

        return currentProcessRecipeReader = new ProcessRecipeReader(processRecipeSet);
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
}
