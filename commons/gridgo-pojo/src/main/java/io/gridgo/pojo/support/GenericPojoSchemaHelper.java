package io.gridgo.pojo.support;

import java.util.Map;

import io.gridgo.pojo.PojoSchema;
import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.pojo.output.PojoSequenceOutput;

public class GenericPojoSchemaHelper {

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type == Byte.class //
                || type == Short.class //
                || type == Integer.class //
                || type == Long.class //
                || type == Float.class //
                || type == Double.class //
                || type == Boolean.class //
                || type == Character.class //
                || type == byte.class //
                || type == short.class //
                || type == int.class //
                || type == long.class //
                || type == float.class //
                || type == double.class //
                || type == boolean.class //
                || type == char.class;
    }

    public static void serializeSchema(byte[] key, Object any, PojoSchemaOutput output) {
        if (any == null) {
            output.writeNull(key);
            return;
        }

        var type = any.getClass();
        if (isPrimitiveOrWrapper(type)) {
            serializePrimitive(key, any, output);
        } else if (Iterable.class.isAssignableFrom(type)) {
            try (var _output = output.openSequence(key)) {
                serializeIterable((Iterable<?>) any, _output);
            }
        } else if (Map.class.isAssignableFrom(type)) {
            try (var _output = output.openSchema(key)) {
                serializeMap((Map<?, ?>) any, _output);
            }
        } else {
            try (var _output = output.openSchema(key)) {
                PojoSchema.of(type).serialize(any, _output);
            }
        }
    }

    public static void serializeSequence(int key, Object any, PojoSequenceOutput output) {
        if (any == null) {
            output.writeNull(key);
            return;
        }

        var type = any.getClass();
        if (isPrimitiveOrWrapper(type)) {
            serializePrimitive(key, any, output);
        } else if (Iterable.class.isAssignableFrom(type)) {
            try (var _output = output.openSequence(key)) {
                serializeIterable((Iterable<?>) any, _output);
            }
        } else if (Map.class.isAssignableFrom(type)) {
            try (var _output = output.openSchema(key)) {
                serializeMap((Map<?, ?>) any, _output);
            }
        } else {
            try (var _output = output.openSchema(key)) {
                PojoSchema.of(type).serialize(any, _output);
            }
        }
    }

    private static void serializeIterable(Iterable<?> it, PojoSequenceOutput output) {
        int i = 0;
        for (var entry : it) {
            serializeSequence(i++, entry, output);
        }
    }

    private static void serializeMap(Map<?, ?> map, PojoSchemaOutput output) {
        for (var entry : map.entrySet()) {
            serializeSchema(entry.getKey().toString().getBytes(), entry.getValue(), output);
        }
    }

    private static void serializePrimitive(byte[] key, Object any, PojoSchemaOutput output) {
        var type = any.getClass();

        if (type == Byte.class)
            output.writeByte(key, ((Number) any).byteValue());
        if (type == Short.class)
            output.writeShort(key, ((Number) any).shortValue());
        if (type == Integer.class)
            output.writeInt(key, ((Number) any).intValue());
        if (type == Long.class)
            output.writeLong(key, ((Number) any).longValue());
        if (type == Float.class)
            output.writeFloat(key, ((Number) any).floatValue());
        if (type == Double.class)
            output.writeDouble(key, ((Number) any).doubleValue());
        if (type == Boolean.class)
            output.writeBoolean(key, ((Boolean) any).booleanValue());
        if (type == Character.class)
            output.writeChar(key, ((Character) any).charValue());
    }

    private static void serializePrimitive(int key, Object any, PojoSequenceOutput output) {
        var type = any.getClass();

        if (type == Byte.class)
            output.writeByte(key, ((Number) any).byteValue());
        if (type == Short.class)
            output.writeShort(key, ((Number) any).shortValue());
        if (type == Integer.class)
            output.writeInt(key, ((Number) any).intValue());
        if (type == Long.class)
            output.writeLong(key, ((Number) any).longValue());
        if (type == Float.class)
            output.writeFloat(key, ((Number) any).floatValue());
        if (type == Double.class)
            output.writeDouble(key, ((Number) any).doubleValue());
        if (type == Boolean.class)
            output.writeBoolean(key, ((Boolean) any).booleanValue());
        if (type == Character.class)
            output.writeChar(key, ((Character) any).charValue());
    }
}
