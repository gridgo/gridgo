package io.gridgo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.gridgo.utils.exception.UnsupportedTypeException;

public final class ByteArrayUtils {

	private ByteArrayUtils() {
		// private constructor
	}

	@SuppressWarnings("unchecked")
	public static final <T> T primitiveFromByteArray(Class<T> clazz, byte[] bytes) {
		if (clazz != null && bytes != null) {
			if (clazz == Boolean.class || clazz == Boolean.TYPE) {
				return (T) Boolean.valueOf(Long.valueOf(ByteBuffer.wrap(bytes).getLong()) != 0);
			} else if (clazz == Byte.class || clazz == Byte.TYPE) {
				return (T) Byte.valueOf(bytes.length > 0 ? bytes[0] : 0);
			} else if (clazz == Short.class || clazz == Short.TYPE) {
				return (T) Short.valueOf(ByteBuffer.wrap(bytes).getShort());
			} else if (clazz == Integer.class || clazz == Integer.TYPE) {
				return (T) Integer.valueOf(ByteBuffer.wrap(bytes).getInt());
			} else if (clazz == Float.class || clazz == Float.TYPE) {
				return (T) Float.valueOf(ByteBuffer.wrap(bytes).getFloat());
			} else if (clazz == Long.class || clazz == Long.TYPE) {
				return (T) Long.valueOf(ByteBuffer.wrap(bytes).getLong());
			} else if (clazz == Double.class || clazz == Double.TYPE) {
				return (T) Double.valueOf(ByteBuffer.wrap(bytes).getDouble());
			} else if (clazz == String.class) {
				return (T) new String(bytes);
			} else if (clazz == Character.class || clazz == Character.TYPE) {
				return (T) Character.valueOf(ByteBuffer.wrap(bytes).getChar());
			}
			throw new UnsupportedTypeException();
		}
		return null;
	}

	public static final String toHex(byte[] bytes, String prefix) {
		if (bytes != null) {
			StringBuilder buffer = new StringBuilder();
			if (prefix != null) {
				buffer.append(prefix);
			}
			for (int i = 0; i < bytes.length; i++) {
				buffer.append(Character.forDigit((bytes[i] >> 4) & 0xF, 16));
				buffer.append(Character.forDigit((bytes[i] & 0xF), 16));
			}
			return buffer.toString();
		}
		return null;
	}

	public static final String toHex(byte[] bytes) {
		return toHex(bytes, null);
	}

	public static final byte[] concat(byte[]... bytesArray) {
		if (bytesArray != null) {
			int length = 0;
			for (byte[] bytes : bytesArray) {
				if (bytes == null) {
					throw new NullPointerException("Byte array to be concated cannot be null");
				}
				length += bytes.length;
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream(length);
			for (byte[] bytes : bytesArray) {
				try {
					os.write(bytes);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return os.toByteArray();
		}
		return null;
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

}
