package io.gridgo.utils.pojo;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedTypes;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY_NULL;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.pojo.exception.RuntimeReflectiveOperationException;
import io.gridgo.utils.pojo.getter.PojoFlattenWalker;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;
import lombok.NonNull;

public class PojoUtils {

    private static final Logger log = LoggerFactory.getLogger(PojoUtils.class);

    private static final Map<String, ValueTranslator> VALUE_TRANSLATOR_REGISTRY = new NonBlockingHashMap<>();

    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.DEFAULT;
    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.DEFAULT;

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
            foreachArray(target, ele -> walkThroughGetter(ele, proxy, walker));
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Collection.class.isInstance(target)) {
            int length = ((Collection) target).size();
            walker.accept(START_ARRAY, length);
            var it = ((Collection) target).iterator();
            while (it.hasNext())
                walkThroughGetter(it.next(), proxy, walker);
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Map.class.isInstance(target)) {
            var map = (Map<?, ?>) target;
            int size = map.size();
            walker.accept(START_MAP, size);
            var it = map.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var key = entry.getKey();
                var value = entry.getValue();
                if (value == null) {
                    walker.accept(KEY_NULL, key);
                } else {
                    walker.accept(KEY, key);
                    walkThroughGetter(value, proxy, walker);
                }
            }
            walker.accept(END_MAP, size);
            return;
        }

        var _proxy = proxy != null ? proxy : getGetterProxy(type);
        int length = _proxy.getFields().length;
        walker.accept(START_MAP, length);
        _proxy.walkThrough(target, (signature, value) -> {
            var key = signature.getTransformedOrDefaultFieldName();
            if (value == null) {
                walker.accept(KEY_NULL, key);
            } else {
                walker.accept(KEY, key);
                walkThroughGetter(value, signature.getElementGetterProxy(), walker);
            }
        });
        walker.accept(END_MAP, length);
    }

    public static final void walkThroughGetterShallowly(Object target, PojoFlattenWalker walker) {
        walkThroughGetterShallowly(target, null, walker);
    }

    @SuppressWarnings({ "rawtypes" })
    public static final void walkThroughGetterShallowly(Object target, PojoGetterProxy proxy,
            PojoFlattenWalker walker) {
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
            foreachArray(target, ele -> walkThroughGetter(ele, proxy, walker));
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Collection.class.isInstance(target)) {
            int length = ((Collection) target).size();
            walker.accept(START_ARRAY, length);
            var it = ((Collection) target).iterator();
            while (it.hasNext())
                walker.accept(VALUE, it.next());
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Map.class.isInstance(target)) {
            var map = (Map<?, ?>) target;
            int size = map.size();
            walker.accept(START_MAP, size);
            var it = map.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var key = entry.getKey();
                var value = entry.getValue();
                if (value == null) {
                    walker.accept(KEY_NULL, key);
                } else {
                    walker.accept(KEY, key);
                    walker.accept(VALUE, value);
                }
            }
            walker.accept(END_MAP, size);
            return;
        }

        var _proxy = proxy != null ? proxy : getGetterProxy(type);
        int length = _proxy.getFields().length;
        walker.accept(START_MAP, length);
        _proxy.walkThrough(target, (signature, value) -> {
            var key = signature.getTransformedOrDefaultFieldName();
            if (value == null) {
                walker.accept(KEY_NULL, key);
            } else {
                walker.accept(KEY, key);
                walker.accept(VALUE, value);
            }
        });
        walker.accept(END_MAP, length);
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
