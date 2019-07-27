package io.gridgo.utils.pojo.setter;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.MethodSignatureProxyInjector;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PojoSetterRegistryImpl implements PojoSetterRegistry, MethodSignatureProxyInjector {

    @Getter
    private final static PojoSetterRegistryImpl instance = new PojoSetterRegistryImpl();

    private final Map<String, PojoSetterProxy> CACHED_PROXIES = new NonBlockingHashMap<>();

    private final PojoSetterProxyBuilder proxyBuilder = PojoSetterProxyBuilder.newJavassist();

    private PojoSetterRegistryImpl() {
    }

    public PojoSetterProxy getSetterProxy(@NonNull Class<?> type) {
        String typeName = type.getName();
        if (!CACHED_PROXIES.containsKey(typeName)) {
            synchronized (CACHED_PROXIES) {
                if (!CACHED_PROXIES.containsKey(typeName)) {
                    PojoSetterProxy proxy = proxyBuilder.buildSetterProxy(type);
                    CACHED_PROXIES.put(typeName, proxy);

                    for (PojoMethodSignature signature : proxy.getSignatures()) {
                        try {
                            if (PojoUtils.isSupported(signature.getFieldType())) {
                                setSetterProxy(signature, getSetterProxy(signature.getFieldType()));
                            } else {
                                Class<?>[] genericTypes = signature.getGenericTypes();
                                Class<?> elementType = null;
                                if (genericTypes != null && genericTypes.length > 0) {
                                    if (genericTypes.length == 1) {
                                        elementType = genericTypes[0];
                                    } else if (genericTypes.length == 2) {
                                        elementType = genericTypes[1];
                                    } else {
                                        log.warn("field with more than 2 generic types isn't supported");
                                    }
                                } else if (signature.getFieldType().isArray()) {
                                    elementType = signature.getComponentType();
                                }
                                if (elementType != null && PojoUtils.isSupported(elementType)) {
                                    setElementSetterProxy(signature, getSetterProxy(elementType));
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return CACHED_PROXIES.get(typeName);
    }
}
