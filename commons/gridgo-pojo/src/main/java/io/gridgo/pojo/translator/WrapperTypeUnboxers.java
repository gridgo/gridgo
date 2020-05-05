package io.gridgo.pojo.translator;

public class WrapperTypeUnboxers {

    @Unboxer
    public static final boolean unboxBoolean(Boolean value) {
        return value.booleanValue();
    }

    @Unboxer
    public static final char unboxCharacter(Character value) {
        return value.charValue();
    }

    @Unboxer
    public static final byte unboxByte(Byte value) {
        return value.byteValue();
    }

    @Unboxer
    public static final short unboxShort(Short value) {
        return value.shortValue();
    }

    @Unboxer
    public static final int unboxInteger(Integer value) {
        return value.intValue();
    }

    @Unboxer
    public static final long unboxLong(Long value) {
        return value.longValue();
    }

    @Unboxer
    public static final float unboxFloat(Float value) {
        return value.floatValue();
    }

    @Unboxer
    public static final double unboxDouble(Double value) {
        return value.doubleValue();
    }
}
