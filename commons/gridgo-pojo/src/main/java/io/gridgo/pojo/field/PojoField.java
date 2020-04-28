package io.gridgo.pojo.field;

import io.gridgo.pojo.getter.PojoGetter;
import io.gridgo.pojo.setter.PojoSetter;

public interface PojoField {

    String name();

    PojoSetter setter();

    PojoGetter getter();
}
