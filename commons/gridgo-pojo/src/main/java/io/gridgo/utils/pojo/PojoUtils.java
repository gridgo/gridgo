package io.gridgo.utils.pojo;

import static io.gridgo.format.StringFormatter.transform;
import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.exception.InvalidFieldNameException;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import lombok.NonNull;

public class PojoUtils {

	private static final Logger log = LoggerFactory.getLogger(PojoUtils.class);

    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.DEFAULT;
    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.DEFAULT;

    private static final String SETTER_PREFIX = "set";
    private final static Set<String> GETTER_PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));
    
    public static Class<?> getElementTypeForGeneric(PojoMethodSignature signature) {
		Class<?>[] genericTypes = signature.getGenericTypes();
		Class<?> elementType = null;
		if (genericTypes != null && genericTypes.length > 0) {
			if (genericTypes.length == 1) {
				elementType = genericTypes[0];
			} else if (genericTypes.length == 2) {
				elementType = genericTypes[1];
			} else {
				log.warn("field with more than 2 generic types isn't supported");
			}
		} else if (signature.getFieldType().isArray()) {
			elementType = signature.getComponentType();
		}
		return elementType;
	}

    public static boolean isSupported(@NonNull Class<?> targetType) {
        return !(Collection.class.isAssignableFrom(targetType) //
                || Map.class.isAssignableFrom(targetType) //
                || PrimitiveUtils.isPrimitive(targetType) //
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
}
