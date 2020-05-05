package io.gridgo.pojo.output;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.Closeable;
import java.nio.charset.Charset;

public interface PojoSequenceOutput extends PojoOutput, Closeable {

    PojoSchemaOutput openSchema(int index);

    PojoSequenceOutput openSequence(int index);

    void close();

    void writeNull(int index);

    void writeBoolean(int index, boolean value);

    void writeChar(int index, char value);

    void writeByte(int index, byte value);

    void writeShort(int index, short value);

    void writeInt(int index, int value);

    void writeLong(int index, long value);

    void writeFloat(int index, float value);

    void writeDouble(int index, double value);

    void writeString(int index, String value, Charset charset);

    default void writeString(int index, String value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        writeString(index, value, UTF_8);
    }

    default void writeBooleanArray(int index, boolean[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeBoolean(i, value[i]);
        }
    }

    default void writeCharArray(int index, char[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeChar(i, value[i]);
        }
    }

    default void writeByteArray(int index, byte[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeByte(i, value[i]);
        }
    }

    default void writeShortArray(int index, short[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeShort(i, value[i]);
        }
    }

    default void writeIntArray(int index, int[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeInt(i, value[i]);
        }
    }

    default void writeLongArray(int index, long[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeLong(i, value[i]);
        }
    }

    default void writeFloatArray(int index, float[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeFloat(i, value[i]);
        }
    }

    default void writeDoubleArray(int index, double[] value) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeDouble(i, value[i]);
        }
    }

    default void writeStringArray(int index, String[] value, Charset charset) {
        if (value == null) {
            writeNull(index);
            return;
        }
        try (var seqOut = openSequence(index)) {
            for (int i = 0; i < value.length; i++)
                seqOut.writeString(i, value[i], charset);
        }
    }

    default void writeStringArray(int index, String[] value) {
        writeStringArray(index, value, UTF_8);
    }
}
