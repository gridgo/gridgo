package io.gridgo.utils.pojo;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.exception.InvalidFieldNameException;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import lombok.NonNull;

public class PojoUtils {

    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.getInstance();
    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.getInstance();

    private static final String SETTER_PREFIX = "set";
    private final static Set<String> GETTER_PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));

    public static List<PojoMethodSignature> extractSetterMethodSignatures(Class<?> targetType) {
        var results = new LinkedList<PojoMethodSignature>();
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 1 && method.getReturnType() == Void.TYPE
                    && method.getName().startsWith(SETTER_PREFIX)) {

                String fieldName = lowerCaseFirstLetter(method.getName().substring(3));
                String transformedFieldName = findTransformedFieldName(targetType, method, fieldName);
                Parameter param = method.getParameters()[0];
                Class<?> paramType = param.getType();

                results.add(PojoMethodSignature.builder() //
                        .fieldName(fieldName) //
                        .transformedFieldName(transformedFieldName) //
                        .method(method) //
                        .fieldType(paramType) //
                        .build());
            }
        }
        return results;
    }

    private static boolean isTransient(Class<?> targetType, Method method, String fieldName) {
        Transient annotation = null;
        if (method.isAnnotationPresent(FieldName.class)) {
            annotation = method.getAnnotation(Transient.class);
        } else {
            try {
                var field = targetType.getDeclaredField(fieldName);
                if (field.isAnnotationPresent(Transient.class)) {
                    annotation = field.getAnnotation(Transient.class);
                }
            } catch (Exception e) {
                // do nothing
            }
        }

        return annotation != null;
    }

    private static String findTransformedFieldName(Class<?> targetType, Method method, String fieldName) {
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
                // do nothing
            }
        }

        String transformedFieldName = null;
        if (annotation != null) {
            transformedFieldName = annotation.value();
            if (transformedFieldName.isBlank()) {
                throw new InvalidFieldNameException("invalid field name: " + transformedFieldName
                        + " in method or field " + fieldName + ", type: " + targetType.getName());
            }
        }

        return transformedFieldName;
    }

    public static final List<PojoMethodSignature> extractGetterMethodSignatures(@NonNull Class<?> targetType) {
        var results = new LinkedList<PojoMethodSignature>();
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (method.getParameterCount() == 0 //
                    && method.getReturnType() != Void.TYPE //
                    && GETTER_PREFIXES.stream().anyMatch(prefix -> methodName.startsWith(prefix))) {

                String fieldName = lowerCaseFirstLetter(methodName.substring(methodName.startsWith("is") ? 2 : 3));
                if (!isTransient(targetType, method, fieldName)) {
                    String transformedFieldName = findTransformedFieldName(targetType, method, fieldName);
                    Class<?> fieldType = method.getReturnType();

                    results.add(PojoMethodSignature.builder() //
                            .fieldName(fieldName) //
                            .transformedFieldName(transformedFieldName) //
                            .method(method) //
                            .fieldType(fieldType) //
                            .build());
                }
            }
        }
        return results;
    }

    public static final Object getValue(@NonNull Object target, @NonNull String fieldName) {
        return GETTER_REGISTRY.getGetterProxy(target.getClass()).getValue(target, fieldName);
    }

    public static final void setValue(@NonNull Object target, @NonNull String fieldName, Object value) {
        SETTER_REGISTRY.getSetterProxy(target.getClass()).applyValue(target, fieldName, value);
    }
}
