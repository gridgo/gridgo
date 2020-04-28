package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface ByteArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    byte[] getByteArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.BYTE;
    }

    @Override
    default Object get(Object target) {
        return getByteArray(target);
    }
}
