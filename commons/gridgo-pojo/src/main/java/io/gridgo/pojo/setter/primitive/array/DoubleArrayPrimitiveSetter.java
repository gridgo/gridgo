package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface DoubleArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setDoubleArray(Object target, double[] value);

    @Override
    default void set(Object target, Object value) {
        setDoubleArray(target, (double[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.DOUBLE;
    }
}
