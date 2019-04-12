package io.gridgo.utils;

import java.lang.reflect.Field;
import java.util.Optional;

import lombok.Getter;
import sun.misc.Unsafe;

public final class UnsafeUtils {

    @Getter
    private static final Unsafe unsafe = ensureUnsafeAvalable();

    public static final boolean isUnsafeAvailable() {
        return unsafe != null;
    }

    public static final Optional<Unsafe> getOptionalUnsafe() {
        return Optional.ofNullable(unsafe);
    }

    private static final Unsafe ensureUnsafeAvalable() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Cannot init unsafe instance", e);
        }
    }
}
