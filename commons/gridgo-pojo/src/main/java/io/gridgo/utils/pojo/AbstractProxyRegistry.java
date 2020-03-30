package io.gridgo.utils.pojo;

import static io.gridgo.utils.pojo.PojoUtils.isSupported;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import lombok.NonNull;

public abstract class AbstractProxyRegistry<T extends PojoProxy> {

    private final Map<String, T> cache = new ConcurrentHashMap<>();

    protected final T getProxy(@NonNull Class<?> type) {
        var name = type.getName();
        var proxy = cache.get(name);
        if (proxy != null)
            return proxy;

        synchronized (cache) {
            return cache.containsKey(name) ? cache.get(name) : buildProxy(type);
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
        for (PojoFieldSignature signature : proxy.getSignatures()) {
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

    private void prepareSubProxy(PojoFieldSignature signature, Map<String, T> tempCache) {
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

    protected abstract void setFieldProxy(PojoFieldSignature signature, T proxy);

    protected abstract void setFieldElementProxy(PojoFieldSignature signature, T proxy);

    protected void injectGetterProxy(PojoFieldSignature methodSignature, PojoGetterProxy getterProxy) {
        methodSignature.setGetterProxy(getterProxy);
    }

    protected void injectElementGetterProxy(PojoFieldSignature signature, PojoGetterProxy elementGetterProxy) {
        signature.setElementGetterProxy(elementGetterProxy);
    }

    protected void injectSetterProxy(PojoFieldSignature signature, PojoSetterProxy setterProxy) {
        signature.setSetterProxy(setterProxy);
    }

    protected void injectElementSetterProxy(PojoFieldSignature signature, PojoSetterProxy elementSetterProxy) {
        signature.setElementSetterProxy(elementSetterProxy);
    }

}
