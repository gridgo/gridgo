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

    @Override
    public String toString() {
        return String.format("{SETTER: {fieldName: %s, refField: %s, element: %s}}", //
                fieldName(), //
                refField() == null ? "null" : refField().getName(), //
                element());
    }
}