package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.field.PrimitiveType;

public interface CharArrayPrimitiveGetter extends ArrayPrimitiveGetter {

    char[] getCharArray(Object target);

    @Override
    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.CHAR;
    }

    @Override
    default Object get(Object target) {
        return getCharArray(target);
    }
}
