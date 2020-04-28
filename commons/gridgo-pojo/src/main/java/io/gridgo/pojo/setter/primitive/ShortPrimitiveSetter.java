package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.utils.PrimitiveUtils;

public interface ShortPrimitiveSetter extends PojoPrimitiveSetter {

    void setShortValue(Object target, short value);

    @Override
    default void set(Object target, Object value) {
        setShortValue(target, PrimitiveUtils.getShortValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.SHORT;
    }
}
