package io.gridgo.utils.pojo.getter;

import java.util.HashMap;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.MethodSignatureProxyInjector;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import lombok.Getter;
import lombok.NonNull;

class PojoGetterRegistryImpl implements PojoGetterRegistry, MethodSignatureProxyInjector {

    @Getter
    private static final PojoGetterRegistry instance = new PojoGetterRegistryImpl();

    private final Map<String, PojoGetterProxy> cache = new NonBlockingHashMap<>();

    private final PojoGetterProxyBuilder getterProxyBuilder = PojoGetterProxyBuilder.newJavassist();

    private PojoGetterRegistryImpl() {
    }

    public PojoGetterProxy getGetterProxy(@NonNull Class<?> type) {
        var name = type.getName();
        var proxy = cache.get(name);
        if (proxy != null)
            return proxy;
        synchronized (cache) {
            return cache.computeIfAbsent(name, key -> buildProxy(type));
        }
    }

    private PojoGetterProxy buildProxy(Class<?> type) {
        var tempCache = new HashMap<String, PojoGetterProxy>();
        var result = buildProxy(type, tempCache);
        tempCache.forEach(cache::putIfAbsent);
        return result;
    }

    private PojoGetterProxy buildProxy(Class<?> type, Map<String, PojoGetterProxy> tempCache) {
        PojoGetterProxy proxy = getterProxyBuilder.buildGetterProxy(type);
        tempCache.put(type.getName(), proxy);
        for (PojoMethodSignature signature : proxy.getSignatures()) {
            setProxyForMethod(signature, tempCache);
        }
        return proxy;
    }

    private PojoGetterProxy lookupGetterProxy(Class<?> type, Map<String, PojoGetterProxy> tempCache) {
        var name = type.getName();
        var result = cache.get(name);
        if (result == null)
            result = tempCache.get(name);
        if (result == null)
            result = buildProxy(type, tempCache);
        return result;
    }

    private void setProxyForMethod(PojoMethodSignature signature, Map<String, PojoGetterProxy> tempCache) {
        if (PojoUtils.isSupported(signature.getFieldType())) {
            setGetterProxy(signature, lookupGetterProxy(signature.getFieldType(), tempCache));
        } else {
            setProxyForUnsupportedTypes(signature, tempCache);
        }
    }

    private void setProxyForUnsupportedTypes(PojoMethodSignature signature, Map<String, PojoGetterProxy> tempCache) {
        var elementType = PojoUtils.getElementTypeForGeneric(signature);
        if (elementType != null && PojoUtils.isSupported(elementType)) {
            setElementGetterProxy(signature, lookupGetterProxy(elementType, tempCache));
        }
    }
}
