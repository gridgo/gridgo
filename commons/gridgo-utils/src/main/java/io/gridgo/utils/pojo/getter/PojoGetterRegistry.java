package io.gridgo.utils.pojo.getter;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PojoGetterRegistry {

    private final Map<Class<?>, PojoGetterSignatures> CACHED_SIGNATURES = new NonBlockingHashMap<>();

    private final PojoGetterGenerator getterGenerator;

    public PojoGetterSignature getGetterMethodSignature(Class<?> type, String fieldName) {
        return getGetterSignatures(type).getMethodSignature(fieldName);
    }

    public PojoGetterSignatures getGetterSignatures(Class<?> type) {
        PojoGetterSignatures getterSignatures = CACHED_SIGNATURES.get(type);
        if (getterSignatures == null) {
            synchronized (CACHED_SIGNATURES) {
                if (!CACHED_SIGNATURES.containsKey(type)) {
                    getterSignatures = new PojoGetterSignatures(type, getterGenerator);
                    CACHED_SIGNATURES.put(type, getterSignatures);
                } else {
                    getterSignatures = CACHED_SIGNATURES.get(type);
                }
            }
        }
        return getterSignatures;
    }
}
