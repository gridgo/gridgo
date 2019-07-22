package io.gridgo.utils.pojo.setter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import io.gridgo.utils.StringUtils;
import io.gridgo.utils.pojo.PojoMethodSignature;

public class PojoSetterSignatures implements Iterable<Entry<String, PojoSetterSignature>> {

    private static final String PREFIX = "set";

    private final Class<?> targetType;

    private final PojoSetterGenerator setterGenerator;

    private final Map<String, PojoSetterSignature> methodMap = new NonBlockingHashMap<>();

    PojoSetterSignatures(Class<?> targetType, PojoSetterGenerator setterGenerator) {
        this.targetType = targetType;
        this.setterGenerator = setterGenerator;
        this.init();
    }

    private void init() {
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 1 && method.getReturnType() == Void.TYPE
                    && method.getName().startsWith(PREFIX)) {

                String _fieldName = StringUtils.lowerCaseFirstLetter(method.getName().substring(3));

                Parameter param = method.getParameters()[0];
                Class<?> paramType = param.getType();
                Type parameterizedType = param.getParameterizedType();
                Class<?> parameterized = parameterizedType == null ? null : parameterizedType.getClass();

                PojoMethodSignature methodSignature = PojoMethodSignature.builder() //
                        .fieldName(_fieldName) //
                        .method(method) //
                        .fieldType(paramType) //
                        .fieldParameterizedType(parameterized) //
                        .build();

                methodMap.put(_fieldName, PojoSetterSignature.builder() //
                        .fieldName(_fieldName) //
                        .method(method) //
                        .fieldType(paramType) //
                        .fieldParameterizedType(parameterized) //
                        .setter(setterGenerator.generateSetter(targetType, methodSignature)) //
                        .build());
            }
        }
    }

    public PojoSetterSignature getMethodSignature(String fieldName) {
        return this.methodMap.get(fieldName);
    }

    @Override
    public Iterator<Entry<String, PojoSetterSignature>> iterator() {
        return this.methodMap.entrySet().iterator();
    }
}
