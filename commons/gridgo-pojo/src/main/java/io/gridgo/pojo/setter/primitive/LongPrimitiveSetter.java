package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.utils.PrimitiveUtils;

public interface LongPrimitiveSetter extends PojoPrimitiveSetter {

    void setLongValue(Object target, long value);

    @Override
    default void set(Object target, Object value) {
        setLongValue(target, PrimitiveUtils.getLongValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.LONG;
    }
}
