package io.gridgo.pojo.getter.primitive.array;

import io.gridgo.pojo.getter.primitive.PojoPrimitiveGetter;

public interface ArrayPrimitiveGetter extends PojoPrimitiveGetter {

    @Override
    default boolean isArray() {
        return true;
    }
}
