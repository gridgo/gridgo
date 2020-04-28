package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface ShortPrimitiveGetter extends PojoPrimitiveGetter {

    short getShortValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.SHORT;
    }

    @Override
    default Object get(Object target) {
        return getShortValue(target);
    }

}
