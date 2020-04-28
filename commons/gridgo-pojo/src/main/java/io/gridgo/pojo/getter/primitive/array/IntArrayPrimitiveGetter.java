package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface IntArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    int[] getIntArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.INT;
    }

    @Override
    default Object get(Object target) {
        return getIntArray(target);
    }
}
