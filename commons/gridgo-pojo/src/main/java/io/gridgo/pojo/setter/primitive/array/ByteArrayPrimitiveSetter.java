package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface ByteArrayPrimitiveSetter extends ArrayPrimitiveSetter {

    void setByteArray(Object target, byte[] value);

    @Override
    default void set(Object target, Object value) {
        setByteArray(target, (byte[]) value);
    }

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BYTE;
    }
}
