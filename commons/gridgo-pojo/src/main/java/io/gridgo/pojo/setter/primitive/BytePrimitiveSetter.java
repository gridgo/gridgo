package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.utils.PrimitiveUtils;

public interface BytePrimitiveSetter extends PojoPrimitiveSetter {

    void setByteValue(Object target, byte value);

    @Override
    default void set(Object target, Object value) {
        setByteValue(target, PrimitiveUtils.getByteValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BYTE;
    }
}
