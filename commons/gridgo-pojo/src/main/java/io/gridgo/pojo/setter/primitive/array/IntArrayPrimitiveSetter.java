package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface IntArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setIntArray(Object target, int[] value);

    @Override
    default void set(Object target, Object value) {
        setIntArray(target, (int[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.INT;
    }
}
