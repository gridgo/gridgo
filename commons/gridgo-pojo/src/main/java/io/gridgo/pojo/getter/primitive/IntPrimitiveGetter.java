package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface IntPrimitiveGetter extends PojoPrimitiveGetter {

    int getIntValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.INT;
    }

    @Override
    default Object get(Object target) {
        return getIntValue(target);
    }
}
