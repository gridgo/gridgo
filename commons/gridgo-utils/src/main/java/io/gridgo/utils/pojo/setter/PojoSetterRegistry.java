package io.gridgo.utils.pojo.setter;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PojoSetterRegistry {

    private final Map<String, PojoSetterSignatures> CACHED_SIGNATURES = new NonBlockingHashMap<>();

    private final PojoSetterGenerator setterGenerator;

    public PojoSetterSignature getSetterMethodSignature(Class<?> type, String fieldName) {
        PojoSetterSignatures setters = CACHED_SIGNATURES.get(type.getName());
        if (setters == null) {
            synchronized (CACHED_SIGNATURES) {
                if (!CACHED_SIGNATURES.containsKey(type.getName())) {
                    setters = new PojoSetterSignatures(type, setterGenerator);
                    CACHED_SIGNATURES.put(type.getName(), setters);
                } else {
                    setters = CACHED_SIGNATURES.get(type.getName());
                }
            }
        }
        return setters.getMethodSignature(fieldName);
    }

    public PojoSetterSignatures getSetterSignatures(Class<?> type) {
        return CACHED_SIGNATURES.get(type.getName());
    }
}
