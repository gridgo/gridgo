package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface BooleanArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    boolean[] getBooleanArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BOOLEAN;
    }

    @Override
    default Object get(Object target) {
        return getBooleanArray(target);
    }
}
