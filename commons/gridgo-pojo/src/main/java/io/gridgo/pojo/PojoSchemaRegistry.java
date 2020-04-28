package io.gridgo.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.gridgo.utils.pojo.exception.PojoException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface PojoSchemaRegistry {

    PojoSchema lookup(Class<?> type);

    default PojoSchema lookupMandatory(Class<?> type) {
        var result = lookup(type);
        if (result != null)
            return result;
        throw new PojoException("Schema not found for type: " + type);
    }

    void register(PojoSchema schema);

    default PojoSchema registerIfAbsent(PojoSchema schema) {
        synchronized (this) {
            var old = lookup(schema.type());
            if (old == null)
                register(schema);
            return old;
        }
    }

    void unregister(Class<?> type);
}

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class AutoBuildingPojoSchemaRegistry implements PojoSchemaRegistry {

    private final @NonNull PojoSchemaBuilder schemaBuilder;

    private final Map<String, PojoSchema> cache = new ConcurrentHashMap<>();

    @Override
    public PojoSchema lookup(@NonNull Class<?> type) {
        var key = type.getName();

        var schema = cache.get(key);
        if (schema != null)
            return schema;

        synchronized (this) {
            return cache.containsKey(type.getName()) ? cache.get(type.getName()) : buildMandatory(type);
        }
    }

    private PojoSchema buildMandatory(Class<?> type) {
        return schemaBuilder.build(type);
    }

    @Override
    public void register(PojoSchema schema) {
        this.cache.put(schema.type().getName(), schema);
    }

    @Override
    public void unregister(Class<?> type) {
        if (type == null)
            return;
        this.cache.remove(type.getName());
    }
}