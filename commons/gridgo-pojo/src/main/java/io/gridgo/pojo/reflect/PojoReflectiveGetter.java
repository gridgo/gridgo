package io.gridgo.pojo.reflect;

import java.lang.reflect.Type;

import io.gridgo.pojo.support.PojoAccessorType;

public class PojoReflectiveGetter extends AbstractPojoReflectiveAccessor {

    public PojoReflectiveGetter(String name, PojoReflectiveElement element) {
        super(PojoAccessorType.GET, name, element);
    }

    @Override
    public Class<?> fieldType() {
        var element = element();
        if (element.isField())
            return element.field().getType();
        return element.method().getReturnType();
    }

    public Type genericFieldType() {
        var element = element();
        if (element.isField())
            return element.field().getType();
        return element.method().getGenericReturnType();
    }

    @Override
    public String toString() {
        return String.format("{GETTER: {fieldName: %s, refField: %s, element: %s}}", //
                fieldName(), //
                refField() == null ? "null" : refField().getName(), //
                element());
    }
}