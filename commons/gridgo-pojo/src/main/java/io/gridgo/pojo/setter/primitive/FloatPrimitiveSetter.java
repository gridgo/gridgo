package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.utils.PrimitiveUtils;

public interface FloatPrimitiveSetter extends PojoPrimitiveSetter {

    void setFloatValue(Object target, float value);

    @Override
    default void set(Object target, Object value) {
        setFloatValue(target, PrimitiveUtils.getFloatValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.FLOAT;
    }
}
