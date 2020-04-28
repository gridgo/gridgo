package io.gridgo.pojo.getter;

import java.lang.reflect.Field;

import io.gridgo.utils.pojo.exception.PojoException;
import lombok.NonNull;

class ReflectiveFieldGetter implements PojoGetter {

    private final @NonNull Field field;

    ReflectiveFieldGetter(Field field) {
        this.field = field;
        if (!this.field.trySetAccessible())
            throw new IllegalArgumentException("Field cannot be accessed: " + this.field);
    }

    @Override
    public Object get(Object target) {
        try {
            return field.get(target);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new PojoException("Cannot get from field " + field, e);
        }
    }

}
