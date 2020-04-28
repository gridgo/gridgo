package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.utils.PrimitiveUtils;

public interface IntPrimitiveSetter extends PojoPrimitiveSetter {

    void setIntValue(Object target, int value);

    @Override
    default void set(Object target, Object value) {
        setIntValue(target, PrimitiveUtils.getIntegerValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.INT;
    }
}
