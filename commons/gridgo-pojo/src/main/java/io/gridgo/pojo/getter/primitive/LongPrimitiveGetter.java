package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface LongPrimitiveGetter extends PojoPrimitiveGetter {

    long getLongValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.LONG;
    }

    @Override
    default Object get(Object target) {
        return getLongValue(target);
    }
}
