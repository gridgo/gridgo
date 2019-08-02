package io.gridgo.utils.pojo.getter;

import static io.gridgo.utils.pojo.PojoUtils.isSupported;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.MethodSignatureProxyInjector;
import io.gridgo.utils.pojo.PojoMethodSignature;
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

        var proxy = getterProxyBuilder.buildGetterProxy(type);
        cache.put(typeName, proxy);

        for (PojoMethodSignature signature : proxy.getSignatures()) {
            if (isSupported(signature.getFieldType())) {
                setGetterProxy(signature, getGetterProxy(signature.getFieldType()));
            }

            Class<?> elementType = null;
            if (signature.isArrayType()) {
                elementType = signature.getComponentType();
            } else {
                Class<?>[] genericTypes = signature.getGenericTypes();
                if (genericTypes == null || genericTypes.length == 0) {
                    continue;
                }

                if (genericTypes.length == 1) {
                    elementType = genericTypes[0];
                } else if (genericTypes.length == 2) {
                    elementType = genericTypes[1];
                } else {
                    throw new RuntimeException("more than 2 generic types isn't supported");
                }
            }

            if (elementType != null && isSupported(elementType)) {
                setElementGetterProxy(signature, getGetterProxy(elementType));
            }
        }
    }
}
