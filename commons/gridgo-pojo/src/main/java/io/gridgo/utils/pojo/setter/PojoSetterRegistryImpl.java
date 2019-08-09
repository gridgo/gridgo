package io.gridgo.utils.pojo.setter;

import java.util.HashMap;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.MethodSignatureProxyInjector;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import lombok.Getter;
import lombok.NonNull;

class PojoSetterRegistryImpl implements PojoSetterRegistry, MethodSignatureProxyInjector {

    @Getter
    private final static PojoSetterRegistryImpl instance = new PojoSetterRegistryImpl();

    private final Map<String, PojoSetterProxy> cache = new NonBlockingHashMap<>();

    private final PojoSetterProxyBuilder proxyBuilder = PojoSetterProxyBuilder.newJavassist();

    private PojoSetterRegistryImpl() {
    }

    @Override
    public PojoSetterProxy getSetterProxy(@NonNull Class<?> type) {
        var name = type.getName();
        var proxy = cache.get(name);
        if (proxy != null)
            return proxy;
        synchronized (cache) {
            return cache.computeIfAbsent(name, (key) -> buildProxy(type));
        }
    }

    private PojoSetterProxy buildProxy(Class<?> type) {
        var tempCache = new HashMap<String, PojoSetterProxy>();
        var result = buildProxy(type, tempCache);
        tempCache.forEach(cache::putIfAbsent);
        return result;
    }

    private PojoSetterProxy buildProxy(Class<?> type, Map<String, PojoSetterProxy> tempCache) {
        var proxy = proxyBuilder.buildSetterProxy(type);
        tempCache.put(type.getName(), proxy);
        for (PojoMethodSignature signature : proxy.getSignatures()) {
            setProxyForMethod(signature, tempCache);
        }
        return proxy;
    }

    private PojoSetterProxy lookupSetterProxy(@NonNull Class<?> type, Map<String, PojoSetterProxy> tempCache) {
        var name = type.getName();
        var result = cache.get(name);
        if (result == null)
            result = tempCache.get(name);
        if (result == null)
            result = buildProxy(type, tempCache);
        return result;
    }

    private void setProxyForMethod(PojoMethodSignature signature, Map<String, PojoSetterProxy> tempCache) {
        if (PojoUtils.isSupported(signature.getFieldType())) {
            setSetterProxy(signature, lookupSetterProxy(signature.getFieldType(), tempCache));
        } else {
            setSetterProxyForUnsupportedTypes(signature, tempCache);
        }
    }

    private void setSetterProxyForUnsupportedTypes(PojoMethodSignature signature,
            Map<String, PojoSetterProxy> tempCache) {
        Class<?> elementType = PojoUtils.getElementTypeForGeneric(signature);
        if (elementType != null && PojoUtils.isSupported(elementType)) {
            setElementSetterProxy(signature, lookupSetterProxy(elementType, tempCache));
        }
    }
}
