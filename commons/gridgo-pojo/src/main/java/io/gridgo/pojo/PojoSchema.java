package io.gridgo.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.gridgo.pojo.builder.PojoSchemaBuilder;
import io.gridgo.pojo.input.PojoSchemaInput;
import io.gridgo.pojo.output.PojoSchemaOutput;

public interface PojoSchema<T> {

    public static PojoSchema<Object> of(Class<?> type) {
        return PojoSchemaCache.lookup(type);
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

    private static final Map<Class<?>, PojoSchema<?>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static final PojoSchema<Object> lookup(Class<?> type) {
        return (PojoSchema<Object>) CACHE.computeIfAbsent(type,
                t -> new PojoSchemaBuilder(t, PojoSchemaConfig.DEFAULT).build());
    }

}