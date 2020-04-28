package io.gridgo.utils.pojo.translator;

import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedMethods;
import static io.gridgo.utils.ClasspathUtils.scanForAnnotatedTypes;
import static io.gridgo.utils.pojo.PojoMethodType.GETTER;
import static io.gridgo.utils.pojo.PojoMethodType.NONE;
import static io.gridgo.utils.pojo.PojoMethodType.SETTER;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.ClasspathUtils;
import io.gridgo.utils.helper.ClasspathScanner;
import lombok.NonNull;

@SuppressWarnings("rawtypes")
public class ValueTranslators implements ClasspathScanner {

    private static final ValueTranslators INSTANCE = new ValueTranslators();

    private final Map<String, ValueTranslator> nameRegistry = new NonBlockingHashMap<>();

    private final Map<Class<?>, ValueTranslator> getterDefaultRegistry = new NonBlockingHashMap<>();

    private final Map<Class<?>, ValueTranslator> setterDefaultRegistry = new NonBlockingHashMap<>();

    public static ValueTranslators getInstance() {
        return INSTANCE;
    }

    private ValueTranslators() {
        var packages = new HashSet<String>();
        packages.add("io.gridgo");

        var tobeScanned = System.getProperty("gridgo.pojo.translator.scan", null);
        if (tobeScanned != null) {
            var arr = tobeScanned.split(",");
            for (var packageName : arr) {
                packageName = packageName.trim();
                if (!packageName.isBlank())
                    packages.add(packageName);
            }
        }

        var contextClassLoader = Thread.currentThread().getContextClassLoader();

        for (var packageName : packages) {
            scan(packageName, contextClassLoader);
        }
    }

    @Override
    public synchronized void scan(String packageName, ClassLoader... classLoaders) {
        var reflections = ClasspathUtils.reflections(packageName, classLoaders);
        scanForAnnotatedTypes(reflections, RegisterValueTranslator.class, this::acceptAnnotatedClass);
        scanForAnnotatedMethods(reflections, RegisterValueTranslator.class, this::acceptAnnotatedMethod);
    }

    private void acceptAnnotatedMethod(Method method, RegisterValueTranslator annotation) {
        var translator = new ReflectiveMethodValueTranslator(method);
        registerByAnnotation(annotation, translator);
    }

    private void acceptAnnotatedClass(Class<?> clazz, RegisterValueTranslator annotation) {
        ValueTranslator translator;
        try {
            translator = (ValueTranslator) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance for value translator: " + clazz, e);
        }

        registerByAnnotation(annotation, translator);
    }

    private void registerByAnnotation(RegisterValueTranslator annotation, ValueTranslator translator) {
        Class<?> autoTranslatedType;
        var defaultMethodType = annotation.defaultFor();
        if ((autoTranslatedType = annotation.defaultType()) == null //
                && defaultMethodType != null && defaultMethodType != NONE)
            throw new RuntimeException("Invalid registering default translator, registered type is null");

        registerByName(annotation.value(), translator);

        if (defaultMethodType == GETTER)
            registerGetterDefault(autoTranslatedType, translator);

        if (defaultMethodType == SETTER)
            registerSetterDefault(autoTranslatedType, translator);
    }

    /**************************************************************
     ************************** REGISTER **************************
     **************************************************************/
    public ValueTranslator registerByName(String key, @NonNull ValueTranslator translator) {
        return nameRegistry.put(key, translator);
    }

    public ValueTranslator registerGetterDefault(Class<?> autoTranslated, ValueTranslator translator) {
        return getterDefaultRegistry.put(autoTranslated, translator);
    }

    public ValueTranslator registerSetterDefault(Class<?> autoTranslated, ValueTranslator translator) {
        return setterDefaultRegistry.put(autoTranslated, translator);
    }

    /**************************************************************
     ************************* UNREGISTER *************************
     **************************************************************/
    public ValueTranslator unregisterByName(String name) {
        return nameRegistry.remove(name);
    }

    public ValueTranslator unregisterGetterDefault(Class<?> type) {
        return getterDefaultRegistry.remove(type);
    }

    public ValueTranslator unregisterSetterDefault(Class<?> type) {
        return setterDefaultRegistry.remove(type);
    }

    /**************************************************************
     *************************** LOOKUP ***************************
     **************************************************************/
    public ValueTranslator lookupSetterDefault(Class<?> type) {
        return setterDefaultRegistry.get(type);
    }

    public ValueTranslator lookupGetterDefault(Class<?> type) {
        return getterDefaultRegistry.get(type);
    }

    public ValueTranslator lookup(String key) {
        return nameRegistry.get(key);
    }

    public ValueTranslator lookupMandatory(String key) {
        var result = lookup(key);
        if (result == null)
            throw new RuntimeException("ValueTranslator cannot be found for key: " + key);
        return result;
    }
}
