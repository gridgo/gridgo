package io.gridgo.pojo.getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.gridgo.pojo.getter.primitive.PrimitiveGetterCompiler;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.NonNull;

public interface PojoGetter {

    Object get(Object target);

    default boolean isPrimitive() {
        return false;
    }

    public static PojoGetter forField(Field field) {
        return new ReflectiveFieldGetter(field);
    }

    public static PojoGetter forField(@NonNull Class<?> type, @NonNull String fieldName) {
        try {
            var field = type.getDeclaredField(fieldName);
            return new ReflectiveFieldGetter(field);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new PojoException("Cannot get field for name '" + fieldName + "' from type " + type.getName());
        }
    }

    public static PojoGetter compile(Method method) {
        var type = method.getReturnType();
        var compiler = type.isPrimitive() //
                ? PrimitiveGetterCompiler.getInstance() //
                : DefaultGetterCompiler.getInstance();
        return compiler.compile(method);
    }

    public static PojoGetter compile(Field field) {
        var type = field.getType();
        var compiler = type.isPrimitive() //
                ? PrimitiveGetterCompiler.getInstance() //
                : DefaultGetterCompiler.getInstance();
        return compiler.compile(field);
    }
}
