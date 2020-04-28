package io.gridgo.pojo.field;

public interface PojoPrimitive {

    PrimitiveType getPrimitiveType();

    default boolean isArray() {
        return false;
    }

    default boolean isBoolean() {
        return getPrimitiveType() == PrimitiveType.BOOLEAN;
    }

    default boolean isChar() {
        return getPrimitiveType() == PrimitiveType.CHAR;
    }

    default boolean isByte() {
        return getPrimitiveType() == PrimitiveType.BYTE;
    }

    default boolean isShort() {
        return getPrimitiveType() == PrimitiveType.SHORT;
    }

    default boolean isInt() {
        return getPrimitiveType() == PrimitiveType.INT;
    }

    default boolean isLong() {
        return getPrimitiveType() == PrimitiveType.LONG;
    }

    default boolean isFloat() {
        return getPrimitiveType() == PrimitiveType.FLOAT;
    }

    default boolean isDouble() {
        return getPrimitiveType() == PrimitiveType.DOUBLE;
    }
}
