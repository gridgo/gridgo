package io.gridgo.pojo.ctor;

import java.lang.reflect.Constructor;

import io.gridgo.utils.pojo.exception.PojoException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE, staticName = "ofMethod")
class NoArgsReflectionPojoCtor implements PojoConstructor {

    static NoArgsReflectionPojoCtor ofType(Class<?> type) {
        try {
            return ofMethod(type.getConstructor());
        } catch (Exception e) {
            throw new PojoException("Cannot get no-args constructor of type: " + type.getName());
        }
    }

    private final @NonNull Constructor<?> noArgsCtor;

    @Override
    public Object newInstance() {
        try {
            return noArgsCtor.newInstance();
        } catch (Exception e) {
            throw new PojoException("Cannot init new instance of type " + noArgsCtor.getDeclaringClass(), e);
        }
    }

}
