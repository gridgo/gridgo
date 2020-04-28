package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface BooleanArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setBooleanArray(Object target, boolean[] value);

    @Override
    default void set(Object target, Object value) {
        setBooleanArray(target, (boolean[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BOOLEAN;
    }
}
