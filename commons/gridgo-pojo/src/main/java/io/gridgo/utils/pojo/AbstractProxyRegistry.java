package io.gridgo.utils.pojo;

import static io.gridgo.utils.pojo.PojoUtils.isSupported;

import java.util.HashMap;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import lombok.NonNull;

public abstract class AbstractProxyRegistry<T extends PojoProxy> {

    private final Map<String, T> cache = new NonBlockingHashMap<>();

    protected final T getProxy(@NonNull Class<?> type) {
        var name = type.getName();
        var proxy = cache.get(name);
        if (proxy != null)
            return proxy;

        synchronized (cache) {
            return buildProxy(type);
        }
    }

    private T buildProxy(Class<?> type) {
        var tempCache = new HashMap<String, T>();
        var result = buildProxy(type, tempCache);
        tempCache.forEach(cache::putIfAbsent);
        return result;
    }

    private T buildProxy(Class<?> type, Map<String, T> tempCache) {
        T proxy = buildMandatory(type);
        tempCache.put(type.getName(), proxy);
        for (PojoMethodSignature signature : proxy.getSignatures()) {
            prepareSubProxy(signature, tempCache);
        }
        return proxy;
    }

    private T lookupProxy(Class<?> type, Map<String, T> tempCache) {
        var name = type.getName();
        var result = cache.get(name);
        if (result == null)
            result = tempCache.get(name);
        if (result == null)
            result = buildProxy(type, tempCache);
        return result;
    }

    private void prepareSubProxy(PojoMethodSignature signature, Map<String, T> tempCache) {
        if (isSupported(signature.getFieldType())) {
            setFieldProxy(signature, lookupProxy(signature.getFieldType(), tempCache));
        } else {
            var elementType = PojoUtils.getElementTypeForGeneric(signature);
            if (elementType == null || !isSupported(elementType))
                return;

            setFieldElementProxy(signature, lookupProxy(elementType, tempCache));
        }
    }

    protected abstract T buildMandatory(Class<?> type);

    protected abstract void setFieldProxy(PojoMethodSignature signature, T proxy);

    protected abstract void setFieldElementProxy(PojoMethodSignature signature, T proxy);

    protected void setGetterProxy(PojoMethodSignature methodSignature, PojoGetterProxy getterProxy) {
        methodSignature.setGetterProxy(getterProxy);
    }

    protected void setElementGetterProxy(PojoMethodSignature methodSignature, PojoGetterProxy elementGetterProxy) {
        methodSignature.setElementGetterProxy(elementGetterProxy);
    }

    protected void setSetterProxy(PojoMethodSignature methodSignature, PojoSetterProxy setterProxy) {
        methodSignature.setSetterProxy(setterProxy);
    }

    protected void setElementSetterProxy(PojoMethodSignature methodSignature, PojoSetterProxy elementSetterProxy) {
        methodSignature.setElementSetterProxy(elementSetterProxy);
    }

}
