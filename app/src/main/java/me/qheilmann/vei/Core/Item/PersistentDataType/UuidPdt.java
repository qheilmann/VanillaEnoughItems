package me.qheilmann.vei.Core.Item.PersistentDataType;

import java.util.UUID;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class UuidPdt implements PersistentDataType<int[], UUID> {
    public static final UuidPdt TYPE = new UuidPdt();

    @Override
    public Class<int[]> getPrimitiveType() {
        return int[].class;
    }

    @Override
    public Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public int[] toPrimitive(UUID complex, PersistentDataAdapterContext context) {
        long mostSigBits = complex.getMostSignificantBits();
        long leastSigBits = complex.getLeastSignificantBits();
        return new int[] {
            (int) (mostSigBits >> 32),
            (int) mostSigBits,
            (int) (leastSigBits >> 32),
            (int) leastSigBits
        };
    }

    @Override
    public UUID fromPrimitive(int[] primitive, PersistentDataAdapterContext context) {
        long mostSigBits = ((long) primitive[0] << 32) | (primitive[1] & 0xFFFFFFFFL);
        long leastSigBits = ((long) primitive[2] << 32) | (primitive[3] & 0xFFFFFFFFL);
        return new UUID(mostSigBits, leastSigBits);
    }
}