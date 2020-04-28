package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface DoubleArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    double[] getDoubleArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.DOUBLE;
    }

    @Override
    default Object get(Object target) {
        return getDoubleArray(target);
    }
}
