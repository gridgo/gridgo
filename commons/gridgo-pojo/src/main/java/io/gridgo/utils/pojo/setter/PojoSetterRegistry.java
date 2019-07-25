package io.gridgo.utils.pojo.setter;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import lombok.Getter;
import lombok.NonNull;

public class PojoSetterRegistry {

    @Getter
    private final static PojoSetterRegistry instance = new PojoSetterRegistry();

    private final Map<String, PojoSetterProxy> CACHED_PROXIES = new NonBlockingHashMap<>();

    private final PojoSetterProxyBuilder proxyBuilder = PojoSetterProxyBuilder.newJavassist();

    private PojoSetterRegistry() {
        // do nothing
    }

    public PojoSetterProxy getSetterProxy(@NonNull Class<?> type) {
        String typeName = type.getName();
        if (!CACHED_PROXIES.containsKey(typeName)) {
            synchronized (CACHED_PROXIES) {
                if (!CACHED_PROXIES.containsKey(typeName)) {
                    CACHED_PROXIES.put(typeName, this.proxyBuilder.buildSetterProxy(type));
                }
            }
        }
        return CACHED_PROXIES.get(typeName);
    }
}
