package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface LongArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    long[] getLongArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.LONG;
    }

    @Override
    default Object get(Object target) {
        return getLongArray(target);
    }
}
