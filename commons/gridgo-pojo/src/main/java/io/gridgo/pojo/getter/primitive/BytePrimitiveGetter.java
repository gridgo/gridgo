package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PrimitiveType;

public interface BytePrimitiveGetter extends PojoPrimitiveGetter {

    byte getByteValue(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BYTE;
    }

    @Override
    default Object get(Object target) {
        return getByteValue(target);
    }
}
