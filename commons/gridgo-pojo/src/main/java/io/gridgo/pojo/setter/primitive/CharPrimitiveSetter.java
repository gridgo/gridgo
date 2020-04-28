package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PrimitiveType;
import io.gridgo.utils.PrimitiveUtils;

public interface CharPrimitiveSetter extends PojoPrimitiveSetter {

    void setCharValue(Object target, char value);

    @Override
    default void set(Object target, Object value) {
        setCharValue(target, PrimitiveUtils.getCharValueFrom(value));
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.CHAR;
    }
}
