package io.gridgo.utils.pojo.getter;

import static io.gridgo.utils.StringUtils.lowerCaseFirstLetter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.pojo.PojoMethodSignature;
import lombok.NonNull;

public class PojoGetterSignatures implements Iterable<Entry<String, PojoGetterSignature>> {
    private final static Set<String> PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));

    private final Class<?> targetType;

    private final Map<String, PojoGetterSignature> methodMap = new NonBlockingHashMap<>();

    private final PojoGetterGenerator getterGenerator;

    PojoGetterSignatures(Class<?> targetType, PojoGetterGenerator getterGenerator) {
        this.targetType = targetType;
        this.getterGenerator = getterGenerator;
        this.init();
    }

    public static final List<PojoMethodSignature> extractMethodSignatures(@NonNull Class<?> targetType) {
        var results = new LinkedList<PojoMethodSignature>();
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 0 //
                    && method.getReturnType() != Void.TYPE //
                    && PREFIXES.stream().anyMatch(prefix -> method.getName().startsWith(prefix))) {

                String _fieldName = lowerCaseFirstLetter(
                        method.getName().substring(method.getName().startsWith("is") ? 2 : 3));

                Class<?> fieldType = method.getReturnType();

                results.add(PojoMethodSignature.builder() //
                        .fieldName(_fieldName) //
                        .method(method) //
                        .fieldType(fieldType) //
                        .build());
            }
        }
        return results;
    }

    private void init() {
        List<PojoMethodSignature> extractedMethodSignatures = extractMethodSignatures(targetType);
        for (PojoMethodSignature methodSignature : extractedMethodSignatures) {
            PojoGetter getter = getterGenerator.generateGetter(targetType, methodSignature);

            String fieldName = methodSignature.getFieldName();
            Method method = methodSignature.getMethod();
            Class<?> fieldType = methodSignature.getFieldType();

            methodMap.put(fieldName, PojoGetterSignature.builder() //
                    .fieldName(fieldName) //
                    .method(method) //
                    .fieldType(fieldType) //
                    .getter(getter) //
                    .build());
        }
    }

    public PojoGetterSignature getMethodSignature(String fieldName) {
        return this.methodMap.get(fieldName);
    }

    @Override
    public Iterator<Entry<String, PojoGetterSignature>> iterator() {
        return this.methodMap.entrySet().iterator();
    }
}
