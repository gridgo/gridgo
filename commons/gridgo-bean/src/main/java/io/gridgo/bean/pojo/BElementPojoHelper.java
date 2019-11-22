package io.gridgo.bean.pojo;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetter;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import lombok.NonNull;

public class BElementPojoHelper {

    public static BElement anyToBElement(Object any) {
        return anyToBElement(any, null);
    }

    public static BElement anyToBElement(Object target, PojoGetterProxy proxy) {
        if (target == null)
            return BValue.of(null);

        Class<?> type = target.getClass();

        if (isPrimitive(type)) {
            return BValue.of(target);
        }

        if (type == Date.class || type == java.sql.Date.class) {
            return BReference.of(target);
        }

        if (BElement.class.isInstance(target)) {
            return (BElement) target;
        }

        if (type.isArray()) {
            return arrayToBArray(target, proxy);
        }

        if (Collection.class.isInstance(target)) {
            return collectionToBArray(target, proxy);
        }

        if (Map.class.isInstance(target)) {
            return mapToBObject(target, proxy);
        }

        proxy = proxy == null ? PojoUtils.getGetterProxy(type) : proxy;

        var result = BObject.ofEmpty();
        proxy.walkThrough(target, (signature, value) -> {
            var fieldName = signature.getTransformedOrDefaultFieldName();
            var elementProxy = signature.getElementGetterProxy();
            var entryValue = anyToBElement(value, elementProxy == null ? signature.getGetterProxy() : elementProxy);
            result.put(fieldName, entryValue);
        });
        return result;
    }

    private static BElement mapToBObject(Object target, PojoGetterProxy proxy) {
        var result = BObject.ofEmpty();
        var map = (Map<?, ?>) target;
        var it = map.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            var key = entry.getKey();
            var value = entry.getValue();
            result.put(key.toString(), anyToBElement(value, proxy));
        }
        return result;
    }

    private static BElement collectionToBArray(Object target, PojoGetterProxy proxy) {
        var it = ((Collection<?>) target).iterator();
        var list = BArray.ofEmpty();
        while (it.hasNext()) {
            list.add(anyToBElement(it.next(), proxy));
        }
        return list;
    }

    private static BElement arrayToBArray(Object array, PojoGetterProxy proxy) {
        var list = BArray.ofEmpty();
        foreachArray(array, ele -> list.add(anyToBElement(ele, proxy)));
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> T bObjectToPojo(@NonNull BObject src, @NonNull Class<T> type) {
        return (T) PojoSetter.ofType(type).from(BGenericData.ofObject(src)).fill();
    }

    @SuppressWarnings("unchecked")
    public static <T> T bObjectToPojo(@NonNull BObject src, Class<T> type, PojoSetterProxy proxy) {
        return (T) PojoSetter.ofType(type, proxy).from(BGenericData.ofObject(src)).fill();
    }
}
