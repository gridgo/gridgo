package io.gridgo.bean;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ImmutableBObject extends BObject {

    static final Supplier<RuntimeException> UNSUPPORTED = () -> new UnsupportedOperationException(
            "Instance of ImmutableBObject cannot be modified");

    @Override
    default BElement put(String key, BElement value) {
        throw UNSUPPORTED.get();
    }

    @Override
    default BElement remove(Object key) {
        throw UNSUPPORTED.get();
    }

    @Override
    default void putAll(Map<? extends String, ? extends BElement> m) {
        throw UNSUPPORTED.get();
    }

    @Override
    default void replaceAll(BiFunction<? super String, ? super BElement, ? extends BElement> function) {
        throw UNSUPPORTED.get();
    }

    @Override
    default BElement putIfAbsent(String key, BElement value) {
        throw UNSUPPORTED.get();
    }

    @Override
    default boolean remove(Object key, Object value) {
        throw UNSUPPORTED.get();
    }

    @Override
    default boolean replace(String key, BElement oldValue, BElement newValue) {
        throw UNSUPPORTED.get();
    }

    @Override
    default BElement replace(String key, BElement value) {
        throw UNSUPPORTED.get();
    }

    @Override
    default void clear() {
        throw UNSUPPORTED.get();
    }

    @Override
    default BElement computeIfAbsent(String key, Function<? super String, ? extends BElement> mappingFunction) {
        throw UNSUPPORTED.get();
    }

    @Override
    default BElement computeIfPresent(String key,
            BiFunction<? super String, ? super BElement, ? extends BElement> remappingFunction) {
        throw UNSUPPORTED.get();
    }

    @Override
    default BElement compute(String key,
            BiFunction<? super String, ? super BElement, ? extends BElement> remappingFunction) {
        throw UNSUPPORTED.get();
    }

    @Override
    default BElement merge(String key, BElement value,
            BiFunction<? super BElement, ? super BElement, ? extends BElement> remappingFunction) {
        throw UNSUPPORTED.get();
    }
}
