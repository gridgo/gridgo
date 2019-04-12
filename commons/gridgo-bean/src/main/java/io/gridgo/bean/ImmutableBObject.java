package io.gridgo.bean;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ImmutableBObject extends BObject {

    static final UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException("Instance of ImmutableBObject cannot be modified");

    @Override
    default BElement put(String key, BElement value) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement remove(Object key) {
        throw UNSUPPORTED;
    }

    @Override
    default void putAll(Map<? extends String, ? extends BElement> m) {
        throw UNSUPPORTED;
    }

    @Override
    default void replaceAll(BiFunction<? super String, ? super BElement, ? extends BElement> function) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement putIfAbsent(String key, BElement value) {
        throw UNSUPPORTED;
    }

    @Override
    default boolean remove(Object key, Object value) {
        throw UNSUPPORTED;
    }

    @Override
    default boolean replace(String key, BElement oldValue, BElement newValue) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement replace(String key, BElement value) {
        throw UNSUPPORTED;
    }

    @Override
    default void clear() {
        throw UNSUPPORTED;
    }

    @Override
    default BElement computeIfAbsent(String key, Function<? super String, ? extends BElement> mappingFunction) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement computeIfPresent(String key, BiFunction<? super String, ? super BElement, ? extends BElement> remappingFunction) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement compute(String key, BiFunction<? super String, ? super BElement, ? extends BElement> remappingFunction) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement merge(String key, BElement value, BiFunction<? super BElement, ? super BElement, ? extends BElement> remappingFunction) {
        throw UNSUPPORTED;
    }
}
