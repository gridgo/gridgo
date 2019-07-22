package io.gridgo.utils.pojo.getter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.StringUtils;
import io.gridgo.utils.pojo.PojoMethodSignature;

public class PojoGetterSignatures implements Iterable<Entry<String, PojoGetterSignature>> {

    private final Class<?> targetType;

    private final Map<String, PojoGetterSignature> methodMap = new NonBlockingHashMap<>();

    private final Set<String> PREFIXES = new HashSet<String>(Arrays.asList("get", "is"));

    private final PojoGetterGenerator getterGenerator;

    PojoGetterSignatures(Class<?> targetType, PojoGetterGenerator getterGenerator) {
        this.targetType = targetType;
        this.getterGenerator = getterGenerator;
        this.init();
    }

    private void init() {
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 0 //
                    && method.getReturnType() != Void.TYPE //
                    && PREFIXES.stream().anyMatch(prefix -> method.getName().startsWith(prefix))) {

                String _fieldName = StringUtils
                        .lowerCaseFirstLetter(method.getName().substring(method.getName().startsWith("is") ? 2 : 3));

                Class<?> fieldType = method.getReturnType();

                PojoMethodSignature methodSignature = PojoMethodSignature.builder() //
                        .fieldName(_fieldName) //
                        .method(method) //
                        .fieldType(fieldType) //
                        .build();

                PojoGetter getter = getterGenerator.generateGetter(targetType, methodSignature);

                methodMap.put(_fieldName, PojoGetterSignature.builder() //
                        .fieldName(_fieldName) //
                        .method(method) //
                        .fieldType(fieldType) //
                        .getter(getter) //
                        .build());
            }
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
