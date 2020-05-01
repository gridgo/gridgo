package io.gridgo.pojo.reflect;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import io.gridgo.pojo.annotation.FieldAccess;
import io.gridgo.pojo.annotation.FieldAccess.FieldAccessLevel;
import io.gridgo.pojo.annotation.FieldAccess.FieldAccessMode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PojoReflectiveAccessors {

    static Pair<List<PojoReflectiveGetter>, List<PojoReflectiveSetter>> extract(Class<?> type) {

        var getters = new LinkedList<PojoReflectiveGetter>();
        var setters = new LinkedList<PojoReflectiveSetter>();

        var methods = type.getMethods();
        for (var method : methods) {
            if (method.getDeclaringClass() == Object.class)
                continue;
            if (method.getParameterCount() == 0) {
                var pair = getterOf(method, type);
                if (pair == null)
                    continue;
                getters.add(pair);
            } else if (method.getParameterCount() == 1) {
                var pair = setterOf(method, type);
                if (pair == null)
                    continue;
                setters.add(pair);
            }
        }

        var defaultFieldAccess = type.getAnnotation(FieldAccess.class);
        var defaultFieldAccessLevel = defaultFieldAccess == null ? FieldAccessLevel.NONE : defaultFieldAccess.value();
        var defaultFieldAccessMode = defaultFieldAccess == null ? FieldAccessMode.FULL : defaultFieldAccess.mode();

        var fields = type.getDeclaredFields();
        for (var field : fields) {
            var fieldAccessLevel = defaultFieldAccessLevel;
            var fieldAccessMode = defaultFieldAccessMode;
            if (field.isAnnotationPresent(FieldAccess.class)) {
                var annotation = field.getAnnotation(FieldAccess.class);
                fieldAccessLevel = annotation.value();
                fieldAccessMode = annotation.mode();
            }

            switch (fieldAccessLevel) {
            case NONE:
                continue;
            case PUBLIC:
                if (!Modifier.isPublic(field.getModifiers()))
                    continue;
            case ANY:
                if (field.trySetAccessible()) {
                    if (fieldAccessMode.isGetable())
                        getters.add(getterOf(field, type));
                    if (fieldAccessMode.isSetable())
                        setters.add(setterOf(field, type));
                } else {
                    log.warn("cannot access field {}.{}", type.getName(), field.getName());
                }
                break;
            }
        }

        return Pair.of(getters, setters);
    }

    private static PojoReflectiveGetter getterOf(Method method, Class<?> effectiveClass) {
        String fieldName = null;

        var methodName = method.getName();
        var methodNameLength = methodName.length();
        if (methodName.startsWith("get") && methodNameLength > 3) {
            fieldName = lowerCaseFirstLetter(methodName.substring(3));
        } else if (methodName.startsWith("is") && methodNameLength > 2) {
            var returnType = method.getReturnType();
            if ((returnType == boolean.class || returnType == Boolean.class))
                fieldName = lowerCaseFirstLetter(methodName.substring(2));
        }

        if (fieldName == null)
            return null;

        return new PojoReflectiveGetter(fieldName, PojoReflectiveElement.ofMethod(method, effectiveClass));
    }

    private static PojoReflectiveGetter getterOf(Field field, Class<?> effectiveClass) {
        return new PojoReflectiveGetter(field.getName(), PojoReflectiveElement.ofField(field, effectiveClass));
    }

    private static PojoReflectiveSetter setterOf(Method method, Class<?> effectiveClass) {
        String fieldName = null;

        var methodName = method.getName();
        var methodNameLength = methodName.length();
        if (methodName.startsWith("set") && methodNameLength > 3)
            fieldName = lowerCaseFirstLetter(methodName.substring(3));

        if (fieldName == null)
            return null;

        return new PojoReflectiveSetter(fieldName, PojoReflectiveElement.ofMethod(method, effectiveClass));
    }

    private static PojoReflectiveSetter setterOf(Field field, Class<?> effectiveClass) {
        return new PojoReflectiveSetter(field.getName(), PojoReflectiveElement.ofField(field, effectiveClass));
    }
}
