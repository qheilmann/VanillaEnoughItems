package dev.qheilmann.vanillaenoughitems.recipe.process;

import org.jspecify.annotations.NullMarked;
import net.kyori.adventure.key.Key;

/**
 * Abstract base class for Process implementations.
 * Provides consistent equals() and hashCode() implementations based on the process key.
 */
@NullMarked
public abstract class AbstractProcess implements Process {

    private final Key key;

    protected AbstractProcess(Key key) {
        this.key = key;
    }

    @Override
    public final Key key() {
        return key;
    }

    /**
     * Two processes are equal if they have the same key.
     * This ensures symmetric, transitive, and consistent equality.
     */
    @Override
    public final boolean equals(@SuppressWarnings("null") Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Process other)) return false;
        return this.key().equals(other.key());
    }

    /**
     * Hash code is based on the process key to ensure consistency with equals.
     */
    @Override
    public final int hashCode() {
        return key().hashCode();
    }
}
