package io.gridgo.bean;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import io.gridgo.utils.PrimitiveUtils;

public interface WrappedBObject extends BObject {

    Map<?, ?> getSource();

    @Override
    default int size() {
        return getSource().size();
    }

    @Override
    default boolean isEmpty() {
        return getSource().isEmpty();
    }

    @Override
    default boolean containsKey(Object key) {
        return getSource().containsKey(key);
    }

    @Override
    default boolean containsValue(Object value) {
        return getSource().containsValue(value);
    }

    @Override
    default BElement get(Object key) {
        return BElement.wrapAny(this.getSource().get(key));
    }

    @Override
    default Set<String> keySet() {
        return getSource().keySet() //
                          .stream() //
                          .map(Object::toString) //
                          .collect(Collectors.toSet());
    }

    @Override
    default Collection<BElement> values() {
        return getSource().values() //
                          .stream() //
                          .map(obj -> (BElement) BElement.ofAny(obj)) //
                          .collect(Collectors.toList());
    }

    @Override
    default Set<Entry<String, BElement>> entrySet() {
        return getSource().entrySet() //
                          .stream() //
                          .collect(Collectors.toMap(entry -> PrimitiveUtils.getStringValueFrom(entry.getKey()),
                                  entry -> (BElement) BElement.ofAny(entry.getValue()))) //
                          .entrySet();
    }

    @Override
    default BElement getOrDefault(Object key, BElement defaultValue) {
        BElement v;
        return (((v = get(key)) != null) || containsKey(key)) ? v : defaultValue;
    }

    @Override
    default void forEach(BiConsumer<? super String, ? super BElement> action) {
        this.getSource().entrySet().forEach(entry -> {
            action.accept(entry.getKey().toString(), BElement.wrapAny(entry.getValue()));
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    default Map<String, Object> toMap() {
        return (Map<String, Object>) this.getSource();
    }
}
