package io.gridgo.utils;

import java.beans.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class ObjectUtils {

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
}