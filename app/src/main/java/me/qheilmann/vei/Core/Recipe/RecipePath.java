package me.qheilmann.vei.Core.Recipe;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import me.qheilmann.vei.Core.Process.Process;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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
        String itemStackName = PlainTextComponentSerializer.plainText().serialize(itemStack.displayName());
        return "RecipePath{" +
                "itemStack=" + itemStackName +
                ", process=" + process.getProcessName() +
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
        ItemStack itemStack = new ItemStack(Material.matchMaterial((String) args.get("itemStack")));
        int variant = (int) args.get("variant");
        Process<?> process = Process.getProcessByName((String) args.get("process"));

        return new RecipePath(itemStack, process, variant);
    }
}
