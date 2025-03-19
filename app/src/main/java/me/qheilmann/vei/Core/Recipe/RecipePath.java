package me.qheilmann.vei.Core.Recipe;

import org.bukkit.inventory.ItemStack;
import me.qheilmann.vei.Core.Process.Process;

public record RecipePath(ItemStack itemStack, Process<?> process, int variant) {

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
    public final boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        }
        if (arg0 == null) {
            return false;
        }
        if (getClass() != arg0.getClass()) {
            return false;
        }
        RecipePath other = (RecipePath) arg0;
        if (itemStack == null) {
            if (other.itemStack != null) {
                return false;
            }
        } else if (!itemStack.equals(other.itemStack)) {
            return false;
        }
        if (process == null) {
            if (other.process != null) {
                return false;
            }
        } else if (!process.equals(other.process)) {
            return false;
        }
        if (variant != other.variant) {
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        return "RecipePath{" +
                "itemStack=" + itemStack +
                ", process=" + process +
                ", variant=" + variant +
                '}';
    }
}
