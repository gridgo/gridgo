package io.gridgo.pojo.output;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.Closeable;
import java.nio.charset.Charset;

public interface PojoSchemaOutput extends PojoOutput, Closeable {

    PojoSchemaOutput openSchema(byte[] key);

    PojoSequenceOutput openSequence(byte[] key);

    void close();

    void writeNull(byte[] key);

    void writeBoolean(byte[] key, boolean value);

    void writeChar(byte[] key, char value);

    void writeByte(byte[] key, byte value);

    void writeShort(byte[] key, short value);

    void writeInt(byte[] key, int value);

    void writeLong(byte[] key, long value);

    void writeFloat(byte[] key, float value);

    void writeDouble(byte[] key, double value);

    void writeString(byte[] key, String value, Charset charset);

    default void writeString(byte[] key, String value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        writeString(key, value, UTF_8);
    }

    default void writeBooleanArray(byte[] key, boolean[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeBoolean(i, value[i]);
        }
    }

    default void writeCharArray(byte[] key, char[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeChar(i, value[i]);
        }
    }

    default void writeByteArray(byte[] key, byte[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeByte(i, value[i]);
        }
    }

    default void writeShortArray(byte[] key, short[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeShort(i, value[i]);
        }
    }

    default void writeIntArray(byte[] key, int[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeInt(i, value[i]);
        }
    }

    default void writeLongArray(byte[] key, long[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeLong(i, value[i]);
        }
    }

    default void writeFloatArray(byte[] key, float[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeFloat(i, value[i]);
        }
    }

    default void writeDoubleArray(byte[] key, double[] value) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeDouble(i, value[i]);
        }
    }

    default void writeStringArray(byte[] key, String[] value, Charset charset) {
        if (value == null) {
            writeNull(key);
            return;
        }
        try (var seqOut = openSequence(key)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeString(i, value[i], charset);
        }
    }

    default void writeStringArray(byte[] key, String[] value) {
        writeStringArray(key, value, UTF_8);
    }
}
