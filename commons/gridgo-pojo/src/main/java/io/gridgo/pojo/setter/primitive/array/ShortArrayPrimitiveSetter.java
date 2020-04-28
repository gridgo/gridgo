package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface ShortArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setShortArray(Object target, short[] value);

    @Override
    default void set(Object target, Object value) {
        setShortArray(target, (short[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.SHORT;
    }
}
