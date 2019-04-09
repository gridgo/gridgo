package io.gridgo.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.gridgo.utils.exception.UnsupportedTypeException;
import lombok.NonNull;

public class PrimitiveUtils {

    private static final String UNSUPPORTED_TYPE_MSG = "Unsupported type: ";

    private PrimitiveUtils() {
        // make constructor private to prevent other where create new instance
    }

    public static boolean isNumberClass(Class<?> clazz) {
        return clazz != null && Number.class.isAssignableFrom(clazz);
    }

    public static boolean isNumber(Object obj) {
        return obj != null && isNumberClass(obj.getClass());
    }

    public static final boolean isPrimitive(Class<?> resultType) {
        return (resultType == String.class //
                || isNumberClass(resultType) //
                || resultType == Character.TYPE || resultType == Character.class //
                || resultType == Boolean.TYPE || resultType == Boolean.class) //
                && !resultType.isArray();
    }

    @SuppressWarnings("unchecked")
    public static final <T> T getValueFrom(@NonNull Class<T> resultType, Object obj) {
        if (obj == null)
            return null;
        if (resultType.isAssignableFrom(obj.getClass()))
            return (T) obj;
        if (resultType == String.class)
            return (T) getStringValueFrom(obj);
        if (resultType == BigInteger.class)
            return (T) getBigIntegerFrom(obj);
        if (resultType == BigDecimal.class)
            return (T) getBigDecimalFrom(obj);
        if (resultType == Integer.TYPE || resultType == Integer.class)
            return (T) Integer.valueOf(getIntegerValueFrom(obj));
        if (resultType == Float.TYPE || resultType == Float.class)
            return (T) Float.valueOf(getFloatValueFrom(obj));
        if (resultType == Long.TYPE || resultType == Long.class)
            return (T) Long.valueOf(getLongValueFrom(obj));
        if (resultType == Double.TYPE || resultType == Double.class)
            return (T) Double.valueOf(getDoubleValueFrom(obj));
        if (resultType == Short.TYPE || resultType == Short.class)
            return (T) Short.valueOf(getShortValueFrom(obj));
        if (resultType == Byte.TYPE || resultType == Byte.class)
            return (T) Byte.valueOf(getByteValueFrom(obj));
        if (resultType == Character.TYPE || resultType == Character.class)
            return (T) Character.valueOf(getCharValueFrom(obj));
        if (resultType == Boolean.TYPE || resultType == Boolean.class)
            return (T) Boolean.valueOf(getBooleanValueFrom(obj));
        throw new UnsupportedTypeException(UNSUPPORTED_TYPE_MSG + resultType.getName());
    }

    private static BigDecimal getBigDecimalFrom(Object obj) {
        if (obj instanceof BigDecimal)
            return (BigDecimal) obj;
        if (obj instanceof Number)
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        if (obj instanceof byte[])
            return new BigDecimal(new BigInteger((byte[]) obj));
        return new BigDecimal(getStringValueFrom(obj));
    }

    private static BigInteger getBigIntegerFrom(Object obj) {
        if (obj instanceof BigInteger)
            return (BigInteger) obj;
        if (obj instanceof Number)
            return BigInteger.valueOf(((Number) obj).longValue());
        if (obj instanceof byte[])
            return new BigInteger((byte[]) obj);
        return new BigInteger(getStringValueFrom(obj));
    }

