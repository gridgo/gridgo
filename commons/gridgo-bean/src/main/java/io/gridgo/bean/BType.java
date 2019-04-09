package io.gridgo.bean;

public enum BType {

    NULL, BOOLEAN, CHAR, BYTE, SHORT, INTEGER, FLOAT, LONG, DOUBLE, STRING, RAW, OBJECT, ARRAY, REFERENCE, GENERIC_NUMBER;

    public static BType forName(String name) {
        for (BType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
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
