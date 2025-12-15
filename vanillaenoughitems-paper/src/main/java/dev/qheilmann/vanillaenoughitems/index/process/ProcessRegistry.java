package dev.qheilmann.vanillaenoughitems.index.process;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NullUnmarked;

import net.kyori.adventure.key.Key;

@NullUnmarked
public class ProcessRegistry {

    private final Map<Key, Process> processes = new HashMap<>();

    public void registerProcess(Process process) {
        this.processes.put(process.key(), process);
    }

    public Process get(Key key) {
        return this.processes.get(key);
    }
}
