package io.gridgo.utils.pojo.getter;

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
        if (cache.containsKey(name))
            return cache.get(name);
        synchronized (cache) {
            if (cache.containsKey(name))
                return cache.get(name);
            return buildProxy(type);
        }
    }

    private PojoGetterProxy buildProxy(Class<?> type) {
        PojoGetterProxy proxy = getterProxyBuilder.buildGetterProxy(type);
        cache.put(type.getName(), proxy);
        for (PojoMethodSignature signature : proxy.getSignatures()) {
            setProxyForMethod(signature);
        }
        return proxy;
    }

    private void setProxyForMethod(PojoMethodSignature signature) {
        if (PojoUtils.isSupported(signature.getFieldType())) {
            setGetterProxy(signature, getGetterProxy(signature.getFieldType()));
        } else {
            setProxyForUnsupportedTypes(signature);
        }
    }

    private void setProxyForUnsupportedTypes(PojoMethodSignature signature) {
        var elementType = PojoUtils.getElementTypeForGeneric(signature);
        if (elementType != null && PojoUtils.isSupported(elementType)) {
            setElementGetterProxy(signature, getGetterProxy(elementType));
        }
    }
}
