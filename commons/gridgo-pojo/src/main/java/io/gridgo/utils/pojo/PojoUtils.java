package io.gridgo.utils.pojo;

import static io.gridgo.utils.ArrayUtils.foreachArray;
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

import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.pojo.exception.RuntimeReflectiveOperationException;
import io.gridgo.utils.pojo.getter.PojoFlattenWalker;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PojoUtils {

    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.DEFAULT;
    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.DEFAULT;

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final void walk(Object target, PojoGetterProxy proxy, PojoFlattenWalker walker, boolean shallowly) {
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
            foreachArray(target, value -> {
                if (shallowly)
                    walker.accept(VALUE, value);
                else
                    walkThroughGetter(value, proxy, walker);
            });
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Collection.class.isInstance(target)) {
            int length = ((Collection) target).size();
            walker.accept(START_ARRAY, length);
            var it = ((Collection) target).iterator();
            while (it.hasNext())
                if (shallowly)
                    walker.accept(VALUE, it.next());
                else
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
                    if (shallowly)
                        walker.accept(VALUE, value);
                    else
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

            var valueTranslator = signature.getValueTranslator();
            if (valueTranslator != null && valueTranslator.translatable(value))
                value = valueTranslator.translate(value);

            if (value == null) {
                walker.accept(KEY_NULL, key);
            } else {
                walker.accept(KEY, key);
                if (shallowly)
                    walker.accept(VALUE, value);
                else
                    walkThroughGetter(value, signature.getElementGetterProxy(), walker);
            }
        });
        walker.accept(END_MAP, length);
    }

    public static final void walkThroughGetter(Object target, PojoFlattenWalker walker) {
        walkThroughGetter(target, null, walker);
    }

    public static final void walkThroughGetter(Object target, PojoGetterProxy proxy, PojoFlattenWalker walker) {
        walk(target, proxy, walker, false);
    }

    public static final void walkThroughGetterShallowly(Object target, PojoFlattenWalker walker) {
        walkThroughGetterShallowly(target, null, walker);
    }

    public static final void walkThroughGetterShallowly(Object target, PojoGetterProxy proxy,
            PojoFlattenWalker walker) {
        walk(target, proxy, walker, true);
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
