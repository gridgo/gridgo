package io.gridgo.pojo.setter.primitive.array;

import io.gridgo.pojo.setter.primitive.PojoPrimitiveSetter;

public interface ArrayPrimitiveSetter extends PojoPrimitiveSetter {

    @Override
    default boolean isArray() {
        return true;
    }
}
