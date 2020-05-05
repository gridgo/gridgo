package io.gridgo.pojo.translator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.gridgo.utils.ClasspathUtils;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public class PojoUnboxerRegistry {

    @Getter
    @Accessors(fluent = true)
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    private static class SimplePojoUnboxer implements PojoUnboxer {
        private final @NonNull Class<?> targetType;
        private final @NonNull Class<?> returnType;
        private final @NonNull String methodName;
        private final @NonNull Class<?> declaringClass;
    }

    @Getter
    private final static PojoUnboxerRegistry instance = new PojoUnboxerRegistry();

    private final Map<Class<?>, PojoUnboxer> cache = Collections.unmodifiableMap(scan());

    private final Map<Class<?>, PojoUnboxer> scan() {
        var map = new HashMap<Class<?>, PojoUnboxer>();
        ClasspathUtils.scanForAnnotatedMethods("", Unboxer.class, (method, annotation) -> {
            var unboxer = createUnboxer(method);
            map.put(unboxer.targetType(), unboxer);
        });
        return map;
    }

    public PojoUnboxer lookup(@NonNull Class<?> type) {
        return cache.get(type);
    }

    public PojoUnboxer lookupMandatory(Class<?> type) {
        var result = lookup(type);
        if (result != null)
            return result;
        throw new PojoException("Unboxer cannot be found for type: " + type);
    }

    private PojoUnboxer createUnboxer(Class<?> targetType, Class<?> returnType, String methodName,
            Class<?> declaringClass) {
        return new SimplePojoUnboxer(targetType, returnType, methodName, declaringClass);
    }

    private PojoUnboxer createUnboxer(Method method) {
        if (!Modifier.isStatic(method.getModifiers()))
            throw new IllegalArgumentException("Unboxer method must be static: " + method);
        if (!Modifier.isPublic(method.getModifiers()))
            throw new IllegalArgumentException("Unboxer method must be public: " + method);
        return createUnboxer(method.getParameters()[0].getType(), method.getReturnType(), method.getName(),
                method.getDeclaringClass());
    }
}
