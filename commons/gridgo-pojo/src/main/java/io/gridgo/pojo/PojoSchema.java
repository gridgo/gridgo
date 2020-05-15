package io.gridgo.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.gridgo.pojo.builder.PojoSchemaBuilder;
import io.gridgo.pojo.input.PojoSchemaInput;
import io.gridgo.pojo.output.PojoSchemaOutput;
import lombok.AllArgsConstructor;
import lombok.NonNull;

public interface PojoSchema<T> {

    public static <T> PojoSchema<T> of(Class<T> type) {
        return of(type, PojoSchemaConfig.DEFAULT);
    }

    public static <T> PojoSchema<T> of(Class<T> type, PojoSchemaConfig config) {
        return PojoSchemaCache.lookup(type, config);
    }

    T newInstance();

    void serialize(T target, PojoSchemaOutput output);

    void deserialize(T target, PojoSchemaInput input);

    default T deserialize(PojoSchemaInput input) {
        var t = newInstance();
        deserialize(t, input);
        return t;
    }
}

class PojoSchemaCache {

    @AllArgsConstructor(staticName = "of")
    private static class Key {
        private @NonNull Class<?> type;
        private @NonNull PojoSchemaConfig config;
    }

    private static final Map<Key, PojoSchema<?>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static final <T> PojoSchema<T> lookup(@NonNull Class<T> type, @NonNull PojoSchemaConfig config) {
        return (PojoSchema<T>) CACHE.computeIfAbsent(Key.of(type, config),
                k -> new PojoSchemaBuilder(k.type, k.config).build());
    }

}