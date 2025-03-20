package me.qheilmann.vei.Core.Recipe;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import me.qheilmann.vei.Core.Process.Process;

import java.util.HashMap;
import java.util.Map;

public record RecipePath(ItemStack itemStack, Process<?> process, int variant)
        implements ConfigurationSerializable {

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Process<?> getProcess() {
        return process;
    }

    public int getVariant() {
        return variant;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((itemStack == null) ? 0 : itemStack.hashCode());
        result = prime * result + ((process == null) ? 0 : process.hashCode());
        result = prime * result + variant;
        return result;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipePath that = (RecipePath) o;
        return variant == that.variant &&
               itemStack.equals(that.itemStack) &&
               process.equals(that.process);
    }

    @Override
    public final String toString() {
        return "RecipePath{" +
                "itemStack=" + itemStack +
                ", process=" + process +
                ", variant=" + variant +
                '}';
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("itemStack", itemStack.getType().key().toString());
        data.put("variant", variant);
        // Temporary placeholder for process
        data.put("process", process.getProcessName());
        return data;
    }

    public static RecipePath deserialize(Map<String, Object> args) {
        ItemStack itemStack = new ItemStack(org.bukkit.Material.matchMaterial((String) args.get("itemStack"))); // TODO adapt to each ItemStack
        int variant = (int) args.get("variant");
        Process<?> process = Process.getProcessByName((String) args.get("process"));

        return new RecipePath(itemStack, process, variant);
    }
}
