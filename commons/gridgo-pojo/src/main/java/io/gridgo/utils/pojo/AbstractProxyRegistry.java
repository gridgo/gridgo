package io.gridgo.utils.pojo;

import java.util.HashMap;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import lombok.NonNull;

public abstract class AbstractProxyRegistry<T extends PojoProxy> implements MethodSignatureProxyInjector {

    private final Map<String, T> cache = new NonBlockingHashMap<>();

    protected final T getProxy(@NonNull Class<?> type) {
        var name = type.getName();
        var proxy = cache.get(name);
        if (proxy != null)
            return proxy;
        synchronized (cache) {
            return cache.computeIfAbsent(name, key -> buildProxy(type));
        }
    }

    private T buildProxy(Class<?> type) {
        var tempCache = new HashMap<String, T>();
        var result = buildProxy(type, tempCache);
        tempCache.forEach(cache::putIfAbsent);
        return result;
    }

    protected abstract T buildMandatory(Class<?> type);

    private T buildProxy(Class<?> type, Map<String, T> tempCache) {
        T proxy = buildMandatory(type);
        tempCache.put(type.getName(), proxy);
        for (PojoMethodSignature signature : proxy.getSignatures()) {
            setProxyForMethod(signature, tempCache);
        }
        return proxy;
    }

    private T lookupGetterProxy(Class<?> type, Map<String, T> tempCache) {
        var name = type.getName();
        var result = cache.get(name);
        if (result == null)
            result = tempCache.get(name);
        if (result == null)
            result = buildProxy(type, tempCache);
        return result;
    }

    protected abstract void setProxy(PojoMethodSignature signature, T proxy);

    protected abstract void setElementProxy(PojoMethodSignature signature, T proxy);

    private void setProxyForMethod(PojoMethodSignature signature, Map<String, T> tempCache) {
        if (PojoUtils.isSupported(signature.getFieldType())) {
            setProxy(signature, lookupGetterProxy(signature.getFieldType(), tempCache));
        } else {
            setProxyForUnsupportedTypes(signature, tempCache);
        }
    }

    private void setProxyForUnsupportedTypes(PojoMethodSignature signature, Map<String, T> tempCache) {
        var elementType = PojoUtils.getElementTypeForGeneric(signature);
        if (elementType != null && PojoUtils.isSupported(elementType)) {
            setElementProxy(signature, lookupGetterProxy(elementType, tempCache));
        }
    }
}
