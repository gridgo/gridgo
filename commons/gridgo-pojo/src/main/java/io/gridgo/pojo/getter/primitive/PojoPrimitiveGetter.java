package io.gridgo.pojo.getter.primitive;

import io.gridgo.pojo.field.PojoPrimitive;
import io.gridgo.pojo.getter.PojoGetter;

public interface PojoPrimitiveGetter extends PojoGetter, PojoPrimitive {

    @Override
    default boolean isPrimitive() {
        return true;
    }
}
