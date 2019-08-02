package io.gridgo.utils.pojo.setter;

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

    public PojoSetterProxy getSetterProxy(@NonNull Class<?> type) {
        String typeName = type.getName();
        if (!cache.containsKey(typeName)) {
            synchronized (cache) {
                if (!cache.containsKey(typeName)) {
                    init(type);
                }
            }
        }
        return cache.get(typeName);
    }

    private void init(Class<?> type) {
        String typeName = type.getName();

        PojoSetterProxy proxy = proxyBuilder.buildSetterProxy(type);
        cache.put(typeName, proxy);

        for (PojoMethodSignature signature : proxy.getSignatures()) {
            if (PojoUtils.isSupported(signature.getFieldType())) {
                setSetterProxy(signature, getSetterProxy(signature.getFieldType()));
            }

            Class<?>[] genericTypes = signature.getGenericTypes();
            Class<?> elementType = null;

            if (genericTypes != null && genericTypes.length > 0) {
                if (genericTypes.length == 1) {
                    elementType = genericTypes[0];
                } else if (genericTypes.length == 2) {
                    elementType = genericTypes[1];
                } else {
                    throw new RuntimeException("field with more than 2 generic types isn't supported");
                }
            } else if (signature.getFieldType().isArray()) {
                elementType = signature.getComponentType();
            }

            if (elementType != null && PojoUtils.isSupported(elementType)) {
                setElementSetterProxy(signature, getSetterProxy(elementType));
            }
        }
    }
}
