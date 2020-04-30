package io.gridgo.pojo.reflect;

import io.gridgo.pojo.support.PojoAccessorType;

public class PojoReflectiveSetter extends AbstractPojoReflectiveAccessor {

    public PojoReflectiveSetter(String name, PojoReflectiveElement element) {
        super(PojoAccessorType.SET, name, element);
    }
}