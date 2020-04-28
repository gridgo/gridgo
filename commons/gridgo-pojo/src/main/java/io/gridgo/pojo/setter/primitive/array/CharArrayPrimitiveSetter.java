package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface CharArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setCharArray(Object target, char[] value);

    @Override
    default void set(Object target, Object value) {
        setCharArray(target, (char[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.CHAR;
    }
}
