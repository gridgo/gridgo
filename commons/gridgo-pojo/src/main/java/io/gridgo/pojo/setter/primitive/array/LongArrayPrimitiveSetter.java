package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface LongArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setLongArray(Object target, long[] value);

    @Override
    default void set(Object target, Object value) {
        setLongArray(target, (long[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.LONG;
    }
}
