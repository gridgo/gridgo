package io.gridgo.pojo.reflect;

import java.lang.reflect.Type;

import io.gridgo.pojo.reflect.type.PojoType;
import io.gridgo.pojo.reflect.type.PojoTypeResolver;
import io.gridgo.pojo.support.PojoAccessorType;

public class PojoReflectiveGetter extends AbstractPojoReflectiveAccessor {

    public PojoReflectiveGetter(String name, PojoReflectiveElement element) {
        super(PojoAccessorType.GET, name, element);
    }

    @Override
    public PojoType fieldType() {
        var element = element();
        var effectiveClass = element.effectiveClass();
        if (element.isField())
            return PojoTypeResolver.extractFieldTypeInfo(element.field(), effectiveClass);
        return PojoTypeResolver.extractReturnTypeInfo(element.method(), effectiveClass);
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