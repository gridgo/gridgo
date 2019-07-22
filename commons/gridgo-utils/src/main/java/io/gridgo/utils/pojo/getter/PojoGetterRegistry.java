package io.gridgo.utils.pojo.getter;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PojoGetterRegistry {

    private final Map<Class<?>, PojoGetterSignatures> CACHED_SIGNATURES = new NonBlockingHashMap<>();

    private final PojoGetterGenerator getterGenerator;

    public PojoGetterSignature getGetterMethodSignature(Class<?> type, String fieldName) {
        PojoGetterSignatures getterSignatures = getGetterSignatures(type);
        if (getterSignatures == null) {
            synchronized (CACHED_SIGNATURES) {
                if (!CACHED_SIGNATURES.containsKey(type)) {
                    getterSignatures = new PojoGetterSignatures(type, getterGenerator);
                    CACHED_SIGNATURES.put(type, getterSignatures);
                } else {
                    getterSignatures = getGetterSignatures(type);
                }
            }
        }
        return getterSignatures.getMethodSignature(fieldName);
    }

    public PojoGetterSignatures getGetterSignatures(Class<?> type) {
        return CACHED_SIGNATURES.get(type);
    }
}
