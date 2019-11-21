package io.gridgo.utils.pojo;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.Map;

import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedTypes;

import io.gridgo.utils.pojo.exception.PojoProxyException;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;
import lombok.NonNull;

public class ValueTranslatorRegistry {

    private static final ValueTranslatorRegistry INSTANCE = new ValueTranslatorRegistry();

    static {
        scanForValueTranslators("io.gridgo");
    }

    private Map<String, ValueTranslator> registry = new NonBlockingHashMap<>();

    public static ValueTranslatorRegistry getInstance() {
        return INSTANCE;
    }

    public static void scanForValueTranslators(String packageName, ClassLoader... classLoaders) {
        scanForAnnotatedTypes(packageName, RegisterValueTranslator.class,
                (clz, annotation) -> INSTANCE.registerValueTranslator(annotation.value(), clz), classLoaders);
    }

    public ValueTranslator registerValueTranslator(@NonNull String key, @NonNull Class<?> clazz) {
        try {
            return registerValueTranslator(key, (ValueTranslator) clazz.getConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Invalid registered translator type: " + clazz + " for key: `" + key + "`");
        }
    }

    public ValueTranslator registerValueTranslator(@NonNull String key, @NonNull ValueTranslator translator) {
        return registry.putIfAbsent(key, translator);
    }

    public ValueTranslator unregisterValueTranslator(@NonNull String key) {
        return registry.remove(key);
    }

    public ValueTranslator lookupValueTranslator(@NonNull String key) {
        return registry.get(key);
    }

    public ValueTranslator lookupValueTranslatorMandatory(@NonNull String key) {
        var result = lookupValueTranslator(key);
        if (result == null)
            throw new PojoProxyException("ValueTranslator cannot be found for key: " + key);
        return result;
    }
}
