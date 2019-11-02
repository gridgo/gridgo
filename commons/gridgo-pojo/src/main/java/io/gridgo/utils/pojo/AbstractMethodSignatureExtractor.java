package io.gridgo.utils.pojo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.gridgo.utils.format.StringFormatter.transform;
import static io.gridgo.utils.pojo.PojoUtils.isSupported;

import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.pojo.exception.InvalidFieldNameException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMethodSignatureExtractor implements MethodSignatureExtractor {

    @Override
    public List<PojoMethodSignature> extractMethodSignatures(Class<?> targetType) {
        if (!isSupported(targetType)) {
            if (log.isWarnEnabled())
                log.warn("Cannot extract method signature from {}", targetType.getName());
            return Collections.emptyList();
        }
        var results = new LinkedList<PojoMethodSignature>();
        String transformRule = null;
        Set<String> ignoredFields = null;
        if (targetType.isAnnotationPresent(FieldNameTransform.class)) {
            var annotation = targetType.getAnnotation(FieldNameTransform.class);
            transformRule = annotation.value();
            ignoredFields = new HashSet<String>(Arrays.asList(annotation.ignore()));
        }
        var methods = extractAllMethods(targetType);
        for (Method method : methods) {
            String methodName = method.getName();

            if (isApplicable(method, methodName)) {
                var fieldName = extractFieldName(methodName);

                if (!isTransient(method, fieldName)) {
                    var signature = extract(targetType, method, fieldName, transformRule, ignoredFields);
                    results.add(signature);
                }
            }
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

    protected String transformFieldName(Class<?> targetType, Method method, String fieldName, String transformRule, Set<String> ignoredFields,
            Class<?> signatureType) {
        String transformedFieldName = findTransformedFieldName(targetType, method, fieldName);
        if (transformedFieldName == null && transformRule != null) {
            if (ignoredFields == null || ignoredFields.size() == 0 || !ignoredFields.contains(fieldName)) {
                Map<String, String> map = new HashMap<>();
                map.put("fieldName", fieldName);
                map.put("methodName", method.getName());
                map.put("fieldType", signatureType.getName());
                map.put("packageName", targetType.getPackageName());
                map.put("typeName", targetType.getName());
                transformedFieldName = transform(transformRule, map);
            }
        }
        return transformedFieldName;
    }

    private String findTransformedFieldName(Class<?> targetType, Method method, String fieldName) {
        FieldName annotation = null;
        if (method.isAnnotationPresent(FieldName.class)) {
            annotation = method.getAnnotation(FieldName.class);
        } else {
            try {
                var field = targetType.getDeclaredField(fieldName);
                if (field.isAnnotationPresent(FieldName.class)) {
                    annotation = field.getAnnotation(FieldName.class);
                }
            } catch (Exception e) {
                log.warn("Cannot get declared field", e);
            }
        }

        String transformedFieldName = null;
        if (annotation != null) {
            transformedFieldName = annotation.value();
            if (transformedFieldName.isBlank()) {
                throw new InvalidFieldNameException("invalid field name: " + transformedFieldName + " in method or field " + fieldName
                        + ", type: " + targetType.getName());
            }
        }

        return transformedFieldName;
    }

    private boolean isTransient(Method method, String fieldName) {
        if (method.isAnnotationPresent(Transient.class)) {
            return true;
        }

        try {
            var field = method.getDeclaringClass().getDeclaredField(fieldName);
            if (field.isAnnotationPresent(Transient.class))
                return true;
        } catch (Exception e) {
            // do nothing
        }

        return false;
    }

    protected abstract boolean isApplicable(Method method, String methodName);

    protected abstract String extractFieldName(String methodName);

    protected abstract PojoMethodSignature extract(Class<?> targetType, Method method, String fieldName, String transformRule,
            Set<String> ignoredFields);
}
