package io.gridgo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import io.gridgo.utils.exception.UnsupportedTypeException;

public final class ByteArrayUtils {

    private static final byte ZERO = (byte) 0;
    private static final byte[] NUMBER_ZERO_BYTES = new byte[8];
    private static final ThreadLocal<ByteBuffer> NUMBER_BYTE_BUFFERS = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(8);
        }
    };

    private ByteArrayUtils() {
        // private constructor
    }

    public static byte[] primitiveToBytes(Object data) {
        if (data == null)
            return null;
        if (data instanceof byte[])
            return (byte[]) data;
        if (data instanceof Boolean)
            return new byte[] { (byte) (((Boolean) data) ? 1 : 0) };
        if (data instanceof String)
            return ((String) data).getBytes();
        if (data instanceof BigDecimal) {
            BigInteger theInt = ((BigDecimal) data).unscaledValue();
            return theInt.toByteArray();
        }
        if (data instanceof BigInteger)
            return ((BigInteger) data).toByteArray();
        if (data instanceof Byte)
            return new byte[] { (Byte) data };

        return primitiveToBytesWithBuffer(data);
    }

    private static byte[] primitiveToBytesWithBuffer(Object data) {
        ByteBuffer buffer = null;
        if (data instanceof Short) {
            buffer = ByteBuffer.allocate(Short.BYTES);
            buffer.putShort((Short) data);
        } else if (data instanceof Integer) {
            buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.putInt((Integer) data);
        } else if (data instanceof Float) {
            buffer = ByteBuffer.allocate(Float.BYTES);
            buffer.putFloat((Float) data);
        } else if (data instanceof Long) {
            buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong((Long) data);
        } else if (data instanceof Double) {
            buffer = ByteBuffer.allocate(Double.BYTES);
            buffer.putDouble((Double) data);
        } else if (data instanceof Character) {
            buffer = ByteBuffer.allocate(Character.BYTES);
            buffer.putChar((Character) data);
        } else {
            throw new IllegalArgumentException("Data must be primitive type");
        }

        return buffer.array();
    }

    @SuppressWarnings("unchecked")
    public static final <T> T bytesToPrimitive(Class<T> clazz, byte[] bytes) {
        if (clazz == null || bytes == null)
            return null;
        if (clazz == byte[].class)
            return (T) bytes;
        if (clazz == Boolean.class || clazz == Boolean.TYPE)
            return (T) bytesToBoolean(bytes);
        if (clazz == Character.class || clazz == Character.TYPE)
            return (T) bytesToChar(bytes);
        if (clazz == Byte.class || clazz == Byte.TYPE)
            return (T) bytesToByte(bytes);
        if (clazz == Short.class || clazz == Short.TYPE)
            return (T) bytesToShort(bytes);
        if (clazz == Integer.class || clazz == Integer.TYPE)
            return (T) bytesToInt(bytes);
        if (clazz == Long.class || clazz == Long.TYPE)
            return (T) bytesToLong(bytes);
        if (clazz == Float.class || clazz == Float.TYPE)
            return (T) bytesToFloat(bytes);
        if (clazz == Double.class || clazz == Double.TYPE)
            return (T) bytesToDouble(bytes);
        if (clazz == BigInteger.class)
            return (T) new BigInteger(bytes);
        if (clazz == BigDecimal.class)
            return (T) new BigDecimal(new BigInteger(bytes));
        if (clazz == String.class)
            return (T) new String(bytes);
        throw new UnsupportedTypeException("Cannot convert bytes to primitive type " + clazz);
    }

    public static Double bytesToDouble(byte[] bytes) {
        return bigEndianNumberBuffer(bytes, Double.BYTES).getDouble();
    }

    public static Float bytesToFloat(byte[] bytes) {
        return bigEndianNumberBuffer(bytes, Float.BYTES).getFloat();
    }

    public static Long bytesToLong(byte[] bytes) {
        return bigEndianNumberBuffer(bytes, Long.BYTES).getLong();
    }

    public static Integer bytesToInt(byte[] bytes) {
        return bigEndianNumberBuffer(bytes, Integer.BYTES).getInt();
    }

    public static Short bytesToShort(byte[] bytes) {
        return bigEndianNumberBuffer(bytes, Short.BYTES).getShort();
    }

    public static Byte bytesToByte(byte[] bytes) {
        return bytes.length > 0 ? bytes[0] : ZERO;
    }

    private static ByteBuffer bigEndianNumberBuffer(byte[] bytes, int minLength) {
        if (bytes.length >= minLength)
            return ByteBuffer.wrap(bytes);

        return NUMBER_BYTE_BUFFERS.get() // only 8bytes length initialized, use for number only
                .clear() // clear current buffer
                .limit(minLength) // set limit, not really necessary
                .put(NUMBER_ZERO_BYTES, 0, minLength - bytes.length) // padded byte zero
                .put(bytes) // fill the rest by input bytes
                .flip(); // flip for caller to read
    }

    public static Character bytesToChar(byte[] bytes) {
        if (bytes.length == 0)
            return Character.valueOf('\0');
        if (bytes.length == 1)
            return Character.valueOf(bytes[0] == 0 ? '\0' : '\1');
        return Character.valueOf(ByteBuffer.wrap(bytes).getChar());
    }

    public static Boolean bytesToBoolean(byte[] bytes) {
        if (bytes.length == 0)
            return Boolean.FALSE;
        for (byte b : bytes) {
            if (b == 1)
                return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static final String toHex(byte[] bytes, String prefix) {
        if (bytes == null)
            return null;
        var buffer = new StringBuilder();
        if (prefix != null)
            buffer.append(prefix);
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(Character.forDigit((bytes[i] >> 4) & 0xF, 16));
            buffer.append(Character.forDigit((bytes[i] & 0xF), 16));
        }
        return buffer.toString();
    }

    public static final String toHex(byte[] bytes) {
        return toHex(bytes, null);
    }

    public static final byte[] concat(byte[]... bytesArray) {
        int length = 0;
        for (byte[] bytes : bytesArray) {
            if (bytes == null)
                throw new NullPointerException("Byte array to be concated cannot be null");
            length += bytes.length;
        }
        try (var os = new ByteArrayOutputStream(length)) {
            appendBytes(os, bytesArray);
            return os.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private static void appendBytes(ByteArrayOutputStream os, byte[]... bytesArray) throws IOException {
        for (byte[] bytes : bytesArray) {
            os.write(bytes);
        }
    }

    public static byte[] fromHex(String hex) {
        int start = (hex.startsWith("0x") || hex.startsWith("0X")) ? 2 : 0;
        int len = hex.length() - start;
        byte[] data = new byte[len / 2];
        for (int i = start; i < hex.length(); i += 2) {
            data[(i - start)
                    / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] leftTrimZero(byte[] bytes) {
        // left trim byte array
        int lastZeroIndex = -1;
        for (int j = 0; j < bytes.length; j++) {
            if (bytes[j] != (byte) 0)
                break;
            lastZeroIndex = j;
        }

        if (lastZeroIndex >= 0)
            return Arrays.copyOfRange(bytes, lastZeroIndex + 1, bytes.length);

        return bytes;
    }
}
