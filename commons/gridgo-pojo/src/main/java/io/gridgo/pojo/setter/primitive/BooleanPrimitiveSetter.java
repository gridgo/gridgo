package io.gridgo.pojo.setter.primitive;

import static io.gridgo.utils.PrimitiveUtils.getBooleanValueFrom;

import io.gridgo.pojo.field.PrimitiveType;

public interface BooleanPrimitiveSetter extends PojoPrimitiveSetter {

    void setBooleanValue(Object target, boolean value);

    @Override
    default void set(Object target, Object value) {
        setBooleanValue(target, getBooleanValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BOOLEAN;
    }
}
