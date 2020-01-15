package io.gridgo.bean;

import java.io.OutputStream;

public interface BJsonSupport extends BBytesSupport {

    public static final String JSON_SERIALIZER_NAME = "json";

    default void writeJson(OutputStream out) {
        writeBytes(out, JSON_SERIALIZER_NAME);
    }

    default String toJson() {
        return new String(toBytes(JSON_SERIALIZER_NAME));
    }
}
