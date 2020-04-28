package io.gridgo.pojo.getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.gridgo.utils.pojo.exception.PojoException;
import lombok.NonNull;

public class ReflectiveMethodGetter implements PojoGetter {

    private final @NonNull Method method;

    ReflectiveMethodGetter(Method method) {
        this.method = method;
    }

    @Override
    public Object get(Object target) {
        try {
            return method.invoke(target);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new PojoException("Cannot invoke method " + method.getName() + " on type "
                    + (target == null ? "null" : target.getClass().getName()));
        }
    }
}
