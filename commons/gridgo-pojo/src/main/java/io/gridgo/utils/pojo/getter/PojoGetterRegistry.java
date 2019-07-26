package io.gridgo.utils.pojo.getter;

import java.lang.reflect.Field;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PojoGetterRegistry {

    @Getter
    private static final PojoGetterRegistry instance = new PojoGetterRegistry();

    private final Map<String, PojoGetterProxy> CACHED_PROXIES = new NonBlockingHashMap<>();

    private final PojoGetterProxyBuilder getterProxyBuilder = PojoGetterProxyBuilder.newJavassist();

    private final Field signatureGetterProxyField;
    private final Field signatureElementGetterProxyField;

    private PojoGetterRegistry() {
        try {
            signatureGetterProxyField = PojoMethodSignature.class.getDeclaredField("getterProxy");
            signatureGetterProxyField.setAccessible(true);

            signatureElementGetterProxyField = PojoMethodSignature.class.getDeclaredField("elementGetterProxy");
            signatureElementGetterProxyField.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }


    public PojoGetterProxy getGetterProxy(@NonNull Class<?> type) {
        String typeName = type.getName();
        if (!CACHED_PROXIES.containsKey(typeName)) {
            synchronized (CACHED_PROXIES) {
                if (!CACHED_PROXIES.containsKey(typeName)) {
                    PojoGetterProxy proxy = getterProxyBuilder.buildGetterProxy(type);
                    CACHED_PROXIES.put(typeName, proxy);
                    for (PojoMethodSignature signature : proxy.getSignatures()) {
                        try {
                            if (PojoUtils.isSupported(signature.getFieldType())) {
                                signatureGetterProxyField.set(signature, getGetterProxy(signature.getFieldType()));
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
                                    signatureElementGetterProxyField.set(signature, getGetterProxy(elementType));
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
