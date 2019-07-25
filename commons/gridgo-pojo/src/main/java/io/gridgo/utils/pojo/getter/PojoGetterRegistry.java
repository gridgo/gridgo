package io.gridgo.utils.pojo.getter;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import lombok.Getter;
import lombok.NonNull;

public class PojoGetterRegistry {

    @Getter
    private static final PojoGetterRegistry instance = new PojoGetterRegistry();

    private final Map<String, PojoGetterProxy> CACHED_PROXIES = new NonBlockingHashMap<>();

    private final PojoGetterProxyBuilder walkerBuilder = PojoGetterProxyBuilder.newJavassist();

    private PojoGetterRegistry() {
        // do nothing
    }

    public PojoGetterProxy getGetterProxy(@NonNull Class<?> type) {
        String typeName = type.getName();
        if (!CACHED_PROXIES.containsKey(typeName)) {
            synchronized (CACHED_PROXIES) {
                if (!CACHED_PROXIES.containsKey(typeName)) {
                    CACHED_PROXIES.put(typeName, walkerBuilder.buildGetterProxy(type));
                }
            }
        }
        return CACHED_PROXIES.get(typeName);
    }
}
