package io.gridgo.pojo.builder;

import java.lang.reflect.InvocationTargetException;

import io.gridgo.pojo.PojoSchema;
import io.gridgo.pojo.PojoSchemaConfig;
import io.gridgo.pojo.input.PojoSchemaInput;
import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractPojoSchema<T> implements PojoSchema<T> {

    @Getter(value = AccessLevel.PROTECTED)
    private final @NonNull Class<T> type;

    protected void init() {
        // do nothing
    }

    @Override
    public void serialize(T target, PojoSchemaOutput output) {
        throw new UnsupportedOperationException("Serializing is not supported for target type: " + type);
    }

    @Override
    public void deserialize(T target, PojoSchemaInput input) {
        throw new UnsupportedOperationException("Deserializing is not supported for target type: " + type);
    }

    @Override
    public T newInstance() {
        try {
            return this.type.getConstructor().newInstance();
        } catch (InstantiationException //
                | IllegalAccessException //
                | IllegalArgumentException //
                | InvocationTargetException //
                | NoSuchMethodException //
                | SecurityException e) {
            throw new PojoException("cannot create new instance of type: " + type, e);
        }
    }

    protected static <T> PojoSchema<T> createSchema(Class<T> type, PojoSchemaConfig config) {
        return PojoSchema.of(type, config);
    }
}
