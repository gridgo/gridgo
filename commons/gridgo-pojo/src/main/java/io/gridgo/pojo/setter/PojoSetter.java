package io.gridgo.pojo.setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.gridgo.pojo.setter.primitive.PrimitiveSetterCompiler;

public interface PojoSetter {

    void set(Object target, Object value);

    default boolean isPrimitive() {
        return false;
    }

    static PojoSetter compile(Method method) {
        var type = method.getParameterTypes()[0];
        var compiler = type.isPrimitive() //
                ? PrimitiveSetterCompiler.getInstance() //
                : DefaultSetterCompiler.getInstance();
        return compiler.compile(method);
    }

    static PojoSetter compile(Field field) {
        var type = field.getType();
        var compiler = type.isPrimitive() //
                ? PrimitiveSetterCompiler.getInstance() //
                : DefaultSetterCompiler.getInstance();
        return compiler.compile(field);
    }

}
