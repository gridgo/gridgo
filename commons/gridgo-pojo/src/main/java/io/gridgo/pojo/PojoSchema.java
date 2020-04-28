package io.gridgo.pojo;

import io.gridgo.pojo.field.PojoField;
import io.gridgo.utils.pojo.exception.PojoException;

public interface PojoSchema {

    Class<?> type();

    PojoField[] fields();

    default Object newInstance() {
        var cls = type();
        try {
            return cls.getConstructor().newInstance();
        } catch (Exception e) {
            throw new PojoException("Cannot construct new instance for type " + cls.getName(), e);
        }
    }
}
