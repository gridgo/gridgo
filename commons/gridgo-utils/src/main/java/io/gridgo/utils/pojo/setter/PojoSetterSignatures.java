package io.gridgo.utils.pojo.setter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

    public static List<PojoMethodSignature> extractMethodSignatures(Class<?> targetType) {
        var results = new LinkedList<PojoMethodSignature>();
        Method[] methods = targetType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 1 && method.getReturnType() == Void.TYPE
                    && method.getName().startsWith(PREFIX)) {

                String _fieldName = StringUtils.lowerCaseFirstLetter(method.getName().substring(3));

                Parameter param = method.getParameters()[0];
                Class<?> paramType = param.getType();

                results.add(PojoMethodSignature.builder() //
                        .fieldName(_fieldName) //
                        .method(method) //
                        .fieldType(paramType) //
                        .build());
            }
        }
        return results;
    }

    private void init() {
        List<PojoMethodSignature> methodSignatures = extractMethodSignatures(targetType);
        for (PojoMethodSignature methodSignature : methodSignatures) {
            methodMap.put(methodSignature.getFieldName(), PojoSetterSignature.builder() //
                    .fieldName(methodSignature.getFieldName()) //
                    .method(methodSignature.getMethod()) //
                    .fieldType(methodSignature.getFieldType()) //
                    .setter(setterGenerator.generateSetter(targetType, methodSignature)) //
                    .build());
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
