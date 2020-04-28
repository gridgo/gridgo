package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface FloatArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    float[] getFloatArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.FLOAT;
    }

    @Override
    default Object get(Object target) {
        return getFloatArray(target);
    }
}
