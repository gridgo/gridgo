package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface DoublePrimitiveGetter extends PojoPrimitiveGetter {

    double getDoubleValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.DOUBLE;
    }

    @Override
    default Object get(Object target) {
        return getDoubleValue(target);
    }
}
