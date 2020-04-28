package io.gridgo.pojo.setter.primitive;

import io.gridgo.pojo.field.PojoPrimitive;
import io.gridgo.pojo.setter.PojoSetter;

public interface PojoPrimitiveSetter extends PojoSetter, PojoPrimitive {

    @Override
    default boolean isPrimitive() {
        return true;
    }
}
