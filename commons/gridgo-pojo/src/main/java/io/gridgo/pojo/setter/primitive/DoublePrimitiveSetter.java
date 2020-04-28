package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.utils.PrimitiveUtils;

public interface DoublePrimitiveSetter extends PojoPrimitiveSetter {

    void setDoubleValue(Object target, double value);

    @Override
    default void set(Object target, Object value) {
        setDoubleValue(target, PrimitiveUtils.getDoubleValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.DOUBLE;
    }
}
