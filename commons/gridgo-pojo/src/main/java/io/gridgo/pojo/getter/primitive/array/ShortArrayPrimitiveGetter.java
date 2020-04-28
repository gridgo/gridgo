package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface ShortArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    short[] getShortArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.SHORT;
    }

    @Override
    default Object get(Object target) {
        return getShortArray(target);
    }
}
