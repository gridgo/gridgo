package io.gridgo.utils.pojo;

import static io.gridgo.utils.StringUtils.upperCaseFirstLetter;
import static io.gridgo.utils.format.StringFormatter.transform;
import static io.gridgo.utils.pojo.PojoUtils.isSupported;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.pojo.exception.InvalidFieldNameException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMethodSignatureExtractor implements MethodSignatureExtractor {

    @Override
    public List<PojoFieldSignature> extractMethodSignatures(Class<?> targetType) {
        if (!isSupported(targetType)) {
            if (log.isWarnEnabled())
                log.warn("Cannot extract method signature from {}", targetType.getName());
            return Collections.emptyList();
        }

        var results = new LinkedList<PojoFieldSignature>();
        String transformRule = null;
        Set<String> ignoredFields = null;

        if (targetType.isAnnotationPresent(FieldNameTransform.class)) {
            var annotation = targetType.getAnnotation(FieldNameTransform.class);
            transformRule = annotation.value();
            ignoredFields = Stream.of(annotation.ignore()).collect(Collectors.toSet());
        }

        var methods = extractAllMethods(targetType);
        for (var method : methods) {
            var methodName = method.getName();

            if (!isApplicable(method))
                continue;

            var fieldName = extractFieldName(methodName);
            if (isTransient(method, fieldName))
                continue;

            results.add(extract(method, fieldName, transformRule, ignoredFields));
        }

        return results;
    }

    private Collection<Method> extractAllMethods(@NonNull Class<?> targetType) {
        var nameToMethod = new HashMap<String, Method>();
        var t = targetType;
        do {
            var methods = t.getDeclaredMethods();
            for (var method : methods) {
                if (!nameToMethod.containsKey(method.getName())) {
                    nameToMethod.put(method.getName(), method);
                }
            }
            t = t.getSuperclass();
        } while (t != null && t != Object.class);
        return nameToMethod.values();
    }

    protected String transformFieldName(Method method, String fieldName, String transformRule,
            Set<String> ignoredFields, Class<?> signatureType) {

        var targetType = method.getDeclaringClass();
        var transformedFieldName = findTransformedFieldName(method, fieldName);
        if (transformedFieldName != null || transformRule == null)
            return transformedFieldName;

        var ignored = ignoredFields != null && ignoredFields.contains(fieldName);
        if (ignored)
            return transformedFieldName;

        var map = new HashMap<>();
        map.put("fieldName", fieldName);
        map.put("methodName", method.getName());
        map.put("fieldType", signatureType.getName());
        map.put("packageName", targetType.getPackageName());
        map.put("typeName", targetType.getName());

        return transform(transformRule, map);
    }

    private Field getDeclaredField(Class<?> type, String... fieldNames) {
        for (var fieldName : fieldNames) { // trying one by one name
            try {
                return type.getDeclaredField(fieldName); // return if existing
            } catch (Exception e) {
                // do nothing, just go to next name
            }
        }
        return null;
    }

    protected Field getCorespondingField(Method method, String interpretedFieldName) {
        var returnType = method.getReturnType();
        if (returnType == void.class) // incase the method were setter
            returnType = method.getParameters()[0].getType();

        String booleanFieldName = null;
        if (returnType == Boolean.class || returnType == Boolean.TYPE || returnType == boolean.class)
            booleanFieldName = "is" + upperCaseFirstLetter(interpretedFieldName);

        return getDeclaredField(method.getDeclaringClass(), interpretedFieldName, booleanFieldName);
    }

    private String findTransformedFieldName(Method method, String fieldName) {
        FieldName annotation = method.getAnnotation(FieldName.class);
        if (annotation == null) {
            var field = getCorespondingField(method, fieldName);
            if (field != null && field.isAnnotationPresent(FieldName.class))
                annotation = field.getAnnotation(FieldName.class);
        }

        String transformedFieldName = null;
        if (annotation != null) {
            transformedFieldName = annotation.value();
            if (transformedFieldName.isBlank())
                throw new InvalidFieldNameException("invalid field name: " + transformedFieldName
                        + " in method or field " + fieldName + ", type: " + method.getDeclaringClass().getName());
        }

        return transformedFieldName;
    }

    private boolean isTransient(Method method, String fieldName) {
        if (method.isAnnotationPresent(Transient.class))
            return true;

        var field = getCorespondingField(method, fieldName);
        if (field != null && field.isAnnotationPresent(Transient.class))
            return true;

        return false;
    }

    protected abstract boolean isApplicable(Method method);

    protected abstract String extractFieldName(String methodName);

    protected abstract PojoFieldSignature extract(Method method, String fieldName, String transformRule,
            Set<String> ignoredFields);
}