    public static final String getStringValueFrom(Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof String)
            return (String) obj;
        if (obj instanceof byte[])
            return new String((byte[]) obj);
        if (isPrimitive(obj.getClass()))
            return String.valueOf(obj);
        return obj.toString();
    }

    public static final int getIntegerValueFrom(@NonNull Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).intValue();
        if (obj instanceof Character)
            return ((Character) obj).charValue();
        if (obj instanceof String)
            return Integer.valueOf((String) obj);
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1 : 0;
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Integer.class, (byte[]) obj);
        throw new RuntimeException("cannot convert null object");
    }

    public static final long getLongValueFrom(@NonNull Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).longValue();
        if (obj instanceof Character)
            return (long) ((Character) obj).charValue();
        if (obj instanceof String)
            return Long.valueOf((String) obj);
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1l : 0l;
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Long.class, (byte[]) obj);
        throw new UnsupportedTypeException(UNSUPPORTED_TYPE_MSG + obj.getClass().getName());
    }

    public static final float getFloatValueFrom(@NonNull Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).floatValue();
        if (obj instanceof Character)
            return (float) ((Character) obj).charValue();
        if (obj instanceof String)
            return Float.valueOf((String) obj);
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1f : 0f;
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Float.class, (byte[]) obj);
        throw new UnsupportedTypeException(UNSUPPORTED_TYPE_MSG + obj.getClass().getName());
    }

    public static final double getDoubleValueFrom(@NonNull Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).doubleValue();
        if (obj instanceof Character)
            return (double) ((Character) obj).charValue();
        if (obj instanceof String)
            return Double.valueOf((String) obj);
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1d : 0d;
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Double.class, (byte[]) obj);
        throw new UnsupportedTypeException(UNSUPPORTED_TYPE_MSG + obj.getClass().getName());
    }

    public static final short getShortValueFrom(@NonNull Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).shortValue();
        if (obj instanceof Character)
            return (short) ((Character) obj).charValue();
        if (obj instanceof String)
            return Short.valueOf((String) obj);
        if (obj instanceof Boolean)
            return (short) ((Boolean) obj ? 1 : 0);
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Short.class, (byte[]) obj);
        throw new UnsupportedTypeException(UNSUPPORTED_TYPE_MSG + obj.getClass().getName());
    }

    public static final byte getByteValueFrom(@NonNull Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).byteValue();
        if (obj instanceof Character)
            return (byte) ((Character) obj).charValue();
        if (obj instanceof String)
            return Byte.valueOf((String) obj);
        if (obj instanceof Boolean)
            return (byte) ((Boolean) obj ? 1 : 0);
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Byte.class, (byte[]) obj);
        throw new UnsupportedTypeException(UNSUPPORTED_TYPE_MSG + obj.getClass().getName());
    }

    /**
     * return char value for specific obj <br>
     * if obj is number, return char represent by obj as UTF-16 code<br>
     * else if obj is boolean, return '0' for false, '1' for true
     * 
     * @param obj
     * @return char represented by input obj
     */
    public static final char getCharValueFrom(@NonNull Object obj) {
        if (obj instanceof Number)
            return Character.toChars(((Number) obj).intValue())[0];
        if (obj instanceof Character)
            return ((Character) obj).charValue();
        if (obj instanceof String) {
            if (((String) obj).length() > 0) {
                return ((String) obj).charAt(0);
            } else {
                return '\0';
            }
        }
        if (obj instanceof Boolean)
            return ((Boolean) obj ? '1' : '0');
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Character.class, (byte[]) obj);
        throw new UnsupportedTypeException(UNSUPPORTED_TYPE_MSG + obj.getClass().getName());
    }

    /**
     * return boolean value for specific obj <br>
     * if obj is number, return false if obj == 0, true for otherwise <br>
     * else if obj is character, return false if obj == '\0' char (null value), true
     * for otherwise <br>
     * else return object != null
     * 
     * @param obj
     * @return
     */
    public static final boolean getBooleanValueFrom(Object obj) {
        if (obj instanceof Number)
            return ((Number) obj).doubleValue() != 0;
        if (obj instanceof String)
            return Boolean.valueOf((String) obj);
        if (obj instanceof Character)
            return ((Character) obj).charValue() != '\0';
        if (obj instanceof Boolean)
            return ((Boolean) obj).booleanValue();
        if (obj instanceof byte[])
            return ByteArrayUtils.bytesToPrimitive(Boolean.class, (byte[]) obj);
        return obj != null;
    }
}
