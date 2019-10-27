package io.gridgo.utils;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.beans.Statement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.exception.ObjectReflectiveException;

public final class ObjectUtils {

    private static Map<Class<?>, Map<String, Getter>> classGetters = new NonBlockingHashMap<>();
    private static final String GETTER_PREFIX = "get";
    private static final String BOOLEAN_GETTER_PREFIX = "is";

    private static class Getter {
        boolean isMethod = false;
        Field field;
        Method method;

        public Getter(Field field) {
            this.field = field;
            this.isMethod = false;
        }

        public Getter(Method method) {
            this.method = method;
            this.isMethod = true;
        }

        public Object get(Object obj) {
            try {
                if (this.isMethod) {
                    return this.method.invoke(obj);
                } else {
                    return field.get(obj);
                }
            } catch (Exception ex) {
                throw new ObjectReflectiveException("Cannot get value from "
                        + (this.isMethod ? ("method " + this.method.getName()) : ("field " + this.field.getName())),
                        ex);
            }
        }
    }

    public static void assembleFromMap(Class<?> clazz, Object config, Map<String, Object> parameters) {
        var fieldMap = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toMap(field -> field.getName(), field -> field.getType()));
        for (String attr : parameters.keySet()) {
            if (!fieldMap.containsKey(attr))
                continue;
            var value = convertValue(parameters.get(attr), fieldMap.get(attr));
            setValue(config, attr, value);
        }
    }

    public static void setValue(Object config, String attr, Object value) {
        var setter = "set" + StringUtils.upperCaseFirstLetter(attr);
        var stmt = new Statement(config, setter, new Object[] { value });
        try {
            stmt.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object convertValue(Object value, Class<?> type) {
        if (value == null)
            return null;
        if (type == String.class)
            return value.toString();
        if (type == int.class || type == Integer.class)
            return Integer.parseInt(value.toString());
        if (type == long.class || type == Long.class)
            return Long.parseLong(value.toString());
        if (type == boolean.class || type == Boolean.class)
            return Boolean.valueOf(value.toString());
        return value;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T getValueByPath(Object obj, String path) {
        if (obj != null && path != null) {
            String[] arr = path.split("\\.");
            Object currObj = obj;
            for (int i = 0; i < arr.length; i++) {
                String fieldName = arr[i];
                if (currObj == null) {
                    throw new NullPointerException("Cannot get field '" + fieldName + "' from '" + arr[i - 1]
                            + "' == null, primitive object: " + obj.toString() + ", path: " + path);
                }
                currObj = getFieldValue(currObj, fieldName);
            }
            return (T) currObj;
        }
        throw new IllegalArgumentException("Object and path must be not-null");
    }

    @SuppressWarnings("unchecked")
    public static final <T> T getFieldValue(Object obj, String fieldName) {
        if (obj != null && fieldName != null) {
            if (fieldName.startsWith("`")) {
                fieldName = fieldName.substring(1);
            }
            if (fieldName.endsWith("`")) {
                fieldName = fieldName.substring(0, fieldName.length() - 1);
            }
            if (obj instanceof Map) {
                return (T) ((Map<String, Object>) obj).get(fieldName);
            }

            Class<?> clazz = obj.getClass();
            Map<String, Getter> getters = classGetters.containsKey(obj.getClass()) ? classGetters.get(obj.getClass())
                    : initClassGetters(clazz);
            if (getters.containsKey(fieldName)) {
                return (T) getters.get(fieldName).get(obj);
            } else {
                throw new NullPointerException(
                        "Field '" + fieldName + "' cannot be found in object type " + obj.getClass().getName());
            }
        }
        throw new IllegalArgumentException("Object and fieldName must be not-null");
    }

    private static final Map<String, Getter> initClassGetters(Class<?> clazz) {
        Map<String, Getter> classGetter = new HashMap<String, ObjectUtils.Getter>();
        Set<Field> fields = getAllInstancePublicFileds(clazz);

        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                try {
                    classGetter.put(field.getName(), new Getter(field));
                } catch (IllegalArgumentException e) {
                    throw e;
                }
            }
        }
        Set<Method> methods = getAllInstancePublicMethods(clazz);
        for (Method method : methods) {
            String methodName = method.getName();
            if (method.getParameterCount() == 0) {
                if (methodName.startsWith(GETTER_PREFIX) && methodName.length() > GETTER_PREFIX.length()) {
                    try {
                        String fieldName = StringUtils
                                .lowerCaseFirstLetter(methodName.substring(GETTER_PREFIX.length()));
                        classGetter.put(fieldName, new Getter(method));
                    } catch (IllegalArgumentException e) {
                        throw e;
                    }
                } else if (methodName.startsWith(BOOLEAN_GETTER_PREFIX)
                        && methodName.length() > BOOLEAN_GETTER_PREFIX.length()) {
                    try {
                        String fieldName = StringUtils
                                .lowerCaseFirstLetter(methodName.substring(BOOLEAN_GETTER_PREFIX.length()));
                        classGetter.put(fieldName, new Getter(method));
                    } catch (IllegalArgumentException e) {
                        throw e;
                    }
                }

            }
        }
        classGetters.put(clazz, classGetter);
        return classGetter;
    }

    private static Set<Field> getAllInstancePublicFileds(final Class<?> clazz) {
        Set<Field> result = new HashSet<>();
        Set<String> checkFieldName = new HashSet<>();
        Class<?> _class = clazz;
        while (_class != null && _class != Object.class && _class != Class.class) {
            if (!_class.isAnnotationPresent(Transient.class)) {
                Field[] fields = _class.getDeclaredFields();
                for (Field field : fields) {
                    if (!checkFieldName.contains(field.getName())) {
                        result.add(field);
                        checkFieldName.add(field.getName());
                    }
                }
            }
            _class = _class.getSuperclass();
        }
        return result;
    }

    private static Set<Method> getAllInstancePublicMethods(final Class<?> clazz) {
        Set<Method> result = new HashSet<>();
        Class<?> _class = clazz;
        while (_class != null && _class != Object.class && _class != Class.class) {
            Method[] methods = _class.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
                        && !method.isAnnotationPresent(Transient.class)) {
                    result.add(method);
                }
            }
            _class = _class.getSuperclass();
        }
        return result;
    }
}