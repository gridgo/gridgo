package io.gridgo.bean;

import lombok.Getter;

public enum BType {

    NULL, //
    BOOLEAN(Boolean.class), //
    CHAR(Character.class), //
    BYTE(Byte.class), //
    SHORT(Short.class), //
    INTEGER(Integer.class), //
    FLOAT(Float.class), //
    LONG(Long.class), //
    DOUBLE(Double.class), //
    STRING(String.class), //
    RAW, //
    OBJECT, //
    ARRAY, //
    REFERENCE, //
    GENERIC_NUMBER;

    public static BType forName(String name) {
        for (BType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    @Getter
    private Class<?> innerClass;

    private BType() {

    }

    private BType(Class<?> innerClass) {
        this.innerClass = innerClass;
    }

    public boolean isNumber() {
        return this == BYTE //
                || this == SHORT //
                || this == INTEGER //
                || this == FLOAT //
                || this == LONG //
                || this == DOUBLE //
                || this == GENERIC_NUMBER;
    }
}
