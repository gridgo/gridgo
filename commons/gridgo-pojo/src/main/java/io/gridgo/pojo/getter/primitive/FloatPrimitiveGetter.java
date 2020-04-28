package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface FloatPrimitiveGetter extends PojoPrimitiveGetter {

    float getFloatValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.FLOAT;
    }

    @Override
    default Object get(Object target) {
        return getFloatValue(target);
    }
}
