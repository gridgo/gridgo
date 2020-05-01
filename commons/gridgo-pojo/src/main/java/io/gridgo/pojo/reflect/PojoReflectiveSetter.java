package io.gridgo.pojo.reflect;

import io.gridgo.pojo.support.PojoAccessorType;

public class PojoReflectiveSetter extends AbstractPojoReflectiveAccessor {

    public PojoReflectiveSetter(String name, PojoReflectiveElement element) {
        super(PojoAccessorType.SET, name, element);
    }

    @Override
    public Class<?> fieldType() {
        var element = element();
        if (element.isField())
            return element.field().getType();
        return element.method().getParameters()[0].getType();
    }
}