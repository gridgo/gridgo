package io.gridgo.utils.hash;

import lombok.Getter;
import net.jpountz.xxhash.XXHash32;

public class XXHash32BinaryHashCodeCalculator extends BinaryHashCodeCalculator {

    public static final int DEFAULT_SEED = 0x9747b28c;

    private final XXHash32 hasher;
    private final int seed;

    @Getter
    private final int id;

    public XXHash32BinaryHashCodeCalculator(XXHash32 hasher, int id) {
        this(hasher, DEFAULT_SEED, id);
    }

    public XXHash32BinaryHashCodeCalculator(XXHash32 hasher, int seed, int id) {
        if (hasher == null) {
            throw new NullPointerException("Hasher cannot be null");
        }
        this.hasher = hasher;
        this.seed = seed;
        this.id = id;
    }

    @Override
    public int calcHashCode(byte[] bytes) {
        return hasher.hash(bytes, 0, bytes.length, this.seed);
    }
}