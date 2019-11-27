package io.gridgo.bean;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;

class BElementUtils {

    @SuppressWarnings("unchecked")
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
            return collectionToBArray((Collection<?>) target, proxy);
        }

        if (Map.class.isInstance(target)) {
            return mapToBObject((Map<?, ?>) target, proxy);
        }

        proxy = proxy == null ? PojoUtils.getGetterProxy(type) : proxy;

        var result = BObject.ofEmpty();
        proxy.walkThrough(target, (signature, value) -> {
            var fieldName = signature.getTransformedOrDefaultFieldName();
            var elementProxy = signature.getElementGetterProxy();

            var valueTranslator = signature.getValueTranslator();
            if (valueTranslator != null && valueTranslator.translatable(value))
                value = valueTranslator.translate(value, signature);

            var entryValue = anyToBElement(value, elementProxy == null ? signature.getGetterProxy() : elementProxy);
            result.put(fieldName, entryValue);
        });
        return result;
    }

    private static BElement mapToBObject(Map<?, ?> map, PojoGetterProxy proxy) {
        var result = BObject.ofEmpty();
        var it = map.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            var key = entry.getKey();
            var value = entry.getValue();
            result.put(key.toString(), anyToBElement(value, proxy));
        }
        return result;
    }

    private static BElement collectionToBArray(Collection<?> target, PojoGetterProxy proxy) {
        var list = BArray.ofEmpty();
        target.forEach(list::addAny);
        return list;
    }

    private static BElement arrayToBArray(Object array, PojoGetterProxy proxy) {
        var list = BArray.ofEmpty();
        foreachArray(array, ele -> list.add(anyToBElement(ele, proxy)));
        return list;
    }
}
