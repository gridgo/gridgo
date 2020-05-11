package io.gridgo.pojo.reflect;

import io.gridgo.pojo.reflect.type.PojoType;
import io.gridgo.pojo.reflect.type.PojoTypes;
import io.gridgo.pojo.support.PojoAccessorType;

public class PojoReflectiveSetter extends AbstractPojoReflectiveAccessor {

    public PojoReflectiveSetter(String name, PojoReflectiveElement element) {
        super(PojoAccessorType.SET, name, element);
    }

    @Override
    public PojoType fieldType() {
        var element = element();
        var effectiveClass = element.effectiveClass();
        if (element.isField())
            return PojoTypes.extractFieldTypeInfo(element.field(), effectiveClass);
        return PojoTypes.extractFirstParamTypeInfo(element.method(), effectiveClass);
    }

    @Override
    public String toString() {
        return String.format("{SETTER: {fieldName: %s, refField: %s, element: %s}}", //
                fieldName(), //
                refField() == null ? "null" : refField().getName(), //
                element());
    }
}