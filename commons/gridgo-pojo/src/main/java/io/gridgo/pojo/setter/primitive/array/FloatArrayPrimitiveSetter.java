package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface FloatArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setFloatArray(Object target, float[] value);

    @Override
    default void set(Object target, Object value) {
        setFloatArray(target, (float[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.FLOAT;
    }
}
