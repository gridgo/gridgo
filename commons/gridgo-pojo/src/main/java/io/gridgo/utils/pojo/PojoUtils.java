package io.gridgo.utils.pojo;

import static io.gridgo.format.StringFormatter.transform;
import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.exception.InvalidFieldNameException;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.getter.PojoGetterWalker;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import lombok.NonNull;

public class PojoUtils {

    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.DEFAULT;
    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.DEFAULT;

    private static final String SETTER_PREFIX = "set";
    private final static Set<String> GETTER_PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));

    public static boolean isSupported(@NonNull Class<?> targetType) {
        return !(Collection.class.isAssignableFrom(targetType) //
                || Map.class.isAssignableFrom(targetType) //
                || isPrimitive(targetType) //
                || targetType.isArray() //
                || targetType == Date.class //
                || targetType == java.sql.Date.class);
    }

    public static List<PojoMethodSignature> extractSetterMethodSignatures(Class<?> targetType) {
        if (!isSupported(targetType)) {
            throw new IllegalArgumentException("Cannot extract method signature from " + targetType.getName());
        }

        var results = new LinkedList<PojoMethodSignature>();

        String transformRule = null;
        Set<String> ignoredFields = null;
        if (targetType.isAnnotationPresent(FieldNameTransform.class)) {
            var annotation = targetType.getAnnotation(FieldNameTransform.class);
            transformRule = annotation.value();
            ignoredFields = new HashSet<String>(Arrays.asList(annotation.ignore()));
        }

        Collection<Method> methods = extractAllMethods(targetType);

        for (Method method : methods) {
            if (method.getParameterCount() == 1 && method.getReturnType() == Void.TYPE
                    && method.getName().startsWith(SETTER_PREFIX)) {

                String fieldName = lowerCaseFirstLetter(method.getName().substring(3));

                if (!isTransient(targetType, method, fieldName)) {
                    Parameter param = method.getParameters()[0];
                    Class<?> paramType = param.getType();

                    String transformedFieldName = findTransformedFieldName(targetType, method, fieldName);
                    if (transformedFieldName == null && transformRule != null) {
                        if (ignoredFields == null || ignoredFields.size() == 0 || !ignoredFields.contains(fieldName)) {
                            Map<String, String> map = new HashMap<>();
                            map.put("fieldName", fieldName);
                            map.put("methodName", method.getName());
                            map.put("fieldType", paramType.getName());
                            map.put("packageName", targetType.getPackageName());
                            map.put("typeName", targetType.getName());
                            transformedFieldName = transform(transformRule, map);
                        }
                    }

                    results.add(PojoMethodSignature.builder() //
                            .fieldName(fieldName) //
                            .transformedFieldName(transformedFieldName) //
                            .method(method) //
                            .fieldType(paramType) //
                            .build());
                }
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
        if (!isSupported(targetType)) {
            throw new IllegalArgumentException("Cannot extract method signature from " + targetType.getName());
        }

        var results = new LinkedList<PojoMethodSignature>();

        String transformRule = null;
        Set<String> ignoredFields = null;
        if (targetType.isAnnotationPresent(FieldNameTransform.class)) {
            var annotation = targetType.getAnnotation(FieldNameTransform.class);
            transformRule = annotation.value();
            ignoredFields = new HashSet<String>(Arrays.asList(annotation.ignore()));
        }

        Collection<Method> methods = extractAllMethods(targetType);

        for (Method method : methods) {
            String methodName = method.getName();

            if (method.getParameterCount() == 0 //
                    && method.getReturnType() != Void.TYPE //
                    && GETTER_PREFIXES.stream().anyMatch(prefix -> methodName.startsWith(prefix))) {

                String fieldName = lowerCaseFirstLetter(methodName.substring(methodName.startsWith("is") ? 2 : 3));
                if (!isTransient(targetType, method, fieldName)) {
                    Class<?> fieldType = method.getReturnType();

                    String transformedFieldName = findTransformedFieldName(targetType, method, fieldName);
                    if (transformedFieldName == null && transformRule != null) {
                        if (ignoredFields == null || ignoredFields.size() == 0 || !ignoredFields.contains(fieldName)) {
                            Map<String, String> map = new HashMap<>();
                            map.put("fieldName", fieldName);
                            map.put("methodName", method.getName());
                            map.put("fieldType", fieldType.getName());
                            map.put("packageName", targetType.getPackageName());
                            transformedFieldName = transform(transformRule, map);
                        }
                    }

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

    private static Collection<Method> extractAllMethods(Class<?> targetType) {
        Map<String, Method> nameToMethod = new HashMap<String, Method>();
        Class<?> t = targetType;
        do {
            Method[] methods = t.getDeclaredMethods();
            for (Method method : methods) {
                if (!nameToMethod.containsKey(method.getName())) {
                    nameToMethod.put(method.getName(), method);
                }
            }
            t = t.getSuperclass();
        } while (t != Object.class);
        return nameToMethod.values();
    }

    public static final Object getValue(@NonNull Object target, @NonNull String fieldName) {
        return GETTER_REGISTRY.getGetterProxy(target.getClass()).getValue(target, fieldName);
    }

    public static final void setValue(@NonNull Object target, @NonNull String fieldName, Object value) {
        SETTER_REGISTRY.getSetterProxy(target.getClass()).applyValue(target, fieldName, value);
    }

    public static final PojoGetterProxy getGetterProxy(Class<?> type) {
        return GETTER_REGISTRY.getGetterProxy(type);
    }

    public static final PojoSetterProxy getSetterProxy(Class<?> type) {
        return SETTER_REGISTRY.getSetterProxy(type);
    }

    public static final void walkThroughGetter(Object target, PojoGetterWalker walker) {
        walkThroughGetter(target, null, walker);
    }

    @SuppressWarnings({ "rawtypes" })
    public static final void walkThroughGetter(Object target, PojoGetterProxy proxy, PojoGetterWalker walker) {
        final Class<?> type;

        if (target == null //
                || isPrimitive(type = target.getClass()) //
                || type == Date.class //
                || type == java.sql.Date.class) {

            walker.accept(VALUE, target);
            return;
        }

        if (type.isArray()) {
            int length = ArrayUtils.length(target);
            walker.accept(START_ARRAY, length);
            foreachArray(target, ele -> {
                walkThroughGetter(ele, proxy, walker);
            });
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Collection.class.isInstance(target)) {
            int length = ((Collection) target).size();
            walker.accept(START_ARRAY, length);
            var it = ((Collection) target).iterator();
            while (it.hasNext()) {
                walkThroughGetter(it.next(), proxy, walker);
            }
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Map.class.isInstance(target)) {
            Map<?, ?> map = (Map<?, ?>) target;
            int size = map.size();
            walker.accept(START_MAP, size);
            var it = map.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var key = entry.getKey();
                var value = entry.getValue();
                walker.accept(KEY, key);
                walkThroughGetter(value, proxy, walker);
            }
            walker.accept(END_MAP, size);
            return;
        }

        var _proxy = proxy != null ? proxy : getGetterProxy(type);
        int length = _proxy.getFields().length;
        walker.accept(START_MAP, length);
        _proxy.walkThrough(target, (signature, value) -> {
            walker.accept(KEY, signature.getTransformedOrDefaultFieldName());
            walkThroughGetter(value, signature.getElementGetterProxy(), walker);
        });
        walker.accept(END_MAP, length);
    }
}
