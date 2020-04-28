package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface BooleanPrimitiveGetter extends PojoPrimitiveGetter {

    boolean getBooleanValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BOOLEAN;
    }

    @Override
    default Object get(Object target) {
        return getBooleanValue(target);
    }
}
