package io.gridgo.utils.pojo.translator;

import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedMethods;
import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedTypes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.ClasspathUtils;
import io.gridgo.utils.helper.ClasspathScanner;
import lombok.Getter;
import lombok.NonNull;

@SuppressWarnings("rawtypes")
public class ValueTranslators implements ClasspathScanner {

    @Getter
    private static final ValueTranslators instance = new ValueTranslators();

    private final Map<String, ValueTranslator> registry = new NonBlockingHashMap<>();

    private ValueTranslators() {
        var packages = new HashSet<String>();
        packages.add("io.gridgo");

        var tobeScanned = System.getProperty("gridgo.pojo.translator.scan", null);
        if (tobeScanned != null) {
            var arr = tobeScanned.split(",");
            for (var packageName : arr) {
                packageName = packageName.trim();
                if (packageName.isBlank())
                    continue;
                packages.add(packageName);
            }
        }

        var list = new ArrayList<String>(packages);
        list.sort((s1, s2) -> s1.length() - s2.length());

        var processed = new ArrayList<>();
        for (var packageName : list) {
            boolean next = false;
            for (var processedPackage : processed) {
                if (packageName.startsWith(processedPackage + ".")) {
                    next = true;
                    break;
                }
            }

            if (next)
                continue;

            this.scan(packageName, Thread.currentThread().getContextClassLoader());
            processed.add(packageName);
        }
    }

    @Override
    public synchronized void scan(String packageName, ClassLoader... classLoaders) {
        var reflections = ClasspathUtils.reflections(packageName, classLoaders);
        scanForAnnotatedTypes(reflections, RegisterValueTranslator.class, this::acceptAnnotatedClass);
        scanForAnnotatedMethods(reflections, RegisterValueTranslator.class, this::acceptAnnotatedMethod);
    }

    private void acceptAnnotatedMethod(@NonNull Method method, @NonNull RegisterValueTranslator annotation) {
        register(annotation.value(), new MethodValueTranslator(method));
    }

    private void acceptAnnotatedClass(@NonNull Class<?> clz, @NonNull RegisterValueTranslator annotation) {
        register(annotation.value(), clz);
    }

    public ValueTranslator register(@NonNull String key, @NonNull Class<?> clazz) {
        try {
            return register(key, (ValueTranslator) clazz.getConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Invalid registered translator type: " + clazz + " for key: `" + key + "`");
        }
    }

    public ValueTranslator register(@NonNull String key, @NonNull ValueTranslator translator) {
        return registry.putIfAbsent(key, translator);
    }

    public ValueTranslator unregister(@NonNull String key) {
        return registry.remove(key);
    }

    public ValueTranslator lookup(@NonNull String key) {
        return registry.get(key);
    }

    public ValueTranslator lookupMandatory(@NonNull String key) {
        var result = lookup(key);
        if (result == null)
            throw new NullPointerException("ValueTranslator cannot be found for key: " + key);
        return result;
    }
}
