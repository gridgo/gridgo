package io.gridgo.utils.pojo;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedTypes;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;
import static io.gridgo.utils.format.StringFormatter.transform;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.StringUtils;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.pojo.exception.InvalidFieldNameException;
import io.gridgo.utils.pojo.exception.RuntimeReflectiveOperationException;
import io.gridgo.utils.pojo.getter.PojoFlattenWalker;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.UseValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;
import lombok.NonNull;

public class PojoUtils {

    private static final Logger log = LoggerFactory.getLogger(PojoUtils.class);

    private static final Map<String, ValueTranslator> VALUE_TRANSLATOR_REGISTRY = new NonBlockingHashMap<>();

    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.DEFAULT;
    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.DEFAULT;

    private static final String SETTER_PREFIX = "set";
    private final static Set<String> GETTER_PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));

    static {
        scanForValueTranslators("io.gridgo");
    }

    public static String extractMethodDescriptor(Method method) {
        String sig;

        StringBuilder sb = new StringBuilder("(");
        for (Class<?> c : method.getParameterTypes())
            sb.append((sig = Array.newInstance(c, 0).toString()).substring(1, sig.indexOf('@')));
        return sb.append(')').append(method.getReturnType() == void.class ? "V"
                : (sig = Array.newInstance(method.getReturnType(), 0).toString()).substring(1, sig.indexOf('@')))
                .toString().replaceAll("\\.", "/");
    }

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
        return !(targetType == Object.class //
                || Collection.class.isAssignableFrom(targetType) //
                || Map.class.isAssignableFrom(targetType) //
                || isPrimitive(targetType) //
                || targetType.isArray() //
                || targetType == Class.class //
                || targetType == Date.class //
                || targetType == java.sql.Date.class);
    }

    public static List<PojoMethodSignature> extractSetterMethodSignatures(Class<?> targetType) {
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

        Collection<Method> methods = extractAllMethods(targetType);

        for (Method method : methods) {
            String methodName = method.getName();
            if (method.getParameterCount() == 1 //
                    && Modifier.isPublic(method.getModifiers()) //
                    && method.getReturnType() == Void.TYPE //
                    && methodName.startsWith(SETTER_PREFIX)) {

                if (methodName.length() <= 3)
                    continue;
                var fieldName = lowerCaseFirstLetter(methodName.substring(3));

                if (!isTransient(method, fieldName)) {
                    Parameter param = method.getParameters()[0];
                    Class<?> paramType = param.getType();

                    String transformedFieldName = findTransformedFieldName(targetType, method, fieldName);
                    if (transformedFieldName == null && transformRule != null) {
                        if (ignoredFields == null || ignoredFields.size() == 0 || !ignoredFields.contains(fieldName)) {
                            Map<String, String> map = new HashMap<>();
                            map.put("fieldName", fieldName);
                            map.put("methodName", methodName);
                            map.put("fieldType", paramType.getName());
                            map.put("packageName", targetType.getPackageName());
                            map.put("typeName", targetType.getName());
                            transformedFieldName = transform(transformRule, map);
                        }
                    }

                    results.add(PojoMethodSignature.builder() //
                            .method(method) //
                            .fieldType(paramType) //
                            .fieldName(fieldName) //
                            .transformedFieldName(transformedFieldName) //
                            .valueTranslator(extractValueTranslator(method, fieldName)) //
                            .build());
                }
            }
        }
        return results;
    }

    private static ValueTranslator extractValueTranslator(Method method, String fieldName) {
        if (method.isAnnotationPresent(UseValueTranslator.class))
            return lookupValueTranslator(method.getAnnotation(UseValueTranslator.class).value());

        try {
            var field = method.getDeclaringClass().getDeclaredField(fieldName);
            if (field.isAnnotationPresent(UseValueTranslator.class))
                return lookupValueTranslator(field.getAnnotation(UseValueTranslator.class).value());
        } catch (Exception e) {
            // do nothing
        }

        return null;
    }

    private static boolean isTransient(Method method, String fieldName) {
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

    private static Field getField(Class<?> targetType, String... maybeNames) {
        for (String fieldName : maybeNames) {
            if (fieldName == null)
                continue;

            try {
                return targetType.getDeclaredField(fieldName);
            } catch (NoSuchFieldException | SecurityException e) {
                // do nothing
            }
        }
        return null;
    }

    private static String findTransformedFieldName(Class<?> targetType, Method method, String fieldName) {
        FieldName annotation = null;
        if (method.isAnnotationPresent(FieldName.class)) {
            annotation = method.getAnnotation(FieldName.class);
        } else {
            var booleanFieldName = method.getReturnType() == Boolean.class || method.getReturnType() == Boolean.TYPE //
                    ? "is" + StringUtils.upperCaseFirstLetter(fieldName)
                    : null;
            var field = getField(targetType, fieldName, booleanFieldName);
            if (field != null && field.isAnnotationPresent(FieldName.class))
                annotation = field.getAnnotation(FieldName.class);
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
            if (log.isWarnEnabled())
                log.warn("Cannot extract method signature from {}. Ignoring it!", targetType.getName());
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

        Collection<Method> methods = extractAllMethods(targetType);

        for (Method method : methods) {
            String methodName = method.getName();

            if (method.getParameterCount() == 0 //
                    && Modifier.isPublic(method.getModifiers()) //
                    && method.getReturnType() != Void.TYPE //
                    && GETTER_PREFIXES.stream().anyMatch(prefix -> methodName.startsWith(prefix))) {

                int skip = methodName.startsWith("is") ? 2 : methodName.startsWith("get") ? 3 : 0;
                if (methodName.length() <= skip)
                    continue;
                String fieldName = lowerCaseFirstLetter(methodName.substring(skip));
                if (!isTransient(method, fieldName)) {
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

    private static Collection<Method> extractAllMethods(@NonNull Class<?> targetType) {
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
        } while (t != null && t != Object.class);
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

    public static final void walkThroughGetter(Object target, PojoFlattenWalker walker) {
        walkThroughGetter(target, null, walker);
    }

    @SuppressWarnings({ "rawtypes" })
    public static final void walkThroughGetter(Object target, PojoGetterProxy proxy, PojoFlattenWalker walker) {
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

    @SuppressWarnings("unchecked")
    public static <T> T toJsonElement(Object any) {
        return (T) toJsonElement(any, null);
    }

    private static Object toJsonElement(Object target, PojoGetterProxy proxy) {
        Class<?> type;
        if (target == null //
                || isPrimitive(type = target.getClass()) //
                || type == Date.class //
                || type == java.sql.Date.class) {
            return target;
        }

        if (type.isArray()) {
            var list = new LinkedList<Object>();
            var _proxy = proxy;
            foreachArray(target, ele -> {
                list.add(toJsonElement(ele, _proxy));
            });
            return list;
        }

        if (Collection.class.isInstance(target)) {
            var it = ((Collection<?>) target).iterator();
            var list = new LinkedList<Object>();
            while (it.hasNext()) {
                list.add(toJsonElement(it.next(), proxy));
            }
            return list;
        }

        if (Map.class.isInstance(target)) {
            var result = new HashMap<String, Object>();
            var map = (Map<?, ?>) target;
            var it = map.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var key = entry.getKey();
                var value = entry.getValue();
                result.put(key.toString(), toJsonElement(value, proxy));
            }
            return result;
        }

        proxy = proxy == null ? getGetterProxy(type) : proxy;

        var result = new HashMap<String, Object>();
        proxy.walkThrough(target, (signature, value) -> {
            String fieldName = signature.getTransformedOrDefaultFieldName();
            Object entryValue = toJsonElement(value, signature.getGetterProxy());
            result.put(fieldName, entryValue);
        });
        return result;
    }

    /**
     * when field have generic type declaration
     *
     * @return list of generic types belong to corresponding field
     * @throws RuntimeReflectiveOperationException if the corresponding field not
     *                                             found
     */
    public static final Class<?>[] extractGenericTypes(Method method, String fieldName) {

        if (method.isAnnotationPresent(GenericTypes.class)) {
            return method.getAnnotation(GenericTypes.class).values();
        }

        var list = method.getParameterCount() == 0 //
                ? extractResultGenericTypes(method) //
                : extractParameterGenericTypes(method);

        if (list == null || list.size() == 0) {
            Class<?> clazz = method.getDeclaringClass();
            Field field = null;
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                return null;
            } catch (Exception e) {
                throw new RuntimeReflectiveOperationException(
                        "Error while get declared field name `" + fieldName + "` in type: " + clazz.getName(), e);
            }

            if (field.isAnnotationPresent(GenericTypes.class)) {
                return field.getAnnotation(GenericTypes.class).values();
            }

            list = extractFieldGenericTypes(field);
        }
        return list.toArray(new Class[0]);
    }

    private static List<Class<?>> extractFieldGenericTypes(Field field) {
        var genericType = field.getGenericType();
        var list = new LinkedList<Class<?>>();
        findGenericTypes(genericType, list);
        return list;
    }

    private static List<Class<?>> extractResultGenericTypes(Method method) {
        var resultType = method.getGenericReturnType();
        var list = new LinkedList<Class<?>>();
        findGenericTypes(resultType, list);
        return list;
    }

    private static List<Class<?>> extractParameterGenericTypes(Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (genericParameterTypes == null || genericParameterTypes.length == 0)
            return null;

        var list = new LinkedList<Class<?>>();
        for (Type genericParameterType : genericParameterTypes) {
            findGenericTypes(genericParameterType, list);
        }

        return list;
    }

    private static void findGenericTypes(Type theType, List<Class<?>> output) {
        if (theType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) theType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            for (Type parameterArgType : parameterArgTypes) {
                if (parameterArgType instanceof Class)
                    output.add((Class<?>) parameterArgType);
            }
        }
    }

    public static void scanForValueTranslators(String packageName, ClassLoader... classLoaders) {
        scanForAnnotatedTypes(packageName, RegisterValueTranslator.class,
                (clz, annotation) -> registerValueTranslator(annotation.value(), clz), classLoaders);
    }

    public static ValueTranslator registerValueTranslator(@NonNull String key, @NonNull Class<?> clazz) {
        try {
            return registerValueTranslator(key, (ValueTranslator) clazz.getConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Invalid registered translator type: " + clazz + " for key: `" + key + "`");
        }
    }

    public static ValueTranslator registerValueTranslator(@NonNull String key, @NonNull ValueTranslator translator) {
        return VALUE_TRANSLATOR_REGISTRY.putIfAbsent(key, translator);
    }

    public static ValueTranslator unregisterValueTranslator(@NonNull String key) {
        return VALUE_TRANSLATOR_REGISTRY.remove(key);
    }

    public static ValueTranslator lookupValueTranslator(@NonNull String key) {
        return VALUE_TRANSLATOR_REGISTRY.get(key);
    }

    public static ValueTranslator lookupValueTranslatorMandatory(@NonNull String key) {
        var result = VALUE_TRANSLATOR_REGISTRY.get(key);
        if (result == null)
            throw new NullPointerException("ValueTranslator cannot be found for key: " + key);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T getValueByPath(@NonNull Object obj, @NonNull String path) {
        String[] arr = path.split("\\.");
        Object currObj = obj;
        for (int i = 0; i < arr.length; i++) {
            String fieldName = arr[i];
            if (currObj == null) {
                throw new NullPointerException("Cannot get field '" + fieldName + "' from '" + arr[i - 1]
                        + "' == null, primitive object: " + obj.toString() + ", path: " + path);
            }
            currObj = Map.class.isInstance(currObj) //
                    ? (((Map<?, ?>) currObj).get(fieldName)) //
                    : getValue(currObj, fieldName);
        }
        return (T) currObj;
    }
}
