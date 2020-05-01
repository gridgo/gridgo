package io.gridgo.pojo;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import io.gridgo.pojo.annotation.FieldAccess;
import io.gridgo.pojo.annotation.FieldAccess.FieldAccessLevel;
import io.gridgo.pojo.annotation.FieldAccess.FieldAccessMode;
import io.gridgo.pojo.getter.PojoGetter;
import io.gridgo.pojo.setter.PojoSetter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectivePojoSchemaBuilder implements PojoSchemaBuilder {

    @Override
    public PojoSchema build(@NonNull Class<?> type) {

        var getters = new HashMap<String, PojoGetter>();
        var setters = new HashMap<String, List<PojoSetter>>();

        var methods = type.getMethods();
        for (var method : methods) {
            if (method.getDeclaringClass() == Object.class)
                continue;
            if (method.getParameterCount() == 0) {
                var pair = buildGetter(method);
                if (pair == null)
                    continue;
                getters.put(pair.getLeft(), pair.getRight());
            } else if (method.getParameterCount() == 1) {
                var pair = buildSetter(method);
                if (pair == null)
                    continue;
                setters.computeIfAbsent(pair.getLeft(), k -> new LinkedList<>()).add(pair.getRight());
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
                    var fieldName = field.getName();
                    if (fieldAccessMode.isGetable())
                        getters.putIfAbsent(fieldName, PojoGetter.compile(field));
                    if (fieldAccessMode.isSetable())
                        setters.computeIfAbsent(fieldName, k -> new LinkedList<>()).add(PojoSetter.compile(field));
                } else {
                    log.warn("cannot access field {}.{}", type.getName(), field.getName());
                }
                break;
            }
        }

        return null;
    }

    private Pair<String, PojoSetter> buildSetter(Method method) {
        String fieldName = null;

        var methodName = method.getName();
        var methodNameLength = methodName.length();
        if (methodName.startsWith("set") && methodNameLength > 3)
            fieldName = lowerCaseFirstLetter(methodName.substring(3));

        if (fieldName == null)
            return null;

        return Pair.of(fieldName, PojoSetter.compile(method));
    }

    private Pair<String, PojoGetter> buildGetter(Method method) {
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

        return Pair.of(fieldName, PojoGetter.compile(method));
    }

}
