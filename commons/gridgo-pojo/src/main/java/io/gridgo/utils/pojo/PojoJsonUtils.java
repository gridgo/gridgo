package io.gridgo.utils.pojo;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;

import io.gridgo.utils.pojo.getter.PojoGetterProxy;

public class PojoJsonUtils {

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

        proxy = proxy == null ? PojoUtils.getGetterProxy(type) : proxy;

        var result = new HashMap<String, Object>();
        proxy.walkThrough(target, (signature, value) -> {
            String fieldName = signature.getTransformedOrDefaultFieldName();
            Object entryValue = toJsonElement(value, signature.getGetterProxy());
            result.put(fieldName, entryValue);
        });
        return result;
    }
}
