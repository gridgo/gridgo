package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface CharPrimitiveGetter extends PojoPrimitiveGetter {

    char getCharValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BYTE;
    }

    @Override
    default Object get(Object target) {
        return getCharValue(target);
    }
}
