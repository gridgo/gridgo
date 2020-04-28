package io.gridgo.pojo.field;

public enum PrimitiveType {

    VOID, BOOLEAN, CHAR, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE;

    public static PrimitiveType valueOf(Class<?> type) {
        if (type != null) {
            if (type == boolean.class || type == Boolean.TYPE)
                return BOOLEAN;
            if (type == int.class || type == Integer.TYPE)
                return INT;
            if (type == long.class || type == Long.TYPE)
                return LONG;
            if (type == float.class || type == Float.TYPE)
                return FLOAT;
            if (type == double.class || type == Double.TYPE)
                return DOUBLE;
            if (type == byte.class || type == Byte.TYPE)
                return BYTE;
            if (type == short.class || type == Short.TYPE)
                return SHORT;
            if (type == char.class || type == Character.TYPE)
                return CHAR;
            if (type == void.class || type == Void.TYPE)
                return VOID;
        }
        return null;
    }
}
